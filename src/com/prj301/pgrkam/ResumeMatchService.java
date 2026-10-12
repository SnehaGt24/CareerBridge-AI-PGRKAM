package com.prj301.pgrkam;

import java.util.*;
import java.util.regex.*;

/**
 * Lightweight, explainable résumé-to-opportunity analyzer for the prototype.
 * It extracts common profile signals locally; it does not claim hiring probability.
 */
public class ResumeMatchService {
    public record Profile(String degree, String targetRole, String location, List<String> skills,
                          String experienceLevel, List<String> educationSignals) {}
    public record Result(Profile profile, List<JobMatchService.Match> matches,
                         String sourceNote, String nextStep) {}

    private static final List<String> SKILLS = List.of(
        "java","spring","spring boot","sql","mysql","postgresql","rest","api","git","github",
        "javascript","html","css","react","bootstrap","python","pandas","numpy","statistics",
        "power bi","excel","machine learning","scikit-learn","tensorflow","docker","kubernetes",
        "linux","networking","aws","azure","testing","selenium","siem","security","cybersecurity",
        "incident response","hibernate","jdbc","jsp","servlet","android","c++","c","communication"
    );
    private static final List<String> LOCATIONS = List.of(
        "Bengaluru","Bangalore","Mysuru","Mangalore","Mangaluru","Hubballi","Chandigarh","Ludhiana",
        "Amritsar","Delhi","Hyderabad","Pune","Mumbai","Chennai","Kolkata","Noida","Gurugram","Remote"
    );

    public Result analyze(String resumeText, String preferredLocation, String workMode, JobMatchService jobs) {
        String text = resumeText == null ? "" : resumeText;
        String lower = text.toLowerCase(Locale.ROOT);
        Profile p = extract(lower, text, preferredLocation);
        String skills = String.join(", ", p.skills());
        List<JobMatchService.Match> matches = jobs.match(p.degree(), skills, p.targetRole(), p.targetRole(),
            preferredLocation == null || preferredLocation.isBlank() ? p.location() : preferredLocation, workMode);
        String next = matches.isEmpty()
            ? "Add more skills or a preferred location, then run the matcher again."
            : "Start with the top role, review its missing skills, and open the official PGRKAM vacancy hub to verify the live vacancy and employer details.";
        return new Result(p, matches, "Résumé signals are extracted locally from the uploaded text. Job cards are prototype role matches linked to the official PGRKAM vacancy hub; they are not live vacancy claims or hiring predictions.", next);
    }

    private Profile extract(String lower, String original, String preferredLocation) {
        List<String> found = new ArrayList<>();
        for (String s : SKILLS) {
            if (Pattern.compile("(?<![a-z0-9])" + Pattern.quote(s) + "(?![a-z0-9])", Pattern.CASE_INSENSITIVE).matcher(original).find()) found.add(s);
        }
        String degree = firstMatch(original, "(?i)\\b(B\\.?\\s*Tech(?:nology)?|B\\.?E\\.?|M\\.?Tech(?:nology)?|M\\.?E\\.?|BCA|MCA|B\\.?Sc|M\\.?Sc)\\b");
        if (degree.isBlank()) degree = lower.contains("computer science") || lower.contains("cse") ? "Computer Science / Engineering" : "Not detected";

        String target = detectRole(lower);
        String location = preferredLocation == null ? "" : preferredLocation.trim();
        if (location.isBlank()) {
            for (String city : LOCATIONS) if (lower.contains(city.toLowerCase(Locale.ROOT))) { location = city; break; }
        }
        String exp = lower.contains("internship") || lower.contains("intern ") ? "Internship / Early career"
            : lower.contains("experience") || lower.contains("worked at") ? "Experience mentioned" : "Student / Fresher";
        List<String> education = new ArrayList<>();
        if (lower.contains("computer science") || lower.contains("cse")) education.add("Computer Science");
        if (lower.contains("engineering") || lower.contains("b.tech") || lower.contains("b.e")) education.add("Engineering");
        if (lower.contains("final year")) education.add("Final-year student");
        return new Profile(degree, target, location, found, exp, education);
    }

    private static String detectRole(String text) {
        String[][] roles = {
            {"Java Backend Developer","java","spring","backend"},
            {"Full Stack Developer","full stack","react","javascript"},
            {"Frontend Developer","frontend","react","html","css"},
            {"Data Analyst","data analyst","power bi","excel","statistics"},
            {"Junior Data Scientist","data scientist","machine learning","pandas"},
            {"Cloud Support Associate","cloud","aws","azure","linux"},
            {"QA / Automation Engineer","qa","testing","selenium","automation"},
            {"SOC Analyst","soc","security","siem","incident response"}
        };
        String best = "General software / technology role"; int bestHits = 0;
        for (String[] r : roles) {
            int hits = 0; for (int i=1;i<r.length;i++) if(text.contains(r[i])) hits++;
            if (hits > bestHits) { bestHits = hits; best = r[0]; }
        }
        return best;
    }

    private static String firstMatch(String text, String regex) {
        Matcher m = Pattern.compile(regex).matcher(text);
        return m.find() ? m.group().replaceAll("\\s+", " ").trim() : "";
    }
}
