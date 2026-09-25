package com.prj301.pgrkam;

import java.time.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class RecruitmentService {
    public record ContactRequest(String id,String candidateName,String email,String role,String message,String status,String createdAt){}
    public record Interview(String id,String candidateName,String candidateEmail,String hrName,String scheduledAt,String meetUrl,String notes,String status){}
    private final List<ContactRequest> contacts=new CopyOnWriteArrayList<>();
    private final List<Interview> interviews=new CopyOnWriteArrayList<>();
    public ContactRequest contact(String name,String email,String role,String message){
        ContactRequest r=new ContactRequest(shortId("HR"),clean(name),clean(email),clean(role),clean(message),"Sent to HR queue",Instant.now().toString());contacts.add(r);return r;
    }
    public Interview schedule(String candidate,String email,String hr,String when,String meet,String notes){
        String url=meet!=null&&meet.startsWith("https://meet.google.com/")?meet:"https://meet.google.com/new";
        Interview i=new Interview(shortId("INT"),clean(candidate),clean(email),clean(hr),clean(when),url,clean(notes),"Scheduled");interviews.add(i);return i;
    }
    private static String shortId(String p){return p+"-"+UUID.randomUUID().toString().substring(0,8).toUpperCase(Locale.ROOT);}
    private static String clean(String s){return s==null?"":s.trim();}
}
