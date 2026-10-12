package com.prj301.pgrkam;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.Executors;

public class Main {
    private static final int PORT=Integer.parseInt(System.getProperty("port","8080"));
    private static final ChatService CHAT=new ChatService();
    private static final JobMatchService JOBS=new JobMatchService();
    private static final ResumeMatchService RESUME_MATCH=new ResumeMatchService();
    private static final StudyAbroadService STUDY=new StudyAbroadService();
    private static final ResumeReviewService RESUMES=new ResumeReviewService();
    private static final RecruitmentService RECRUITMENT=new RecruitmentService();
    private static final CareerPortalService PORTAL=new CareerPortalService();
    private static final EmployerDirectoryService EMPLOYERS=new EmployerDirectoryService();
    private static final EmailAuthService AUTH=new EmailAuthService();
    private static Path webRoot;

    public static void main(String[] args)throws Exception{
        if(args.length>0&&"--evaluate".equalsIgnoreCase(args[0])){evaluate();return;}
        webRoot=locateWebRoot();
        HttpServer server=HttpServer.create(new InetSocketAddress(PORT),0);
        server.createContext("/api/health",ex->sendJson(ex,200,"{\"status\":\"ok\",\"project\":\"PRJ_301\"}"));
        server.createContext("/api/auth/me",Main::handleAuthMe);
        server.createContext("/auth/email",Main::handleEmailLogin);
        server.createContext("/auth/logout",Main::handleLogout);
        server.createContext("/api/intents",ex->{if(!"GET".equals(ex.getRequestMethod())){sendJson(ex,405,"{\"error\":\"Method not allowed\"}");return;}sendJson(ex,200,JsonUtil.services(CHAT.services()));});
        server.createContext("/api/chat",Main::handleChat);
        server.createContext("/api/job-match",Main::handleJobMatch);
        server.createContext("/api/study-abroad",Main::handleStudyAbroad);
        server.createContext("/api/resume-review",Main::handleResumeReview);
        server.createContext("/api/resume-match",Main::handleResumeMatch);
        server.createContext("/api/hr-contact",Main::handleHrContact);
        server.createContext("/api/interview",Main::handleInterview);
        server.createContext("/api/application",Main::handleApplication);
        server.createContext("/api/hr-directory",Main::handleHrDirectory);
        server.createContext("/",Main::serveStatic);
        server.setExecutor(Executors.newFixedThreadPool(Math.max(4,Runtime.getRuntime().availableProcessors())));
        server.start();
        System.out.println("CareerBridge AI — PGRKAM Career Copilot is running.");
        System.out.println("Open: http://localhost:"+PORT);
        System.out.println("Modules: My Career | AI Copilot | Opportunities | Passport | Applications | Recruiter | Resume | Skill Plan | Interview | PGRKAM Resources");
    }

