package com.prj301.pgrkam;
import java.util.List;

public record UniversityOption(
    String name, String country, String city, String institutionType,
    List<String> programs, String duration, String intakes,
    double demoCgpaTarget, String budgetBand,
    String academicEligibility, String languageRequirement,
    String testRequirement, String experienceRequirement,
    List<String> documents, List<String> applicationSteps,
    String officialUrl, String applicationUrl, String guidanceUrl
) {}
