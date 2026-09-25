package com.prj301.pgrkam;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class CareerPortalService {
    public record Application(String id,String candidateName,String email,String role,String company,
                              String location,String status,String appliedAt,String notes){}
    private final List<Application> applications=new CopyOnWriteArrayList<>();

    public Application add(String candidateName,String email,String role,String company,String location,String status,String notes){
        String safeStatus=List.of("Saved","Applied","Shortlisted","Interview","Offer","Rejected").contains(status)?status:"Saved";
        Application a=new Application("APP-"+UUID.randomUUID().toString().substring(0,8).toUpperCase(Locale.ROOT),
            clean(candidateName),clean(email),clean(role),clean(company),clean(location),safeStatus,Instant.now().toString(),clean(notes));
        applications.add(a);return a;
    }
    private static String clean(String value){return value==null?"":value.trim();}
}