    private static void handleChat(HttpExchange ex)throws IOException{
        if(!requireAuth(ex))return;
        if(!"POST".equals(ex.getRequestMethod())){sendJson(ex,405,"{\"error\":\"Use POST\"}");return;}
        sendJson(ex,200,JsonUtil.chatResponse(CHAT.chat(JsonUtil.extractMessage(readBody(ex)))));
    }
    private static void handleJobMatch(HttpExchange ex)throws IOException{
        if(!requireAuth(ex))return;
        if(!"POST".equals(ex.getRequestMethod())){sendJson(ex,405,"{\"error\":\"Use POST\"}");return;}
        String b=readBody(ex);
        sendJson(ex,200,JsonUtil.jobMatches(JOBS.match(JsonUtil.extractString(b,"degree"),JsonUtil.extractString(b,"skills"),JsonUtil.extractString(b,"interests"),JsonUtil.extractString(b,"preferredRole"),JsonUtil.extractString(b,"location"),JsonUtil.extractString(b,"workMode"))));
    }
    private static void handleResumeReview(HttpExchange ex)throws IOException{
        if(!requireAuth(ex))return;
        if(!"POST".equals(ex.getRequestMethod())){sendJson(ex,405,"{\"error\":\"Use POST\"}");return;}String b=readBody(ex);
        sendJson(ex,200,JsonUtil.resumeReview(RESUMES.review(JsonUtil.extractString(b,"resumeText"),JsonUtil.extractString(b,"targetRole"))));
    }
    private static void handleResumeMatch(HttpExchange ex)throws IOException{
        if(!requireAuth(ex))return;
        if(!"POST".equals(ex.getRequestMethod())){sendJson(ex,405,"{\"error\":\"Use POST\"}");return;}
        String b=readBody(ex);
        String resume=JsonUtil.extractString(b,"resumeText");
        if(resume.isBlank()){sendJson(ex,400,"{\"error\":\"Upload or paste a résumé before matching.\"}");return;}
        var result=RESUME_MATCH.analyze(resume,JsonUtil.extractString(b,"preferredLocation"),JsonUtil.extractString(b,"workMode"),JOBS);
        sendJson(ex,200,JsonUtil.resumeMatch(result));
    }
    private static void handleHrContact(HttpExchange ex)throws IOException{
        if(!requireAuth(ex))return;
        if(!"POST".equals(ex.getRequestMethod())){sendJson(ex,405,"{\"error\":\"Use POST\"}");return;}String b=readBody(ex);
        String email=JsonUtil.extractString(b,"email");if(!email.contains("@")){sendJson(ex,400,"{\"error\":\"Enter a valid email address\"}");return;}
        sendJson(ex,201,JsonUtil.contact(RECRUITMENT.contact(JsonUtil.extractString(b,"candidateName"),email,JsonUtil.extractString(b,"role"),JsonUtil.extractString(b,"message"))));
    }
    private static void handleInterview(HttpExchange ex)throws IOException{
        if(!requireAuth(ex))return;
        if(!"POST".equals(ex.getRequestMethod())){sendJson(ex,405,"{\"error\":\"Use POST\"}");return;}String b=readBody(ex);
        sendJson(ex,201,JsonUtil.interview(RECRUITMENT.schedule(JsonUtil.extractString(b,"candidateName"),JsonUtil.extractString(b,"candidateEmail"),JsonUtil.extractString(b,"hrName"),JsonUtil.extractString(b,"scheduledAt"),JsonUtil.extractString(b,"meetUrl"),JsonUtil.extractString(b,"notes"))));
    }
    private static void handleApplication(HttpExchange ex)throws IOException{
        if(!requireAuth(ex))return;
        if(!"POST".equals(ex.getRequestMethod())){sendJson(ex,405,"{\"error\":\"Use POST\"}");return;}String b=readBody(ex);
        String email=JsonUtil.extractString(b,"email");if(!email.contains("@")){sendJson(ex,400,"{\"error\":\"Enter a valid candidate email\"}");return;}
        sendJson(ex,201,JsonUtil.application(PORTAL.add(JsonUtil.extractString(b,"candidateName"),email,JsonUtil.extractString(b,"role"),JsonUtil.extractString(b,"company"),JsonUtil.extractString(b,"location"),JsonUtil.extractString(b,"status"),JsonUtil.extractString(b,"notes"))));
    }
    private static void handleHrDirectory(HttpExchange ex)throws IOException{
        if(!requireAuth(ex))return;
        if(!"GET".equals(ex.getRequestMethod())){sendJson(ex,405,"{\"error\":\"Use GET\"}");return;}
        sendJson(ex,200,JsonUtil.employerContacts(EMPLOYERS.contacts()));
    }
    private static void handleStudyAbroad(HttpExchange ex)throws IOException{
        if(!requireAuth(ex))return;
        if(!"POST".equals(ex.getRequestMethod())){sendJson(ex,405,"{\"error\":\"Use POST\"}");return;}
        String b=readBody(ex);
        sendJson(ex,200,JsonUtil.studyPlan(STUDY.plan(JsonUtil.extractString(b,"country"),JsonUtil.extractString(b,"course"),JsonUtil.extractDouble(b,"cgpa",0),JsonUtil.extractString(b,"budget"),JsonUtil.extractString(b,"intake"))));
    }

