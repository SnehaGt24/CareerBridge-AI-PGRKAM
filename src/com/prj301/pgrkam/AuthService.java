package com.prj301.pgrkam;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.HashMap;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

/** Google OAuth 2.0 authorization-code flow with server-side sessions. */
public final class AuthService {
    private static final Map<String,String> DOTENV = loadDotEnv();
    public record User(String sub, String email, String name, String picture) {}
    private record Pending(String state, long createdAt) {}
    private record Session(User user, long expiresAt) {}

    private final String clientId = env("GOOGLE_CLIENT_ID");
    private final String clientSecret = env("GOOGLE_CLIENT_SECRET");
    private final String redirectUri = envOr("GOOGLE_REDIRECT_URI", "http://localhost:8080/auth/google/callback");
    private final boolean secureCookies = Boolean.parseBoolean(envOr("SECURE_COOKIES", "false"));
    private final SecureRandom random = new SecureRandom();
    private final Map<String, Pending> states = new ConcurrentHashMap<>();
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private final HttpClient http = HttpClient.newHttpClient();

    public boolean configured() { return !clientId.isBlank() && !clientSecret.isBlank(); }

    public String loginUrl() throws IOException {
        if (!configured()) throw new IOException("Google authentication is not configured. Set GOOGLE_CLIENT_ID and GOOGLE_CLIENT_SECRET.");
        String state = token();
        states.put(state, new Pending(state, Instant.now().toEpochMilli()));
        cleanup();
        return "https://accounts.google.com/o/oauth2/v2/auth?" +
                "client_id=" + enc(clientId) +
                "&redirect_uri=" + enc(redirectUri) +
                "&response_type=code&scope=" + enc("openid email profile") +
                "&access_type=online&prompt=select_account&state=" + enc(state);
    }

    public User callback(String code, String state) throws Exception {
        if (!configured()) throw new IOException("Google authentication is not configured.");
        Pending pending = states.remove(state);
        if (state.isBlank() || pending == null || Instant.now().toEpochMilli() - pending.createdAt() > 5 * 60_000L)
            throw new IOException("Invalid or expired authentication state.");

        String form = "code=" + enc(code) + "&client_id=" + enc(clientId) + "&client_secret=" + enc(clientSecret) +
                "&redirect_uri=" + enc(redirectUri) + "&grant_type=authorization_code";
        HttpRequest tokenReq = HttpRequest.newBuilder(URI.create("https://oauth2.googleapis.com/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form)).build();
        HttpResponse<String> tokenRes = http.send(tokenReq, HttpResponse.BodyHandlers.ofString());
        if (tokenRes.statusCode() != 200) throw new IOException("Google token exchange failed.");
        String accessToken = JsonUtil.extractString(tokenRes.body(), "access_token");
        if (accessToken.isBlank()) throw new IOException("Google did not return an access token.");

        HttpRequest userReq = HttpRequest.newBuilder(URI.create("https://openidconnect.googleapis.com/v1/userinfo"))
                .header("Authorization", "Bearer " + accessToken).GET().build();
        HttpResponse<String> userRes = http.send(userReq, HttpResponse.BodyHandlers.ofString());
        if (userRes.statusCode() != 200) throw new IOException("Google user profile lookup failed.");
        User user = new User(JsonUtil.extractString(userRes.body(), "sub"),
                JsonUtil.extractString(userRes.body(), "email"),
                JsonUtil.extractString(userRes.body(), "name"),
                JsonUtil.extractString(userRes.body(), "picture"));
        if (user.sub().isBlank() || user.email().isBlank()) throw new IOException("Google account information was incomplete.");
        return user;
    }

    public String createSession(User user) {
        String id = token();
        sessions.put(id, new Session(user, Instant.now().toEpochMilli() + 8 * 60 * 60_000L));
        return id;
    }

    public User current(HttpExchange ex) {
        String cookie = ex.getRequestHeaders().getFirst("Cookie");
        String id = cookieValue(cookie, "CB_SESSION");
        if (id == null) return null;
        Session s = sessions.get(id);
        if (s == null || s.expiresAt() < Instant.now().toEpochMilli()) { sessions.remove(id); return null; }
        return s.user();
    }

    public void logout(HttpExchange ex) {
        String id = cookieValue(ex.getRequestHeaders().getFirst("Cookie"), "CB_SESSION");
        if (id != null) sessions.remove(id);
        ex.getResponseHeaders().add("Set-Cookie", cookie("CB_SESSION", "", 0));
    }

    public void setSessionCookie(HttpExchange ex, String id) {
        ex.getResponseHeaders().add("Set-Cookie", cookie("CB_SESSION", id, 8 * 60 * 60));
    }

    private String cookie(String name, String value, int maxAge) {
        return name + "=" + value + "; Max-Age=" + maxAge + "; Path=/; HttpOnly; SameSite=Lax" + (secureCookies ? "; Secure" : "");
    }
    private void cleanup() { long now=Instant.now().toEpochMilli(); states.entrySet().removeIf(e -> now-e.getValue().createdAt()>5*60_000L); sessions.entrySet().removeIf(e -> now-e.getValue().expiresAt()>0); }
    private String token() { byte[] b=new byte[32]; random.nextBytes(b); return Base64.getUrlEncoder().withoutPadding().encodeToString(b); }
    private static String cookieValue(String cookie, String name) { if(cookie==null)return null; for(String p:cookie.split(";")){String[] x=p.trim().split("=",2);if(x.length==2&&x[0].equals(name))return x[1];}return null; }
    private static String enc(String s){return URLEncoder.encode(s, StandardCharsets.UTF_8);}
    private static String env(String k){
        String v=System.getenv(k);
        if(v!=null&&!v.isBlank())return v.trim();
        v=DOTENV.get(k);
        return v==null?"":v.trim();
    }
    private static Map<String,String> loadDotEnv(){
        Map<String,String> out=new HashMap<>();
        try{
            Path p=Paths.get(".env");
            if(!Files.exists(p))return out;
            for(String line:Files.readAllLines(p)){
                String t=line.trim();
                if(t.isEmpty()||t.startsWith("#"))continue;
                if(t.startsWith("export "))t=t.substring(7).trim();
                int eq=t.indexOf('=');
                if(eq<=0)continue;
                String key=t.substring(0,eq).trim();
                String value=t.substring(eq+1).trim();
                if(value.length()>=2&&((value.startsWith("\"")&&value.endsWith("\""))||(value.startsWith("'")&&value.endsWith("'"))))value=value.substring(1,value.length()-1);
                out.put(key,value);
            }
        }catch(Exception ignored){}
        return out;
    }
    private static String envOr(String k,String d){String v=env(k);return v.isBlank()?d:v;}
}
