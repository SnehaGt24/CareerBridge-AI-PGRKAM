package com.prj301.pgrkam;

import java.util.*;

public class StudyAbroadService {
    public record Recommendation(String university,String country,String city,String institutionType,
                                 String program,String duration,String intakes,int fitScore,String matchReason,
                                 String budgetBand,String academicEligibility,String languageRequirement,
                                 String testRequirement,String experienceRequirement,List<String> documents,
                                 List<String> applicationSteps,String officialUrl,String applicationUrl,
                                 String guidanceUrl) {}
    public record Plan(String summary,List<Recommendation> universities,List<String> documents,
                       List<String> steps,String officialGuidanceLabel,String officialGuidanceUrl,
                       String disclaimer) {}

    private final List<UniversityOption> universities = List.of(
        new UniversityOption("University of Birmingham","UK","Birmingham","Public",
            List.of("Computer Science","Data Science","Artificial Intelligence"),"Usually 1 year for many taught master's; verify the selected course.","Autumn; course-specific intakes vary",7.0,"High",
            "Relevant bachelor's degree with the academic background specified by the selected programme.","English-language evidence may be required; exact score depends on the programme.","GRE/GMAT only where the selected programme requests it.","Usually programme-dependent; verify the selected course.",
            docs(),countrySteps("UK"),"https://www.birmingham.ac.uk/study/postgraduate","https://www.birmingham.ac.uk/study/postgraduate","https://www.gov.uk/student-visa"),
        new UniversityOption("University of Leeds","UK","Leeds","Public",
            List.of("Computer Science","Data Science","Artificial Intelligence","Business Analytics"),"Usually 1 year for many UK taught master's; verify the selected course.","Autumn; selected courses vary",7.0,"High",
            "Relevant bachelor's degree and any course-specific prerequisite subjects.","English-language evidence may be required; check the course page.","Programme-specific; check whether the selected course asks for GRE/GMAT.","Usually not required for many taught programmes, but course-specific rules apply.",
            docs(),countrySteps("UK"),"https://www.leeds.ac.uk/study/postgraduate","https://www.leeds.ac.uk/study/postgraduate","https://www.gov.uk/student-visa"),
        new UniversityOption("University of Glasgow","UK","Glasgow","Public",
            List.of("Computer Science","Data Science","Software Engineering","Business Analytics"),"Programme-dependent; many taught master's are 1 year.","September and other course-specific intakes",7.2,"High",
            "Relevant undergraduate qualification meeting the selected programme's entry requirements.","English-language evidence may be required; exact score is programme-specific.","Check the selected programme for any additional tests.","Programme-specific; verify the official course page.",
            docs(),countrySteps("UK"),"https://www.gla.ac.uk/postgraduate/","https://www.gla.ac.uk/postgraduate/","https://www.gov.uk/student-visa"),
        new UniversityOption("University of Ottawa","Canada","Ottawa","Public",
            List.of("Computer Science","Data Science","Engineering","Analytics"),"Programme-dependent; verify the selected graduate programme.","Programme-specific",7.0,"High",
            "A recognised bachelor's degree and programme-specific prerequisites/academic standing.","English or French proficiency may be required depending on the programme.","Programme-specific; check the graduate programme page.","Usually programme-dependent; check the selected graduate programme.",
            docs(),countrySteps("Canada"),"https://www.uottawa.ca/study/graduate-studies","https://www.uottawa.ca/study/graduate-studies","https://www.canada.ca/en/immigration-refugees-citizenship/services/study-canada.html"),
        new UniversityOption("University of Calgary","Canada","Calgary","Public",
            List.of("Computer Science","Data Science","Engineering","Software"),"Programme-dependent", "Programme-specific",7.0,"High",
            "Relevant bachelor's degree with the prerequisite background listed by the selected programme.","English-language proficiency may be required.","Check the exact programme for additional test requirements.","Programme-specific.",
            docs(),countrySteps("Canada"),"https://grad.ucalgary.ca/future-students","https://grad.ucalgary.ca/future-students","https://www.canada.ca/en/immigration-refugees-citizenship/services/study-canada.html"),
        new UniversityOption("Technical University of Munich (TUM)","Germany","Munich","Public",
            List.of("Computer Science","Informatics","Data Engineering","Artificial Intelligence"),"Programme-dependent", "Programme-specific",8.0,"Medium",
            "A recognised bachelor's degree with subject-specific prerequisites and equivalence requirements.","Many programmes require proof of language proficiency; language depends on the selected programme.","Programme-specific; check aptitude/subject requirements on the degree page.","Usually programme-dependent.",
            docs(),countrySteps("Germany"),"https://www.tum.de/en/studies/degree-programs","https://www.tum.de/en/studies/degree-programs","https://www.daad.de/en/studying-in-germany/"),
        new UniversityOption("Saarland University","Germany","Saarbrücken","Public",
            List.of("Computer Science","Data Science","Artificial Intelligence","Informatics"),"Programme-dependent", "Programme-specific",7.5,"Medium",
            "Relevant bachelor's degree and the prerequisite knowledge listed for the selected programme.","Language proof depends on the programme language.","Programme-specific.","Usually programme-dependent.",
            docs(),countrySteps("Germany"),"https://www.uni-saarland.de/en/study/programmes.html","https://www.uni-saarland.de/en/study/programmes.html","https://www.daad.de/en/studying-in-germany/"),
        new UniversityOption("Monash University","Australia","Melbourne","Public",
            List.of("Computer Science","Data Science","Information Technology","Business Analytics"),"Programme-dependent", "Course-specific",7.0,"High",
            "Recognised prior degree and any course-specific prerequisites.","English-language proficiency is generally required unless an accepted exemption applies.","Check the selected course for any additional test requirements.","Usually course-specific.",
            docs(),countrySteps("Australia"),"https://www.monash.edu/study/courses","https://www.monash.edu/study/courses","https://www.studyaustralia.gov.au/"),
        new UniversityOption("University of Adelaide","Australia","Adelaide","Public",
            List.of("Computer Science","Data Science","Artificial Intelligence","Engineering"),"Programme-dependent", "Course-specific",7.0,"High",
            "Recognised bachelor's qualification and course-specific academic prerequisites.","English-language proficiency may be required.","Course-specific.","Usually course-specific.",
            docs(),countrySteps("Australia"),"https://www.adelaide.edu.au/degree-finder/","https://www.adelaide.edu.au/degree-finder/","https://www.studyaustralia.gov.au/"),
        new UniversityOption("Arizona State University","USA","Tempe","Public",
            List.of("Computer Science","Data Science","Software Engineering","Analytics"),"Programme-dependent", "Multiple intakes depending on programme",7.0,"High",
            "Relevant bachelor's degree and programme-specific prerequisites.","English-language proficiency may be required for international applicants.","GRE/GMAT and other tests depend on the selected programme.","Programme-specific.",
            docs(),countrySteps("USA"),"https://graduate.asu.edu/","https://graduate.asu.edu/","https://educationusa.state.gov/"),
        new UniversityOption("Northeastern University","USA","Boston","Private",
            List.of("Computer Science","Data Science","Software Engineering","Analytics"),"Programme-dependent", "Multiple intakes depending on programme",7.4,"High",
            "Relevant bachelor's degree and the prerequisites stated by the selected graduate programme.","English-language proficiency may be required.","Programme-specific; verify the exact programme's test policy.","Programme-specific.",
            docs(),countrySteps("USA"),"https://graduate.northeastern.edu/","https://graduate.northeastern.edu/","https://educationusa.state.gov/")
    );

