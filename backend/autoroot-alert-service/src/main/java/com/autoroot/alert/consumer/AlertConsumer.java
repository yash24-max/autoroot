package com.autoroot.alert.consumer;

import com.autoroot.common.dto.LogEntryDto;
import com.autoroot.alert.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for Alert Service.
 * Listens to the log stream to detect anomalies and trigger alerts.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AlertConsumer {

    private final AlertService alertService;

    @KafkaListener(topics = "${autoroot.kafka.log-topic:autoroot.logs}", groupId = "${autoroot.kafka.alert-group:autoroot-alert-service}")
    public void consumeLogForAlerts(LogEntryDto logEntryDto) {
        log.debug("Consumed log message for alerting check: {}", logEntryDto.getTraceId());

        try {
            alertService.processLogForAlerts(logEntryDto);
        } catch (Exception e) {
            log.error("Error processing log for alerts: {}", e.getMessage(), e);
        }
    }
}
