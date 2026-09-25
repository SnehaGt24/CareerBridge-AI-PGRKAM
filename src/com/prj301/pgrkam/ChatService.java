package com.prj301.pgrkam;

import java.util.*;

public class ChatService {
    public record ChatResponse(
        String message,
        String intent,
        double confidence,
        String serviceName,
        String description,
        String actionLabel,
        String modulePath,
        String secondaryActionLabel,
        String secondaryPage,
        List<IntentClassifier.Result> alternatives
    ) {}

    private final List<ServiceInfo> services;
    private final Map<String,ServiceInfo> byIntent = new HashMap<>();
    private final IntentClassifier classifier;

    public ChatService() {
        this.services = KnowledgeBase.services();
        for (ServiceInfo s : services) byIntent.put(s.intent(), s);
        this.classifier = new IntentClassifier(services, 0.20);
    }

    public ChatResponse chat(String message) {
        if (message == null || message.isBlank()) {
            ServiceInfo oos = byIntent.get("OUT_OF_SCOPE");
            return response("Please tell me what you need help with—for example, finding a job, choosing a career, improving your skills, preparing for an interview, or navigating a PGRKAM service.",
                    "OUT_OF_SCOPE", 0, oos, "", "", List.of());
        }

        // The classifier is intentionally supplemented with a few high-value
        // student scenarios so natural questions do not fall through to OOS.
        String normalized = message.toLowerCase(Locale.ROOT);
        String hintedIntent = intentHint(normalized);
        IntentClassifier.Prediction p = classifier.predict(message);
        if (hintedIntent != null && ("OUT_OF_SCOPE".equals(p.intent()) || p.confidence() < 0.48)) {
            p = new IntentClassifier.Prediction(hintedIntent, Math.max(0.78, p.confidence()), p.alternatives());
        }

        ServiceInfo service = byIntent.getOrDefault(p.intent(), byIntent.get("OUT_OF_SCOPE"));
        if ("OUT_OF_SCOPE".equals(service.intent())) {
            return response(
                "I’m here for employment, career planning and PGRKAM service navigation. Try asking about a job search, career choice, skill gap, resume, interview, recruiter outreach, job mela, government/private employment or another PGRKAM service.",
                service.intent(), p.confidence(), service, "", "", p.alternatives());
        }

        String response = tailoredResponse(normalized, service);
        String secondaryLabel = secondaryLabel(service.intent());
        String secondaryPage = secondaryPage(service.intent());
        return response(response, service.intent(), p.confidence(), service, secondaryLabel, secondaryPage, p.alternatives());
    }

    private ChatResponse response(String message, String intent, double confidence, ServiceInfo service,
                                  String secondaryLabel, String secondaryPage,
                                  List<IntentClassifier.Result> alternatives) {
        return new ChatResponse(message, intent, confidence, service.serviceName(), service.description(),
                service.actionLabel(), service.modulePath(), secondaryLabel, secondaryPage, alternatives);
    }

