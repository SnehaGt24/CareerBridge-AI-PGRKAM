package com.prj301.pgrkam;
import java.util.List;
public record UniversityOption(String name, String country, List<String> areas,
                               double demoCgpaTarget, String budgetBand, String officialUrl) {}
