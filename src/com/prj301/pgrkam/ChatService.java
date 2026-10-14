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

        // Resolve explicit navigation/workflow requests before similarity scoring.
        // This prevents clear resume/study/job prompts from falling into generic guidance.
        String normalized = message.toLowerCase(Locale.ROOT).replace('’', '\'');
        String directIntent = directIntent(normalized);
        if ("RESUME_MATCH".equals(directIntent)) {
            return new ChatResponse(
                "I’ll take you to Resume Tailor so you can upload or paste your résumé, review it, and match it against suitable entry-level roles. Add your preferred location to prioritise nearby or remote-compatible roles.",
                "RESUME_MATCH", 0.99, "Resume Matching", "Upload or paste a résumé to extract skills and compare role fit.",
                "", "", "Open Resume Matching", "resume", List.of());
        }
        if ("RESUME_REVIEW".equals(directIntent)) {
            return new ChatResponse(
                "I’ll open Resume Tailor, where you can review your résumé and get practical improvements for a target role.",
                "RESUME_REVIEW", 0.99, "Resume Tailor", "Review and improve your résumé.",
                "", "", "Open Resume Tailor", "resume", List.of());
        }
        IntentClassifier.Prediction p = classifier.predict(message);
        String hintedIntent = directIntent != null ? directIntent : intentHint(normalized);
        if (hintedIntent != null && ("OUT_OF_SCOPE".equals(p.intent()) || p.confidence() < 0.48 || isStrongNavigationHint(normalized, hintedIntent))) {
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
        if ((normalized.contains("resume") || normalized.contains("résumé")) && (normalized.contains("job") || normalized.contains("jobs") || normalized.contains("role") || normalized.contains("roles"))) { secondaryLabel = "Upload résumé + match jobs"; secondaryPage = "resume"; }
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
                    if (q.contains("python") && (q.contains("java") || q.contains("developer role") || q.contains("developer roles"))) {
                    yield "With Python and web skills, consider Junior Python Developer, Backend Developer, QA Automation Intern and Junior Web Developer roles. You do not need to learn Java first for every role; choose one target stack, build a project, and compare each vacancy’s requirements. I’m opening PGRKAM’s private-vacancy search.";
                }
                if (q.contains("internship") || q.contains("intern")) {
                    yield "For a Python development internship, target Python Developer Intern, Backend Intern, Automation Intern and Data/QA Intern roles. Highlight Python projects, Git and APIs on your résumé, and verify the internship is current before applying. I’m opening PGRKAM’s private-sector vacancy search.";
                }
                yield "You can target entry-level roles such as Junior Software Developer, Java Developer, Backend Developer and Web Developer. I’m opening PGRKAM’s private-vacancy search for software developer roles. Check each listing for current availability, eligibility and deadlines, then use CareerBridge Opportunities to compare roles with your skills.";
                }
                yield "I’m opening PGRKAM’s private-sector vacancy search. Review the current listings and verify the employer, eligibility and deadline. After you find a role, CareerBridge can help you check role fit, identify skill gaps, tailor your resume and prepare recruiter outreach.";
            }
            case "CAREER_GUIDANCE" -> {
                if (q.contains("spring boot") || q.contains("java developer")) {
                    yield "Java is a useful foundation for developer roles. If Spring Boot is new to you, focus next on Spring Boot fundamentals, REST APIs, SQL integration and one small backend project. PGRKAM also provides vocational guidance covering career planning, job avenues and training information.";
                }
                if (q.contains("three months") || q.contains("3 months") || q.contains("graduation") || q.contains("graduate")) {
                    yield "With about three months left, focus on a small number of target roles rather than applying randomly: complete your career profile, strengthen the highest-priority skills, tailor your resume, apply to relevant vacancies, contact recruiters through public channels and practise interviews. PGRKAM provides employment services and vocational guidance that can support this process.";
                }
                if (q.contains("project") && (q.contains("resume") || q.contains("fresher"))) {
                    yield "For a fresher software résumé, build two or three finished projects that show practical skills: a full-stack job portal with authentication and search, a REST API with a database and tests, and a Python automation or data project. Add a clear README, screenshots, setup steps and what you personally implemented. I’m opening CareerBridge’s skill-planning workflow and PGRKAM’s official career-guidance information.";
                }
                if (q.contains("python") && q.contains("java")) {
                    yield "You can start with Junior Python Developer, Backend Developer, QA Automation Intern or Junior Web Developer roles if your Python/web fundamentals are stronger. Java is useful for Java-specific openings but is not a prerequisite for every developer job. PGRKAM’s vocational guidance page covers career planning, job avenues and training information.";
                }
                yield "PGRKAM’s vocational guidance service covers career planning, courses, training options and job avenues. I’m opening the official guidance page; use CareerBridge’s Skill Plan to turn your chosen direction into weekly learning and project steps.";
            }
            case "COUNSELLING" -> "I’m opening PGRKAM’s Counselling Sessions page directly. Check the available sessions and registration details on the official portal.";
            case "GOVT_JOB" -> "I’m opening PGRKAM’s Government Jobs page directly. Review each official notification, eligibility criteria and application deadline before applying.";
            case "SELF_EMPLOYMENT" -> "I’m opening PGRKAM’s Self-Employment page directly. Review the available entrepreneurship and self-employment support, scheme requirements and official application instructions.";
            case "WOMEN_JOBS" -> "I’m opening PGRKAM’s job search filtered for women candidates. Check each listing’s eligibility, location, application deadline and employer details before applying.";
            case "PWD_JOBS" -> "I’m opening PGRKAM’s job search for persons with disabilities. Check the post’s disability eligibility, required documentation, role requirements and deadline before applying.";
            case "PM_VIKAS" -> "I’m opening PGRKAM’s PM VIKAS Scheme page. Review the scheme details, eligibility and application instructions on the official portal.";
            case "MBA_FPI_GIRLS" -> "I’m opening the MBA FPI Girls programme website you provided. Check the current admission notice, eligibility, fees and deadline directly with the programme.";
            case "FOREIGN_JOB" -> "PGRKAM provides overseas-employment information and support. Start with the official overseas employment information, verify the employer or recruiting channel, and do not pay unverified recruitment fees.";
            case "FOREIGN_STUDY" -> "I’m opening PGRKAM’s foreign-study counselling information and CareerBridge Study Abroad Match. Enter your country, course, CGPA and budget to shortlist options, then verify exact programme eligibility, documents, fees, deadlines and visa rules on official university/government pages.";
            case "JOB_MELA" -> "PGRKAM publishes employment-service information and recruitment activities. Open the official service page to check current job-mela or recruitment-drive information and eligibility.";
            case "ARMED_FORCES" -> "PGRKAM provides information related to armed-forces career opportunities. Open the official information and verify the current recruitment notification and eligibility before applying.";
            default -> s.description();
        };
    }

    private String secondaryLabel(String intent) {
        return switch (intent) {
            case "PRIVATE_JOB" -> "Open CareerBridge Opportunities";
            case "GOVT_JOB", "JOB_MELA" -> "Open CareerBridge Opportunities";
            case "CAREER_GUIDANCE", "COUNSELLING" -> "Open CareerBridge Skill Plan";
            case "SELF_EMPLOYMENT" -> "Open CareerBridge Career Plan";
            case "WOMEN_JOBS", "PWD_JOBS", "PM_VIKAS", "MBA_FPI_GIRLS" -> "Open CareerBridge Resources";
            case "FOREIGN_JOB" -> "Open CareerBridge Resources";
            case "FOREIGN_STUDY" -> "Open Study Abroad Match";
            default -> "Open CareerBridge";
        };
    }

    private String secondaryPage(String intent) {
        return switch (intent) {
            case "PRIVATE_JOB", "GOVT_JOB", "JOB_MELA" -> "opportunities";
            case "CAREER_GUIDANCE", "COUNSELLING" -> "skills";
            case "SELF_EMPLOYMENT", "FOREIGN_JOB", "ARMED_FORCES", "WOMEN_JOBS", "PWD_JOBS", "PM_VIKAS", "MBA_FPI_GIRLS" -> "resources";
            case "FOREIGN_STUDY" -> "study";
            default -> "dashboard";
        };
    }

    private String directIntent(String q) {
        if (q.contains("pm vikas")) return "PM_VIKAS";
        if (q.contains("mba fpi") || q.contains("mbafpi") || (q.contains("food processing") && q.contains("girls") && q.contains("mba"))) return "MBA_FPI_GIRLS";
        if ((q.contains("person with disabilit") || q.contains("persons with disabilit") || q.contains("pwd job") || q.contains("pwd jobs") || q.contains("disabled candidate") || q.contains("disability job"))) return "PWD_JOBS";
        if ((q.contains("jobs for women") || q.contains("job for women") || q.contains("women job") || q.contains("jobs for girls") || q.contains("female candidates"))) return "WOMEN_JOBS";
        if (q.contains("self-employment") || q.contains("self employment") || q.contains("start my own business") || q.contains("entrepreneur")) return "SELF_EMPLOYMENT";
        if ((q.contains("counselling session") || q.contains("counseling session") || q.contains("book counselling") || q.contains("book counseling") || q.contains("career counsellor appointment") || q.contains("career counselor appointment"))) return "COUNSELLING";
        if (q.contains("project") && (q.contains("fresher") || q.contains("resume") || q.contains("résumé"))) return "CAREER_GUIDANCE";
        boolean resume = q.contains("resume") || q.contains("résumé");
        if (resume && (q.contains("matching") || q.contains("match") || q.contains("upload") || q.contains("suitable jobs") || q.contains("job matching"))) return "RESUME_MATCH";
        if (resume && (q.contains("improv") || q.contains("review") || q.contains("strengthen") || q.contains("better") || q.contains("feedback"))) return "RESUME_REVIEW";
        if ((q.contains("study abroad") || q.contains("study overseas") || q.contains("masters") || q.contains("master's") || q.contains("master degree") || q.contains("admission requirement") || q.contains("universit"))
                && !q.contains("job abroad") && !q.contains("work abroad")) return "FOREIGN_STUDY";
        if ((q.contains("internship") || q.contains("intern")) && (q.contains("python") || q.contains("developer") || q.contains("software") || q.contains("programming"))) return "PRIVATE_JOB";
        if ((q.contains("python") && q.contains("java") && (q.contains("developer") || q.contains("role"))) ||
                (q.contains("software developer") && (q.contains("fresher") || q.contains("entry-level") || q.contains("entry level")))) return "PRIVATE_JOB";
        if (q.contains("government job") || q.contains("government jobs") || q.contains("govt job") || q.contains("government vacancy") || q.contains("government recruitment") || q.contains("govt-jobs")) return "GOVT_JOB";
        if (q.contains("private job") || q.contains("private software") || q.contains("private sector") || q.contains("software developer job") || q.contains("software jobs")) return "PRIVATE_JOB";
        if (q.contains("career after") || q.contains("choose a career") || q.contains("career choice") || q.contains("career guidance") || q.contains("career counselling") || q.contains("career counseling")) return "CAREER_GUIDANCE";
        return null;
    }

    private boolean isStrongNavigationHint(String q, String intent) {
        return switch (intent) {
            case "FOREIGN_STUDY" -> q.contains("study abroad") || q.contains("masters") || q.contains("master's") || q.contains("admission requirement") || q.contains("universit");
            case "GOVT_JOB" -> q.contains("government") || q.contains("govt job");
            case "WOMEN_JOBS" -> q.contains("women") || q.contains("girls") || q.contains("female");
            case "PWD_JOBS" -> q.contains("disabilit") || q.contains("pwd") || q.contains("disabled");
            case "PM_VIKAS" -> q.contains("pm vikas");
            case "MBA_FPI_GIRLS" -> q.contains("mba fpi") || q.contains("mbafpi");
            case "SELF_EMPLOYMENT" -> q.contains("self-employment") || q.contains("self employment") || q.contains("entrepreneur");
            case "COUNSELLING" -> q.contains("counselling session") || q.contains("counseling session");
            case "PRIVATE_JOB" -> q.contains("internship") || q.contains("intern") || q.contains("software developer") || q.contains("developer roles");
            case "CAREER_GUIDANCE" -> q.contains("career after") || q.contains("choose a career") || q.contains("career guidance");
            default -> false;
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
        if (q.contains("self employment") || q.contains("self-employment") || q.contains("start my own business") || q.contains("entrepreneur")) return "SELF_EMPLOYMENT";
        if (q.contains("pm vikas")) return "PM_VIKAS";
        if (q.contains("mba fpi") || q.contains("mbafpi")) return "MBA_FPI_GIRLS";
        if (q.contains("jobs for women") || q.contains("women job") || q.contains("jobs for girls")) return "WOMEN_JOBS";
        if (q.contains("pwd jobs") || q.contains("persons with disabilities") || q.contains("disabled candidates")) return "PWD_JOBS";
        if (q.contains("job abroad") || q.contains("work abroad") || q.contains("overseas employment")) return "FOREIGN_JOB";
        if ((q.contains("resume") || q.contains("résumé")) && (q.contains("job") || q.contains("jobs") || q.contains("role") || q.contains("roles"))) return "PRIVATE_JOB";
        if (q.contains("study abroad") || q.contains("masters abroad") || q.contains("study overseas") || q.contains("which universities") || q.contains("university can i apply")) return "FOREIGN_STUDY";
        return null;
    }

    public List<ServiceInfo> services() { return services; }
    public IntentClassifier classifier() { return classifier; }
}
