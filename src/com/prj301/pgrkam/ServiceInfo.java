package com.prj301.pgrkam;

import java.util.List;

public record ServiceInfo(
        String intent,
        String serviceName,
        String description,
        String actionLabel,
        String modulePath,
        List<String> examples) {
}
