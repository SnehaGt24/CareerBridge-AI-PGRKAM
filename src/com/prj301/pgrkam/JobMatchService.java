package com.prj301.pgrkam;

import java.util.*;
import java.util.stream.Collectors;

public class JobMatchService {
    public record Match(String role, int score, String description, List<String> matchedSkills,
                        List<String> missingSkills, String locationFit, String workMode,
                        String employmentType, String roadmap, String actionUrl,
                        int locationPriority, String sourceLabel) {}

    private final List<JobRole> roles = List.of(
        new JobRole("Java Backend Developer","Build APIs, business logic and database-backed services using Java.",
            List.of("java","spring","sql","rest","git"),List.of("Bengaluru","Mysuru","Mangaluru"),"Hybrid","Full-time",PgrkamUrl.vacancySearch("Java Backend Developer", "")),
        new JobRole("Full Stack Developer","Build frontend interfaces and backend APIs for web applications.",
            List.of("java","spring","javascript","html","css","sql","git"),List.of("Bengaluru","Hubballi","Remote"),"Hybrid","Full-time",PgrkamUrl.vacancySearch("Full Stack Developer", "")),
        new JobRole("Data Analyst","Clean, analyse and visualise business data to support decisions.",
            List.of("sql","excel","python","power bi","statistics"),List.of("Bengaluru","Mysuru","Remote"),"Hybrid","Full-time",PgrkamUrl.vacancySearch("Data Analyst", "")),
        new JobRole("Business Analyst","Translate business needs into requirements, insights and process improvements.",
            List.of("requirements","communication","sql","excel","power bi"),List.of("Bengaluru","Mangaluru","Remote"),"On-site / Hybrid","Full-time",PgrkamUrl.vacancySearch("Business Analyst", "")),
        new JobRole("Frontend Developer","Create responsive web interfaces and user experiences.",
            List.of("javascript","html","css","react","git"),List.of("Bengaluru","Remote"),"Remote / Hybrid","Full-time",PgrkamUrl.vacancySearch("Frontend Developer", "")),
        new JobRole("QA / Automation Engineer","Test applications manually and through automation.",
            List.of("testing","selenium","java","api","sql"),List.of("Bengaluru","Mysuru"),"Hybrid","Full-time",PgrkamUrl.vacancySearch("QA Automation Engineer", "")),
        new JobRole("Cloud Support Associate","Support cloud-hosted systems, networking, Linux and deployment operations.",
            List.of("linux","networking","cloud","aws","azure"),List.of("Bengaluru","Remote"),"Hybrid / Shifts","Full-time",PgrkamUrl.vacancySearch("Cloud Support Associate", "")),
        new JobRole("Junior Data Scientist","Apply statistics and machine learning to data-driven problems.",
            List.of("python","sql","statistics","machine learning","pandas"),List.of("Bengaluru","Remote"),"Hybrid","Full-time",PgrkamUrl.vacancySearch("Junior Data Scientist", "")),
        new JobRole("SOC Analyst","Monitor alerts, investigate threats and support incident response.",
            List.of("networking","linux","siem","security","incident response"),List.of("Bengaluru","Mysuru","Remote"),"On-site / Shifts","Full-time",PgrkamUrl.vacancySearch("SOC Analyst", ""))
    );

    public List<Match> match(String degree, String skillsText, String interests, String preferredRole, String location, String workMode) {
        Set<String> skills = normaliseSkills(skillsText);
        String interest = ((interests == null ? "" : interests) + " " +
                           (preferredRole == null ? "" : preferredRole)).toLowerCase(Locale.ROOT);
        List<Match> matches = new ArrayList<>();

        for (JobRole role : roles) {
            List<String> matched = new ArrayList<>();
            List<String> missing = new ArrayList<>();
            for (String req : role.requiredSkills()) {
                if (hasSkill(skills, req)) matched.add(req); else missing.add(req);
            }
            double coverage = (double)matched.size()/role.requiredSkills().size();
            int score = (int)Math.round(coverage*75);
            String rt = role.title().toLowerCase(Locale.ROOT);
            for (String word : rt.split("\\s+")) {
                if (word.length()>3 && interest.contains(word)) { score += 12; break; }
            }
            if (degree != null && !degree.isBlank()) score += 5;
            int locationPriority = locationPriority(role, location);
            if (locationPriority == 2) score += 8;
            else if (locationPriority == 1) score += 3;
            if (workMode != null && !workMode.isBlank() && role.workMode().toLowerCase(Locale.ROOT).contains(workMode.toLowerCase(Locale.ROOT))) score += 5;
            score = Math.min(99, score);

            String locationFit = locationFit(role, location);
            String roadmap = missing.isEmpty()
                ? "Strong skill alignment. Build one portfolio project, polish your resume and start targeted applications."
                : "Priority learning: " + String.join(", ", missing.subList(0, Math.min(3, missing.size()))) +
                  ". Then build a small project using these skills and update your resume.";
            matches.add(new Match(role.title(), score, role.description(), matched, missing, locationFit,
                role.workMode(), role.employmentType(), roadmap, PgrkamUrl.vacancySearch(role.title(), location), locationPriority,
                "Opens the official PGRKAM vacancy search filtered by this role; confirm the live listing and employer advertisement before applying"));
        }
        matches.sort(Comparator.comparingInt(Match::locationPriority).reversed().thenComparing(Comparator.comparingInt(Match::score).reversed()));
        return matches.subList(0,Math.min(6,matches.size()));
    }

    private static int locationPriority(JobRole role, String preferred) {
        if (preferred == null || preferred.isBlank()) return 0;
        String p=preferred.toLowerCase(Locale.ROOT);
        for(String l:role.locations()) {
            String x=l.toLowerCase(Locale.ROOT);
            if(!x.equals("remote") && (p.contains(x) || x.contains(p))) return 2;
        }
        return role.locations().stream().anyMatch(l->l.equalsIgnoreCase("Remote")) ? 1 : 0;
    }

    private static String locationFit(JobRole role, String preferred) {
        if (preferred == null || preferred.isBlank()) return "Add a preferred location to improve matching";
        String p=preferred.toLowerCase(Locale.ROOT);
        for(String l:role.locations()) if(!l.equalsIgnoreCase("Remote") && (p.contains(l.toLowerCase(Locale.ROOT)) || l.toLowerCase(Locale.ROOT).contains(p))) return "Priority location match: "+l;
        if(role.locations().stream().anyMatch(l->l.equalsIgnoreCase("Remote"))) return "No exact city match • remote option available";
        return "Not a location-priority match • available in "+String.join(", ",role.locations());
    }

    private static Set<String> normaliseSkills(String text) {
        if (text == null) return new HashSet<>();
        return Arrays.stream(text.toLowerCase(Locale.ROOT).split("[,;/\\n]+"))
            .map(String::trim).filter(s->!s.isBlank()).collect(Collectors.toCollection(HashSet::new));
    }
    private static boolean hasSkill(Set<String> skills,String req) {
        for(String s:skills) if(s.equals(req)||s.contains(req)||req.contains(s)) return true;
        return false;
    }
}