    private static boolean requireAuth(HttpExchange ex)throws IOException{
        if(AUTH.current(ex)!=null)return true;
        sendJson(ex,401,"{\"error\":\"Authentication required.\"}");
        return false;
    }
    private static void handleAuthMe(HttpExchange ex)throws IOException{
        if(!"GET".equals(ex.getRequestMethod())){sendJson(ex,405,"{\"error\":\"Use GET\"}");return;}
        EmailAuthService.User u=AUTH.current(ex);
        if(u==null){sendJson(ex,401,"{\"authenticated\":false}");return;}
        StringBuilder b=new StringBuilder("{\"authenticated\":true,");
        field(b,"email",u.email()).append(',');field(b,"name",u.name()).append(',');field(b,"picture","");b.append('}');
        sendJson(ex,200,b.toString());
    }
    private static void handleEmailLogin(HttpExchange ex)throws IOException{
        if(!"POST".equals(ex.getRequestMethod())){sendJson(ex,405,"{\"error\":\"Use POST\"}");return;}
        String email=JsonUtil.extractString(readBody(ex),"email").trim();
        if(!AUTH.isValidEmail(email)){sendJson(ex,400,"{\"error\":\"Please enter a valid .com email address. Email ownership must be verified separately.\"}");return;}
        String sid=AUTH.createSession(email);
        AUTH.setSessionCookie(ex,sid);
        sendJson(ex,200,"{\"authenticated\":true}");
    }
    private static void handleLogout(HttpExchange ex)throws IOException{AUTH.logout(ex);redirect(ex,"/");}
    private static String query(String q,String key){if(q==null)return "";for(String p:q.split("&")){String[] x=p.split("=",2);if(x.length==2&&java.net.URLDecoder.decode(x[0],StandardCharsets.UTF_8).equals(key))return java.net.URLDecoder.decode(x[1],StandardCharsets.UTF_8);}return "";}
    private static void redirect(HttpExchange ex,String location)throws IOException{ex.getResponseHeaders().set("Location",location);ex.sendResponseHeaders(302,-1);ex.close();}
    private static StringBuilder field(StringBuilder sb,String k,String v){return sb.append('\"').append(k).append("\":\"").append(v==null?"":v.replace("\\","\\\\").replace("\"","\\\"")).append('\"');}
    private static String readBody(HttpExchange ex)throws IOException{return new String(ex.getRequestBody().readAllBytes(),StandardCharsets.UTF_8);}
    private static void serveStatic(HttpExchange ex)throws IOException{
        String path=ex.getRequestURI().getPath();if("/".equals(path))path="/index.html";
        Path requested=webRoot.resolve(path.substring(1)).normalize();
        if(!requested.startsWith(webRoot)||!Files.exists(requested)||Files.isDirectory(requested)){byte[] b="Not found".getBytes(StandardCharsets.UTF_8);ex.sendResponseHeaders(404,b.length);ex.getResponseBody().write(b);ex.close();return;}
        ex.getResponseHeaders().set("Content-Type",contentType(requested.getFileName().toString()));ex.getResponseHeaders().set("Cache-Control","no-store");
        byte[] b=Files.readAllBytes(requested);ex.sendResponseHeaders(200,b.length);ex.getResponseBody().write(b);ex.close();
    }
    private static Path locateWebRoot(){
        Path cwd=Paths.get(System.getProperty("user.dir"));if(Files.isDirectory(cwd.resolve("web")))return cwd.resolve("web").toAbsolutePath();
        if(Files.isDirectory(cwd.resolve("pgrkam-smart-assistant/web")))return cwd.resolve("pgrkam-smart-assistant/web").toAbsolutePath();
        throw new IllegalStateException("Cannot find web/ folder.");
    }
    private static String contentType(String n){if(n.endsWith(".html"))return"text/html; charset=utf-8";if(n.endsWith(".css"))return"text/css; charset=utf-8";if(n.endsWith(".js")||n.endsWith(".mjs"))return"application/javascript; charset=utf-8";if(n.endsWith(".json"))return"application/json; charset=utf-8";return"application/octet-stream";}
    private static void sendJson(HttpExchange ex,int status,String json)throws IOException{byte[] b=json.getBytes(StandardCharsets.UTF_8);ex.getResponseHeaders().set("Content-Type","application/json; charset=utf-8");ex.sendResponseHeaders(status,b.length);ex.getResponseBody().write(b);ex.close();}
    private static void evaluate(){
        record T(String q,String e){} List<T> ts=List.of(new T("I want a private software developer role","PRIVATE_JOB"),new T("government recruitment vacancies","GOVT_JOB"),new T("I want a job abroad","FOREIGN_JOB"),new T("I want to do masters overseas","FOREIGN_STUDY"),new T("join the army","ARMED_FORCES"),new T("recommend a movie","OUT_OF_SCOPE"));
        int c=0;for(T t:ts){var p=CHAT.classifier().predict(t.q());boolean ok=t.e().equals(p.intent());if(ok)c++;System.out.printf("%-42s %-16s %-16s %.3f %s%n",t.q(),t.e(),p.intent(),p.confidence(),ok?"✓":"✗");}
        System.out.printf("%nDemo intent accuracy: %.1f%% (%d/%d)%n",100.0*c/ts.size(),c,ts.size());
    }
}