    public Plan plan(String country,String course,double cgpa,String budget,String intake) {
        String c=canonicalCountry(country), courseText=course==null?"":course.toLowerCase(Locale.ROOT);
        String budgetBand=normaliseBudget(budget);
        List<Recommendation> recs=new ArrayList<>();
        for(UniversityOption u:universities) {
            if(!c.isBlank() && !u.country().equalsIgnoreCase(c)) continue;
            int score=45;
            boolean courseMatch=u.programs().stream().anyMatch(a->areaMatches(courseText,a));
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
                ?"Your selected study area is represented by this university's programme catalogue; the profile score is a prototype heuristic, not an admission probability."
                :"The university is in the selected country; verify whether the current programme catalogue contains your exact course.";
            recs.add(new Recommendation(u.name(),u.country(),u.city(),u.institutionType(),String.join(" / ",u.programs()),u.duration(),u.intakes(),score,reason,u.budgetBand(),u.academicEligibility(),u.languageRequirement(),u.testRequirement(),u.experienceRequirement(),u.documents(),u.applicationSteps(),u.officialUrl(),u.applicationUrl(),u.guidanceUrl()));
        }
        recs.sort((a,b)->Integer.compare(b.fitScore(),a.fitScore()));
        if(recs.size()>8) recs=new ArrayList<>(recs.subList(0,8));

        String summary="University matching for "+(c.isBlank()?"your preferred country":c)
            +(courseText.isBlank()?"":" • "+course)
            +(intake==null||intake.isBlank()?"":" • "+intake+" intake");
        return new Plan(summary,recs,docs(),applicationSteps(c),guidanceLabel(c),guidanceUrl(c),
            "Profile matches, CGPA targets and budget bands in this prototype are illustrative. They are not admission probabilities. Always verify the selected programme's current eligibility, language scores, tests, fees, deadlines, scholarships and visa requirements on official university/government pages before applying.");
    }

