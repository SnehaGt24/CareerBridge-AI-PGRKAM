package com.prj301.pgrkam;
import java.util.List;
public record JobRole(String title, String description, List<String> requiredSkills,
                      List<String> locations, String workMode, String employmentType,
                      String pgrkamUrl) {}
