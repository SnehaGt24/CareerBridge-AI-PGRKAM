package com.prj301.pgrkam;

import java.util.*;
import java.util.regex.*;

public class ResumeReviewService {
    public record Review(int score, String verdict, List<String> strengths, List<String> improvements,
                         List<String> detectedSkills, List<String> missingSections, List<String> keywordTips) {}
    private static final Map<String,List<String>> ROLE_SKILLS=Map.of(
        "developer",List.of("java","sql","git","rest","spring"),
        "analyst",List.of("sql","excel","power bi","requirements","communication"),
        "security",List.of("networking","linux","siem","incident response","security"),
        "data",List.of("python","sql","statistics","power bi","machine learning"));
    private static final List<String> SKILLS=List.of("java","python","sql","git","html","css","javascript","react","spring","excel","power bi","linux","networking","aws","azure","testing","selenium","communication","leadership","security","siem");

    public Review review(String text,String targetRole){
        String resume=text==null?"":text.trim(), lower=resume.toLowerCase(Locale.ROOT);
        List<String> strengths=new ArrayList<>(), improvements=new ArrayList<>(), missing=new ArrayList<>(), detected=new ArrayList<>();
        int score=15;
        for(String s:SKILLS)if(lower.contains(s)){detected.add(s);score+=2;}
        if(containsAny(lower,"experience","employment","internship")){strengths.add("Experience section is present");score+=12;}else missing.add("Experience");
        if(containsAny(lower,"education","university","college","degree")){strengths.add("Education is clearly represented");score+=10;}else missing.add("Education");
        if(containsAny(lower,"skills","technical skills","competencies")){strengths.add("Dedicated skills section found");score+=10;}else missing.add("Skills");
        if(containsAny(lower,"project","projects")){strengths.add("Project evidence is included");score+=10;}else improvements.add("Add 2–3 relevant projects with tools used and measurable outcomes.");
        if(Pattern.compile("\\b\\d+(?:[.%+]|\\s*(?:users|clients|projects|days|weeks|months|years))",Pattern.CASE_INSENSITIVE).matcher(resume).find()){strengths.add("Uses measurable evidence");score+=12;}else improvements.add("Quantify results, for example: reduced processing time by 20%.");
        if(resume.length()<350)improvements.add("The résumé appears too short; add evidence for skills and achievements.");
        if(resume.length()>7000)improvements.add("Condense the résumé and keep early-career applications close to 1–2 pages.");
        if(!lower.contains("@"))improvements.add("Add a professional email address."); else score+=4;
        List<String> wanted=keywords(targetRole), keywordTips=new ArrayList<>();
        for(String k:wanted)if(!lower.contains(k))keywordTips.add(k);
        if(!keywordTips.isEmpty())improvements.add("Tailor the summary and evidence to the target role; only add keywords you can genuinely demonstrate.");
        score=Math.max(20,Math.min(96,score));
        String verdict=score>=80?"Strong foundation — tailor it for each vacancy":score>=60?"Good start — strengthen evidence and targeting":"Needs revision before targeted applications";
        return new Review(score,verdict,strengths,improvements,detected,missing,keywordTips);
    }
    private static boolean containsAny(String s,String... terms){for(String t:terms)if(s.contains(t))return true;return false;}
    private static List<String> keywords(String role){String r=role==null?"":role.toLowerCase(Locale.ROOT);if(r.contains("soc")||r.contains("cyber")||r.contains("security"))return ROLE_SKILLS.get("security");if(r.contains("data"))return ROLE_SKILLS.get("data");if(r.contains("developer"))return ROLE_SKILLS.get("developer");if(r.contains("analyst"))return ROLE_SKILLS.get("analyst");return List.of("communication","teamwork","problem solving");}
}
