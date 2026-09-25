package com.prj301.pgrkam;

import java.util.*;

public class EmployerDirectoryService {
    public record EmployerContact(String company,String recruiterName,String roleArea,String location,
                                  String email,String phone,String hours,String verified,String careersUrl){}
    private final List<EmployerContact> contacts=List.of(
        new EmployerContact("DemoTech Solutions","Ananya Rao","Software & QA","Bengaluru","careers@demotech.example","+91 80 0000 1001","Mon–Fri, 9:30–17:30","Demo contact","https://www.pgrkam.com/private-jobs"),
        new EmployerContact("SecureOps Demo","Kiran S","Cybersecurity & SOC","Bengaluru","talent@secureops.example","+91 80 0000 1002","Mon–Fri, 10:00–18:00","Demo contact","https://www.pgrkam.com/private-jobs"),
        new EmployerContact("DataBridge Demo","Meera N","Data & Business Analysis","Mysuru","jobs@databridge.example","+91 821 000 1003","Mon–Fri, 9:00–17:00","Demo contact","https://www.pgrkam.com/private-jobs"),
        new EmployerContact("CloudWorks Demo","Rahul P","Cloud & Support","Remote / Karnataka","hiring@cloudworks.example","+91 80 0000 1004","Mon–Fri, 10:00–18:00","Demo contact","https://www.pgrkam.com/private-jobs")
    );
    public List<EmployerContact> contacts(){return contacts;}
}
