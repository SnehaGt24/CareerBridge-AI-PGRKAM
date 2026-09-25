package com.prj301.pgrkam;

import java.util.*;

public class StudyAbroadService {
    public record Recommendation(String university,String country,int fitScore,String matchReason,
                                 String budgetBand,String officialUrl) {}
    public record Plan(String summary,List<Recommendation> universities,List<String> documents,
                       List<String> steps,String officialGuidanceLabel,String officialGuidanceUrl,
                       String disclaimer) {}

    private final List<UniversityOption> universities = List.of(
        new UniversityOption("University of Birmingham","UK",List.of("computer science","data science","artificial intelligence","business analytics"),7.0,"High","https://www.birmingham.ac.uk/study/postgraduate"),
        new UniversityOption("University of Leeds","UK",List.of("computer science","data science","artificial intelligence","business analytics"),7.0,"High","https://www.leeds.ac.uk/postgraduate"),
        new UniversityOption("University of Glasgow","UK",List.of("computer science","data science","software engineering","business analytics"),7.2,"High","https://www.gla.ac.uk/postgraduate/"),
        new UniversityOption("University of Ottawa","Canada",List.of("computer science","data science","engineering","analytics"),7.0,"High","https://www.uottawa.ca/study/graduate-studies"),
        new UniversityOption("University of Calgary","Canada",List.of("computer science","data science","engineering","software"),7.0,"High","https://grad.ucalgary.ca/future-students"),
        new UniversityOption("Dalhousie University","Canada",List.of("computer science","data science","digital innovation","engineering"),6.8,"High","https://www.dal.ca/study/programs/graduate-professional.html"),
        new UniversityOption("Technical University of Munich (TUM)","Germany",List.of("computer science","informatics","data engineering","artificial intelligence"),8.0,"Medium","https://www.tum.de/en/studies/degree-programs"),
        new UniversityOption("University of Stuttgart","Germany",List.of("computer science","engineering","software","information technology"),7.2,"Medium","https://www.uni-stuttgart.de/en/study/study-programs/"),
        new UniversityOption("Saarland University","Germany",List.of("computer science","data science","artificial intelligence","informatics"),7.5,"Medium","https://www.uni-saarland.de/en/study/programmes.html"),
        new UniversityOption("Monash University","Australia",List.of("computer science","data science","information technology","business analytics"),7.0,"High","https://www.monash.edu/study/courses"),
        new UniversityOption("University of Adelaide","Australia",List.of("computer science","data science","artificial intelligence","engineering"),7.0,"High","https://www.adelaide.edu.au/degree-finder/"),
        new UniversityOption("University of Technology Sydney (UTS)","Australia",List.of("information technology","data science","artificial intelligence","analytics"),6.8,"High","https://www.uts.edu.au/study"),
        new UniversityOption("Arizona State University","USA",List.of("computer science","data science","software engineering","analytics"),7.0,"High","https://graduate.asu.edu/"),
        new UniversityOption("Northeastern University","USA",List.of("computer science","data science","software engineering","analytics"),7.4,"High","https://graduate.northeastern.edu/"),
        new UniversityOption("University at Buffalo","USA",List.of("computer science","data science","engineering","management science"),7.0,"High","https://www.buffalo.edu/grad/explore.html")
    );

    public Plan plan(String country,String course,double cgpa,String budget,String intake) {
        String c=canonicalCountry(country), courseText=course==null?"":course.toLowerCase(Locale.ROOT);
        String budgetBand=normaliseBudget(budget);
        List<Recommendation> recs=new ArrayList<>();

        for(UniversityOption u:universities) {
            if(!c.isBlank() && !u.country().equalsIgnoreCase(c)) continue;
            int score=45;
            boolean courseMatch=u.areas().stream().anyMatch(a->areaMatches(courseText,a));
            if(courseMatch) score+=25;
            if(cgpa>0) {
                double diff=cgpa-u.demoCgpaTarget();
                if(diff>=0.5) score+=18; else if(diff>=0) score+=12; else if(diff>=-0.5) score+=5; else score-=8;
            }
            if(!budgetBand.isBlank()) {
                if(budgetBand.equalsIgnoreCase(u.budgetBand())) score+=10;
                else if("High".equalsIgnoreCase(budgetBand)) score+=7;
                else if("Medium".equalsIgnoreCase(budgetBand)&&"High".equalsIgnoreCase(u.budgetBand())) score-=5;
                else if("Low".equalsIgnoreCase(budgetBand)&&!"Low".equalsIgnoreCase(u.budgetBand())) score-=10;
            }
            score=Math.max(20,Math.min(98,score));
            String reason=courseMatch
                ?"Matches your selected study area; profile fit is a demo heuristic, not an admission decision."
                :"Country match; verify whether the university currently offers your exact course.";
            recs.add(new Recommendation(u.name(),u.country(),score,reason,u.budgetBand(),u.officialUrl()));
        }
        recs.sort((a,b)->Integer.compare(b.fitScore(),a.fitScore()));
        if(recs.size()>6) recs=new ArrayList<>(recs.subList(0,6));

        List<String> docs=List.of("Passport","Academic transcripts / marksheets","Degree or provisional certificate",
            "Statement of Purpose (SOP)","Letters of Recommendation (if required)","Resume / CV",
            "English-language test result if required","Financial documents for tuition/visa requirements");

        String summary="Study plan for "+(c.isBlank()?"your preferred country":c)
            +(courseText.isBlank()?"":" • "+course)
            +(intake==null||intake.isBlank()?"":" • "+intake+" intake");

        return new Plan(summary,recs,docs,applicationSteps(c),guidanceLabel(c),guidanceUrl(c),
            "University matches, profile targets and budget bands in this academic prototype are illustrative. Always verify current course availability, entry requirements, fees, deadlines, scholarships and visa rules on official university/government websites before applying.");
    }

