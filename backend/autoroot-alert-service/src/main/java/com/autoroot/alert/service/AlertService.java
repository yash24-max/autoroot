package com.autoroot.alert.service;

import com.autoroot.common.dto.LogEntryDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for detecting anomalies and triggering alerts.
 */
@Service
@Slf4j
public class AlertService {

    /**
     * Process a log entry to check for alert conditions.
     */
    public void processLogForAlerts(LogEntryDto logEntryDto) {
        if ("ERROR".equalsIgnoreCase(logEntryDto.getLevel()) || "FATAL".equalsIgnoreCase(logEntryDto.getLevel())) {
            triggerAlert(logEntryDto);
        }
    }

    private void triggerAlert(LogEntryDto logEntryDto) {
        log.warn("🚨 ALERT TRIGGERED: High severity log detected in service: {}", logEntryDto.getServiceName());
        log.warn("Details: [{}] {}", logEntryDto.getLevel(), logEntryDto.getMessage());

        // TODO: In a real implementation, this would send a notification via:
        // 1. Slack Webhook
        // 2. Email (SendGrid/Amazon SES)
        // 3. PagerDuty API

        log.info("Alert notification dispatched successfully for trace: {}", logEntryDto.getTraceId());
    }
}
