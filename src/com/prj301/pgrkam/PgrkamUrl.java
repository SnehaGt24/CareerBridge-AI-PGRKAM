package com.prj301.pgrkam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/** Builds links to the official PGRKAM vacancy-search portal. */
public final class PgrkamUrl {
    private static final String BASE = "https://pgrkam.com/search-results/?job_type=2";
    private PgrkamUrl() {}

    public static String vacancySearch(String keywords, String location) {
        StringBuilder url = new StringBuilder(BASE);
        if (keywords != null && !keywords.isBlank()) {
            url.append("&search_keywords=").append(URLEncoder.encode(keywords.trim(), StandardCharsets.UTF_8));
        }
        if (location != null && !location.isBlank()) {
            url.append("&search_location=").append(URLEncoder.encode(location.trim(), StandardCharsets.UTF_8));
        }
        return url.toString();
    }
}