    private static List<String> docs(){
        return List.of("Passport / identity document","Academic transcripts / marksheets","Degree or provisional certificate",
            "Statement of Purpose (SOP)","Letters of Recommendation (if required)","Resume / CV",
            "English-language test result if required","Financial documents where required for admission/visa",
            "Passport-size photograph if requested by the institution");
    }
    private static List<String> applicationSteps(String c) {
        return switch(c) {
            case "UK" -> steps("Shortlist the exact programme and verify entry and English requirements.","Prepare transcripts, SOP, references, CV and required evidence.","Apply through the university's official application route.","Review offers, funding, tuition and living costs.","After an eligible offer, complete the institution's sponsorship/CAS steps where applicable.","Follow the current UK Student visa process.");
            case "Canada" -> steps("Shortlist the institution and exact graduate programme.","Check academic prerequisites and language evidence.","Prepare documents and apply through the institution's official route.","Review the current letter-of-acceptance and study-permit requirements.","Prepare financial and identity evidence.","Follow the current Government of Canada study-permit process.");
            case "Germany" -> steps("Shortlist programmes and check degree equivalence/prerequisites.","Check whether the university uses direct application or another application route.","Prepare academic documents, CV, motivation letter and language proof where required.","Submit before the programme deadline.","After admission, arrange finance and health-insurance evidence.","Follow the current German student-visa/residence process.");
            case "Australia" -> steps("Shortlist the course and verify academic and English requirements.","Prepare academic records, CV, statement and required evidence.","Apply through the university's official route.","Compare offers, tuition, scholarships and living costs.","Complete the institution's enrolment/confirmation process.","Follow the current Australian student-visa requirements.");
            case "USA" -> steps("Shortlist programmes and check programme-specific prerequisites.","Prepare transcripts, statement, recommendations, CV and test evidence if required.","Submit the university application and any programme-specific materials.","Review funding and admission decisions.","After admission, complete the institution's international-student process.","Follow the current US student-visa process and institution guidance.");
            default -> steps("Select a country and exact programme.","Verify current official eligibility and language requirements.","Prepare the programme-specific document set.","Apply through the official university route.","Review offer, funding and visa requirements.");
        };
    }
    private static List<String> steps(String... v){return List.of(v);}
    private static List<String> countrySteps(String c){
        return switch(c){
            case "UK" -> steps("Verify the exact programme entry and English requirements.","Prepare transcripts, SOP, references, CV and required evidence.","Apply through the university's official application route.","Review offers, funding, tuition and living costs.","Complete institution sponsorship/CAS steps where applicable.","Follow the current UK Student visa process.");
            case "Canada" -> steps("Verify the exact graduate programme and prerequisites.","Prepare academic records, statement, references, CV and language evidence where required.","Apply through the institution's official route.","Review the current letter-of-acceptance and study-permit requirements.","Prepare financial and identity evidence.","Follow the current Government of Canada study-permit process.");
            case "Germany" -> steps("Check programme prerequisites and degree equivalence.","Confirm the university's official application route.","Prepare academic documents, CV, motivation letter and language proof where required.","Submit before the programme deadline.","After admission, arrange finance and health-insurance evidence.","Follow the current German student-visa/residence process.");
            case "Australia" -> steps("Verify the course's academic and English requirements.","Prepare academic records, CV, statement and required evidence.","Apply through the university's official route.","Compare offers, tuition, scholarships and living costs.","Complete the institution's enrolment/confirmation process.","Follow the current Australian student-visa requirements.");
            case "USA" -> steps("Check programme-specific prerequisites and test policy.","Prepare transcripts, statement, recommendations, CV and required test evidence.","Submit the university application and programme-specific materials.","Review funding and admission decisions.","Complete the institution's international-student process after admission.","Follow the current US student-visa process.");
            default -> steps("Select the exact programme.","Verify official eligibility and language requirements.","Prepare programme-specific documents.","Apply through the official university route.");
        };
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
        if(b==null) return ""; String x=b.toLowerCase(Locale.ROOT);
        if(x.contains("low")) return "Low"; if(x.contains("medium")) return "Medium"; if(x.contains("high")) return "High"; return "";
    }
    private static String guidanceLabel(String c) {
        return switch(c) {
            case "UK" -> "Official UK Student visa guidance";
            case "Canada" -> "Government of Canada study guidance";
            case "Germany" -> "Official DAAD study guidance";
            case "Australia" -> "Official Study Australia guidance";
            case "USA" -> "Official EducationUSA guidance";
            default -> "PGRKAM / official overseas guidance";
        };
    }
    private static String guidanceUrl(String c) {
        return switch(c) {
            case "UK" -> "https://www.gov.uk/student-visa";
            case "Canada" -> "https://www.canada.ca/en/immigration-refugees-citizenship/services/study-canada.html";
            case "Germany" -> "https://www.daad.de/en/studying-in-germany/";
            case "Australia" -> "https://www.studyaustralia.gov.au/";
            case "USA" -> "https://educationusa.state.gov/";
            default -> "https://cms.pgrkam.com/";
        };
    }
}
