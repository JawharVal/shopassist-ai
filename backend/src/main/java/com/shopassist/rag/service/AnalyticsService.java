package com.shopassist.rag.service;

import com.shopassist.rag.domain.AnalyticsSummary;
import com.shopassist.rag.repository.AnalyticsRepository;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;

    public AnalyticsService(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    public AnalyticsSummary summary(String tenantId) {
        return analyticsRepository.summary(tenantId);
    }
}
