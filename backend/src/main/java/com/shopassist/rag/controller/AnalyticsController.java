package com.shopassist.rag.controller;

import com.shopassist.rag.domain.AnalyticsSummary;
import com.shopassist.rag.security.SecurityUtils;
import com.shopassist.rag.service.AnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final SecurityUtils securityUtils;

    public AnalyticsController(AnalyticsService analyticsService, SecurityUtils securityUtils) {
        this.analyticsService = analyticsService;
        this.securityUtils = securityUtils;
    }

    @GetMapping("/summary")
    public AnalyticsSummary summary() {
        return analyticsService.summary(securityUtils.currentUser().tenantId());
    }
}
