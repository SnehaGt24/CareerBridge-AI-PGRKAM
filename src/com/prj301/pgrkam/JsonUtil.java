package com.prj301.pgrkam;
import java.util.*;
import java.util.regex.*;

public final class JsonUtil {
    private JsonUtil() {}
    public static String extractMessage(String json){return extractString(json,"message");}
    public static String extractString(String json,String key){
        if(json==null)return "";
        Matcher m=Pattern.compile("\\\""+Pattern.quote(key)+"\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\"])*)\\\"").matcher(json);
        return m.find()?unescape(m.group(1)):"";
    }
    public static double extractDouble(String json,String key,double d){
        if(json==null)return d;
        Matcher m=Pattern.compile("\\\""+Pattern.quote(key)+"\\\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)").matcher(json);
        if(!m.find())return d;
        try{return Double.parseDouble(m.group(1));}catch(Exception e){return d;}
    }
    public static String chatResponse(ChatService.ChatResponse r){
        StringBuilder sb=new StringBuilder("{");
        field(sb,"message",r.message()).append(',');field(sb,"intent",r.intent()).append(',');
        sb.append("\"confidence\":").append(String.format(Locale.ROOT,"%.4f",r.confidence())).append(',');
        field(sb,"serviceName",r.serviceName()).append(',');field(sb,"description",r.description()).append(',');
        field(sb,"actionLabel",r.actionLabel()).append(',');field(sb,"modulePath",r.modulePath()).append(',');field(sb,"secondaryActionLabel",r.secondaryActionLabel()).append(',');field(sb,"secondaryPage",r.secondaryPage()).append(',');
        sb.append("\"alternatives\":[");
        for(int i=0;i<r.alternatives().size();i++){if(i>0)sb.append(',');var a=r.alternatives().get(i);sb.append('{');field(sb,"intent",a.intent()).append(',');sb.append("\"score\":").append(String.format(Locale.ROOT,"%.4f",a.score())).append('}');}
        return sb.append("]}").toString();
    }
    public static String jobMatches(List<JobMatchService.Match> ms){
        StringBuilder sb=new StringBuilder("{\"matches\":[");
        for(int i=0;i<ms.size();i++){if(i>0)sb.append(',');var m=ms.get(i);sb.append('{');field(sb,"role",m.role()).append(',');sb.append("\"score\":").append(m.score()).append(',');field(sb,"description",m.description()).append(',');array(sb,"matchedSkills",m.matchedSkills()).append(',');array(sb,"missingSkills",m.missingSkills()).append(',');field(sb,"locationFit",m.locationFit()).append(',');field(sb,"workMode",m.workMode()).append(',');field(sb,"employmentType",m.employmentType()).append(',');field(sb,"roadmap",m.roadmap()).append(',');field(sb,"actionUrl",m.actionUrl()).append(',');sb.append("\"locationPriority\":").append(m.locationPriority()).append(',');field(sb,"sourceLabel",m.sourceLabel());sb.append('}');}
        return sb.append("]}").toString();
    }
    public static String resumeReview(ResumeReviewService.Review r){StringBuilder sb=new StringBuilder("{");sb.append("\"score\":").append(r.score()).append(',');field(sb,"verdict",r.verdict()).append(',');array(sb,"strengths",r.strengths()).append(',');array(sb,"improvements",r.improvements()).append(',');array(sb,"detectedSkills",r.detectedSkills()).append(',');array(sb,"missingSections",r.missingSections()).append(',');array(sb,"keywordTips",r.keywordTips());return sb.append('}').toString();}
    public static String contact(RecruitmentService.ContactRequest r){StringBuilder sb=new StringBuilder("{");field(sb,"id",r.id()).append(',');field(sb,"status",r.status()).append(',');field(sb,"createdAt",r.createdAt());return sb.append('}').toString();}
    public static String interview(RecruitmentService.Interview i){StringBuilder sb=new StringBuilder("{");field(sb,"id",i.id()).append(',');field(sb,"status",i.status()).append(',');field(sb,"scheduledAt",i.scheduledAt()).append(',');field(sb,"meetUrl",i.meetUrl());return sb.append('}').toString();}
    public static String application(CareerPortalService.Application a){StringBuilder sb=new StringBuilder("{");applicationFields(sb,a);return sb.append('}').toString();}
    private static void applicationFields(StringBuilder sb,CareerPortalService.Application a){field(sb,"id",a.id()).append(',');field(sb,"candidateName",a.candidateName()).append(',');field(sb,"email",a.email()).append(',');field(sb,"role",a.role()).append(',');field(sb,"company",a.company()).append(',');field(sb,"location",a.location()).append(',');field(sb,"status",a.status()).append(',');field(sb,"appliedAt",a.appliedAt()).append(',');field(sb,"notes",a.notes());}
    public static String employerContacts(List<EmployerDirectoryService.EmployerContact> contacts){StringBuilder sb=new StringBuilder("{\"contacts\":[");for(int i=0;i<contacts.size();i++){if(i>0)sb.append(',');var c=contacts.get(i);sb.append('{');field(sb,"company",c.company()).append(',');field(sb,"recruiterName",c.recruiterName()).append(',');field(sb,"roleArea",c.roleArea()).append(',');field(sb,"location",c.location()).append(',');field(sb,"email",c.email()).append(',');field(sb,"phone",c.phone()).append(',');field(sb,"hours",c.hours()).append(',');field(sb,"verified",c.verified()).append(',');field(sb,"careersUrl",c.careersUrl());sb.append('}');}return sb.append("]}").toString();}
    public static String studyPlan(StudyAbroadService.Plan p){
        StringBuilder sb=new StringBuilder("{");field(sb,"summary",p.summary()).append(',');sb.append("\"universities\":[");
        for(int i=0;i<p.universities().size();i++){if(i>0)sb.append(',');var u=p.universities().get(i);sb.append('{');
            field(sb,"university",u.university()).append(',');field(sb,"country",u.country()).append(',');field(sb,"city",u.city()).append(',');field(sb,"institutionType",u.institutionType()).append(',');
            field(sb,"program",u.program()).append(',');field(sb,"duration",u.duration()).append(',');field(sb,"intakes",u.intakes()).append(',');sb.append("\"fitScore\":").append(u.fitScore()).append(',');
            field(sb,"matchReason",u.matchReason()).append(',');field(sb,"budgetBand",u.budgetBand()).append(',');field(sb,"academicEligibility",u.academicEligibility()).append(',');
            field(sb,"languageRequirement",u.languageRequirement()).append(',');field(sb,"testRequirement",u.testRequirement()).append(',');field(sb,"experienceRequirement",u.experienceRequirement()).append(',');
            array(sb,"documents",u.documents()).append(',');array(sb,"applicationSteps",u.applicationSteps()).append(',');field(sb,"officialUrl",u.officialUrl()).append(',');field(sb,"applicationUrl",u.applicationUrl()).append(',');field(sb,"guidanceUrl",u.guidanceUrl());sb.append('}');
        }
        sb.append("],");array(sb,"documents",p.documents()).append(',');array(sb,"steps",p.steps()).append(',');field(sb,"officialGuidanceLabel",p.officialGuidanceLabel()).append(',');field(sb,"officialGuidanceUrl",p.officialGuidanceUrl()).append(',');field(sb,"disclaimer",p.disclaimer());return sb.append('}').toString();
    }
    public static String resumeMatch(ResumeMatchService.Result r){
        StringBuilder sb=new StringBuilder("{\"profile\":{");var p=r.profile();field(sb,"degree",p.degree()).append(',');field(sb,"targetRole",p.targetRole()).append(',');field(sb,"location",p.location()).append(',');field(sb,"experienceLevel",p.experienceLevel()).append(',');array(sb,"skills",p.skills()).append(',');array(sb,"educationSignals",p.educationSignals());sb.append("},\"matches\":");
        String jm=jobMatches(r.matches());sb.append(jm.substring(jm.indexOf(':')+1,jm.length()-1)).append(',');field(sb,"sourceNote",r.sourceNote()).append(',');field(sb,"nextStep",r.nextStep());return sb.append('}').toString();
    }
    public static String services(List<ServiceInfo> ss){
        StringBuilder sb=new StringBuilder("[");
        for(int i=0;i<ss.size();i++){if(i>0)sb.append(',');ServiceInfo s=ss.get(i);sb.append('{');field(sb,"intent",s.intent()).append(',');field(sb,"serviceName",s.serviceName()).append(',');field(sb,"description",s.description()).append(',');field(sb,"actionLabel",s.actionLabel()).append(',');field(sb,"modulePath",s.modulePath());sb.append('}');}
        return sb.append(']').toString();
    }
    private static StringBuilder array(StringBuilder sb,String k,List<String> vs){sb.append('"').append(escape(k)).append("\":[");for(int i=0;i<vs.size();i++){if(i>0)sb.append(',');sb.append('"').append(escape(vs.get(i))).append('"');}return sb.append(']');}
    private static StringBuilder field(StringBuilder sb,String k,String v){return sb.append('"').append(escape(k)).append("\":\"").append(escape(v==null?"":v)).append('"');}
    private static String escape(String s){return s.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n").replace("\r","\\r");}
    private static String unescape(String s){return s.replace("\\n","\n").replace("\\\"","\"").replace("\\\\","\\");}
}