    private String tailoredResponse(String q, ServiceInfo s) {
        return switch (s.intent()) {
            case "PRIVATE_JOB" -> {
                if (q.contains("java") || q.contains("software") || q.contains("developer") || q.contains("cse")) {
                    yield "You can target entry-level roles such as Java Developer, Junior Software Developer, Backend Developer and Web Developer. Start by checking current PGRKAM vacancies, then use CareerBridge Opportunities to compare each role with your skills before applying.";
                }
                yield "Start with current private-sector vacancies on PGRKAM. After you find a role, CareerBridge can help you check role fit, identify skill gaps, tailor your resume and prepare recruiter outreach.";
            }
            case "CAREER_GUIDANCE", "COUNSELLING" -> {
                if (q.contains("spring boot") || q.contains("java developer")) {
                    yield "Java is a useful foundation for developer roles. If Spring Boot is new to you, focus next on Spring Boot fundamentals, REST APIs, SQL integration and one small backend project. PGRKAM also provides vocational guidance covering career planning, job avenues and training information.";
                }
                if (q.contains("three months") || q.contains("3 months") || q.contains("graduation") || q.contains("graduate")) {
                    yield "With about three months left, focus on a small number of target roles rather than applying randomly: complete your career profile, strengthen the highest-priority skills, tailor your resume, apply to relevant vacancies, contact recruiters through public channels and practise interviews. PGRKAM provides employment services and vocational guidance that can support this process.";
                }
                yield "PGRKAM provides vocational/career guidance covering career planning, job avenues and training information. Use the official guidance service first, then use CareerBridge to turn that direction into a practical job-search plan.";
            }
            case "GOVT_JOB" -> "Use PGRKAM's employment services to check current vacancy information and eligibility details for government/public-sector opportunities. Always verify the vacancy notice and deadline before applying.";
            case "SELF_EMPLOYMENT" -> "PGRKAM supports self-employment and entrepreneurship guidance. Start with the official service information, then use CareerBridge to organise your goals, skills and next steps.";
            case "FOREIGN_JOB" -> "PGRKAM provides overseas-employment information and support. Start with the official overseas employment information, verify the employer or recruiting channel, and do not pay unverified recruitment fees.";
            case "FOREIGN_STUDY" -> "PGRKAM provides information related to overseas study and employment services. Start with the official portal and verify institution, eligibility, fees and application requirements independently.";
            case "JOB_MELA" -> "PGRKAM publishes employment-service information and recruitment activities. Open the official service page to check current job-mela or recruitment-drive information and eligibility.";
            case "ARMED_FORCES" -> "PGRKAM provides information related to armed-forces career opportunities. Open the official information and verify the current recruitment notification and eligibility before applying.";
            default -> s.description();
        };
    }

    private String secondaryLabel(String intent) {
        return switch (intent) {
            case "PRIVATE_JOB", "GOVT_JOB", "JOB_MELA" -> "Open CareerBridge Opportunities";
            case "CAREER_GUIDANCE", "COUNSELLING" -> "Build a Career Plan";
            case "SELF_EMPLOYMENT" -> "Open CareerBridge Career Plan";
            case "FOREIGN_JOB", "FOREIGN_STUDY" -> "Open CareerBridge Resources";
            default -> "Open CareerBridge";
        };
    }

    private String secondaryPage(String intent) {
        return switch (intent) {
            case "PRIVATE_JOB", "GOVT_JOB", "JOB_MELA" -> "opportunities";
            case "CAREER_GUIDANCE", "COUNSELLING" -> "dashboard";
            case "SELF_EMPLOYMENT", "FOREIGN_JOB", "FOREIGN_STUDY", "ARMED_FORCES" -> "resources";
            default -> "dashboard";
        };
    }

    private String intentHint(String q) {
        if (q.matches(".*\\b(cricket|football|weather|movie|movies|song|songs|restaurant|joke|capital|calculator|calculate|math)\\b.*")) return "OUT_OF_SCOPE";
        if ((q.contains("java") || q.contains("software developer") || q.contains("software job") || q.contains("cse student"))
                && (q.contains("job") || q.contains("jobs") || q.contains("target") || q.contains("role") || q.contains("roles"))) return "PRIVATE_JOB";
        if (q.contains("spring boot") || q.contains("career") || q.contains("three months") || q.contains("3 months")
                || q.contains("graduation") || q.contains("graduate") || q.contains("what should i do")) return "CAREER_GUIDANCE";
        if (q.contains("government job") || q.contains("govt job") || q.contains("public sector") || q.contains("government recruitment")) return "GOVT_JOB";
        if (q.contains("job mela") || q.contains("job fair") || q.contains("recruitment drive")) return "JOB_MELA";
        if (q.contains("self employment") || q.contains("start my own business") || q.contains("entrepreneur")) return "SELF_EMPLOYMENT";
        if (q.contains("job abroad") || q.contains("work abroad") || q.contains("overseas employment")) return "FOREIGN_JOB";
        if (q.contains("study abroad") || q.contains("masters abroad") || q.contains("study overseas")) return "FOREIGN_STUDY";
        return null;
    }

    public List<ServiceInfo> services() { return services; }
    public IntentClassifier classifier() { return classifier; }
}