    private static boolean areaMatches(String course,String area) {
        if(course.isBlank()) return true;
        String a=area.toLowerCase(Locale.ROOT);
        if(a.contains(course)||course.contains(a)) return true;
        for(String w:course.split("\\s+")) if(w.length()>3&&a.contains(w)) return true;
        return false;
    }
    private static String canonicalCountry(String c) {
        if(c==null) return "";
        return switch(c.trim().toLowerCase(Locale.ROOT)) {
            case "united kingdom","england","uk" -> "UK";
            case "united states","united states of america","us","usa" -> "USA";
            case "canada" -> "Canada"; case "germany" -> "Germany"; case "australia" -> "Australia";
            default -> c.trim();
        };
    }
    private static String normaliseBudget(String b) {
        if(b==null) return "";
        String x=b.toLowerCase(Locale.ROOT);
        if(x.contains("low")) return "Low"; if(x.contains("medium")) return "Medium"; if(x.contains("high")) return "High"; return "";
    }
    private static List<String> applicationSteps(String c) {
        return switch(c) {
            case "UK" -> List.of("Shortlist universities and verify course-specific academic and English requirements.",
                "Prepare transcripts, SOP, references, CV and English-language evidence if required.",
                "Apply through the university's official application route.",
                "Compare offers, scholarships, tuition and living costs before accepting.",
                "After an eligible offer, obtain the required sponsorship/CAS documentation from the institution.",
                "Prepare funds and required evidence, then follow the current UK Student visa process.",
                "Arrange accommodation, travel and pre-departure documents.");
            case "Canada" -> List.of("Search institutions/programs and compare academic fit, location and total cost.",
                "Prepare transcripts, statement, references, CV and language-test evidence where required.",
                "Apply directly using each institution's official process.",
                "After an eligible letter of acceptance, review current study-permit requirements.",
                "Prepare financial/identity documents and other required evidence.",
                "Apply through the official government study-permit/visa process.",
                "Plan accommodation, insurance and travel after approval.");
            case "Germany" -> List.of("Search suitable programmes and check degree equivalence and prerequisites.",
                "Check whether the university uses direct application or a central application service.",
                "Prepare transcripts, degree documents, CV, motivation letter and language proof where required.",
                "Submit applications before programme deadlines.",
                "After admission, arrange finances and health insurance.",
                "Follow the current German student visa/residence process.",
                "Arrange accommodation and enrolment documents.");
            case "Australia" -> List.of("Shortlist suitable courses and verify academic/English requirements.",
                "Prepare academic records, CV, statement and language evidence where required.",
                "Apply through the university's official route.",
                "Review offer conditions, fees, scholarships and living costs.",
                "Accept an offer and obtain enrolment documentation required for visa processing.",
                "Follow the current Australian student visa process.",
                "Arrange health cover, accommodation and travel.");
            case "USA" -> List.of("Shortlist programmes by curriculum, prerequisites, location, cost and career goals.",
                "Check whether GRE/English tests are required by each programme.",
                "Prepare transcripts, SOP/personal statement, CV and recommendation letters.",
                "Submit individual university applications and track decisions.",
                "Compare offers and funding; complete international-student financial steps.",
                "After receiving the institution's immigration document, follow the current US student-visa process.",
                "Arrange housing, travel and pre-arrival requirements.");
            default -> List.of("Choose a country and course based on your career goal and budget.",
                "Shortlist universities using official programme pages.","Check academic and language requirements.",
                "Prepare SOP, CV, transcripts and references where required.","Apply through official university portals.",
                "Compare offers and funding options.","Follow the current official student-visa process.");
        };
    }
    private static String guidanceLabel(String c) {
        return switch(c) {
            case "UK" -> "Official UK Student visa guidance";
            case "Canada" -> "Official EduCanada study guidance";
            case "Germany" -> "Official DAAD study guidance";
            case "Australia" -> "Official Study Australia guidance";
            case "USA" -> "Official EducationUSA guidance";
            default -> "PGRKAM Foreign Study guidance";
        };
    }
    private static String guidanceUrl(String c) {
        return switch(c) {
            case "UK" -> "https://www.gov.uk/student-visa";
            case "Canada" -> "https://www.educanada.ca/start-commencez/index.aspx?lang=eng";
            case "Germany" -> "https://www.daad.de/en/studying-in-germany/";
            case "Australia" -> "https://www.studyaustralia.gov.au/";
            case "USA" -> "https://educationusa.state.gov/";
            default -> "https://www.pgrkam.com/foreign-module-info";
        };
    }
}
