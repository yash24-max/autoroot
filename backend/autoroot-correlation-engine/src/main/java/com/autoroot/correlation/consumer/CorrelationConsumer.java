package com.autoroot.correlation.consumer;

import com.autoroot.common.dto.LogEntryDto;
import com.autoroot.correlation.service.CorrelationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Kafka consumer for Correlation Engine.
 * Correlates logs after they have been processed and stored by LogProcessor.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CorrelationConsumer {

    private final CorrelationService correlationService;

    @KafkaListener(topics = "${autoroot.kafka.log-topic:autoroot.logs}", groupId = "${autoroot.kafka.correlation-group:autoroot-correlation-engine}")
    public void consumeLogForCorrelation(LogEntryDto logEntryDto) {
        log.debug("Consumed log message for correlation with traceId: {}", logEntryDto.getTraceId());

        UUID logId = logEntryDto.getId();
        if (logId == null) {
            log.warn("Skip correlation: Log message has no ID for traceId: {}", logEntryDto.getTraceId());
            return;
        }

        try {
            correlationService.correlateLog(logId);
        } catch (Exception e) {
            log.error("Error correlating log message: {}", e.getMessage(), e);
        }
    }
}
