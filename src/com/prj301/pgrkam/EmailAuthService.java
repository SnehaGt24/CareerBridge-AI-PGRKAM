package com.prj301.pgrkam;

import com.sun.net.httpserver.HttpExchange;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/** Simple email-based sign-in for the local/demo CareerBridge app. */
public final class EmailAuthService {
    public record User(String email, String name) {}
    private record Session(User user, long expiresAt) {}

    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)+$");
    private final SecureRandom random = new SecureRandom();
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public boolean isValidEmail(String email) {
        if (email == null || email.length() > 254 || !EMAIL.matcher(email.trim()).matches()) return false;
        // CareerBridge demo policy requested by the project owner: accept .com addresses only.
        // This checks the address format/domain suffix, not ownership of the mailbox.
        return email.trim().toLowerCase(java.util.Locale.ROOT).matches(".*\\.com$");
    }

    public String createSession(String email) {
        String clean = email.trim().toLowerCase();
        String name = clean.substring(0, clean.indexOf('@')).replace('.', ' ');
        name = name.isBlank() ? "Candidate" : Character.toUpperCase(name.charAt(0)) + name.substring(1);
        String id = token();
        sessions.put(id, new Session(new User(clean, name), Instant.now().toEpochMilli() + 8 * 60 * 60_000L));
        return id;
    }

    public User current(HttpExchange ex) {
        String id = cookieValue(ex.getRequestHeaders().getFirst("Cookie"), "CB_SESSION");
        if (id == null) return null;
        Session s = sessions.get(id);
        if (s == null || s.expiresAt() < Instant.now().toEpochMilli()) {
            sessions.remove(id);
            return null;
        }
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
        return name + "=" + value + "; Max-Age=" + maxAge + "; Path=/; HttpOnly; SameSite=Lax";
    }

    private String token() {
        byte[] b = new byte[32];
        random.nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }

    private static String cookieValue(String cookie, String name) {
        if (cookie == null) return null;
        for (String p : cookie.split(";")) {
            String[] x = p.trim().split("=", 2);
            if (x.length == 2 && x[0].equals(name)) return x[1];
        }
        return null;
    }
}
