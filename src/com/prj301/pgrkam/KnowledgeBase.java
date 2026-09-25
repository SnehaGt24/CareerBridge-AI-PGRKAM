package com.prj301.pgrkam;

import java.util.*;

public final class KnowledgeBase {
    private KnowledgeBase() {}

    public static List<ServiceInfo> services() {
        return List.of(
            new ServiceInfo("PRIVATE_JOB", "Private Sector Jobs",
                "Explore private-sector employment opportunities and job openings relevant to your profile.",
                "Go to Private Jobs", "https://cms.pgrkam.com/vacancies",
                List.of(
                    "I want a private job", "I need a job in a private company", "software jobs in private companies",
                    "I completed btech and want an IT job", "show me private sector vacancies", "jobs in companies for graduates",
                    "I am searching for a software developer job", "private employment opportunities", "corporate jobs for freshers",
                    "find private jobs near me", "suggest a job for me", "recommend a job for me", "what job is suitable for me", "which job should I apply for", "find a job that matches my profile", "show jobs suitable for my skills", "recommend jobs based on my profile", "I am a CSE student and I know Java MySQL and HTML what jobs should I target", "I know Java MySQL and HTML what software jobs should I target", "I am a final year CSE student looking for a software developer job")),
            new ServiceInfo("GOVT_JOB", "Government Jobs",
                "Find government recruitment information and guidance for public-sector opportunities.",
                "Explore Government Jobs", "https://cms.pgrkam.com/vacancies",
                List.of(
                    "I want a government job", "government recruitment notifications", "show public sector job vacancies",
                    "how can I apply for government jobs", "any govt jobs for graduates", "state government recruitment",
                    "central government jobs", "government vacancy information", "I am preparing for a public service job",
                    "where are government job openings")),
            new ServiceInfo("SELF_EMPLOYMENT", "Self-Employment",
                "Explore self-employment, entrepreneurship and livelihood avenues available through employment services.",
                "Explore Self-Employment", "https://cms.pgrkam.com/vocational-guidance",
                List.of(
                    "I want to start my own business", "self employment opportunities", "how can I become self employed",
                    "support for starting a small business", "entrepreneurship guidance", "business opportunities for unemployed youth",
                    "I want to work for myself", "startup and self employment options", "small enterprise guidance",
                    "ways to earn through self employment")),
            new ServiceInfo("FOREIGN_JOB", "Foreign Jobs / Overseas Employment",
                "Get guidance related to overseas employment and foreign job opportunities.",
                "Explore Foreign Jobs", "https://cms.pgrkam.com/",
                List.of(
                    "I want a job abroad", "foreign employment opportunities", "how to work in another country",
                    "overseas jobs for graduates", "I want to find a job in the UK", "jobs outside India",
                    "international employment", "work abroad opportunities", "foreign company jobs overseas",
                    "guidance for overseas employment")),
            new ServiceInfo("FOREIGN_STUDY", "Foreign Study / Study Abroad",
                "Access guidance related to higher education and study opportunities abroad.",
                "Explore Foreign Study", "https://cms.pgrkam.com/",
                List.of(
                    "I want to study abroad", "higher studies in another country", "study in the UK after engineering",
                    "foreign education guidance", "masters abroad information", "I want to go overseas for studies",
                    "study abroad after graduation", "international university guidance", "foreign study opportunities",
                    "how can I study in another country")),
            new ServiceInfo("COUNSELLING", "Career Counselling",
                "Get counselling support to discuss education, employment choices and career-related concerns.",
                "Get Counselling Guidance", "https://cms.pgrkam.com/vocational-guidance",
                List.of(
                    "I need career counselling", "I want to talk to a career counsellor", "I am confused about my career",
                    "can someone counsel me about jobs", "employment counselling", "I need personal career advice",
                    "career counselling appointment", "help me decide my career", "I need counselling after graduation",
                    "career counsellor guidance")),
            new ServiceInfo("CAREER_GUIDANCE", "Career Guidance",
                "Access general career information, pathways, skills guidance and employment planning support.",
                "Explore Career Guidance", "https://cms.pgrkam.com/vocational-guidance",
                List.of(
                    "I need career guidance", "what career should I choose", "career options after btech",
                    "which field is good for me", "guide me about career opportunities", "what can I do after graduation",
                    "career path information", "skills needed for a good career", "help me plan my career",
                    "career options and guidance", "career choices after engineering", "what are my career choices after engineering",
                    "career options after engineering", "I want to become a Java developer but I do not know Spring Boot", "I want to become a Java developer but I don’t know Spring Boot", "I have three months before graduation and I still do not have a job", "I have 3 months before graduation and I still do not have a job")),
            new ServiceInfo("ARMED_FORCES", "Armed Forces Opportunities",
                "Find guidance on induction and employment opportunities related to the armed forces.",
                "Explore Armed Forces", "https://cms.pgrkam.com/employment-services-related-to-candidates",
                List.of(
                    "I want to join the army", "armed forces career", "how can I join the military",
                    "defence jobs after graduation", "army recruitment guidance", "opportunities in armed forces",
                    "join navy or air force", "military career information", "defence employment opportunities",
                    "induction into armed forces")),
            new ServiceInfo("JOB_MELA", "Job Melas / Job Fairs",
                "Find information related to job melas, recruitment drives and employment events.",
                "Explore Job Melas", "https://cms.pgrkam.com/employment-services-related-to-candidates",
                List.of(
                    "are there any job melas", "upcoming job fair", "recruitment drive information",
                    "when is the next job mela", "job fair for graduates", "employment fair near me",
                    "campus recruitment event", "job mela registration", "job fair information",
                    "upcoming recruitment events")),
            new ServiceInfo("OUT_OF_SCOPE", "Unsupported Query",
                "This assistant is designed for employment, career, study and related PGRKAM service navigation.",
                "Ask an Employment Question", "",
                List.of(
                    "who won the cricket match", "what is the weather today", "play a song", "tell me a joke",
                    "what is the capital of france", "movie recommendations", "calculate two plus two", "book a restaurant",
                    "latest football score", "what time is it"))
        );
    }
}
