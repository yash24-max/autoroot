package com.autoroot.logprocessor.consumer;

import com.autoroot.common.dto.LogEntryDto;
import com.autoroot.logprocessor.service.LogProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for log ingestion.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaLogConsumer {

    private final LogProcessorService logProcessorService;

    @KafkaListener(topics = "${autoroot.kafka.log-topic:autoroot.logs}", groupId = "${autoroot.kafka.consumer-group:autoroot-processors}")
    public void consumeLog(LogEntryDto logEntryDto) {
        log.debug("Consumed log message with traceId: {}", logEntryDto.getTraceId());
        try {
            logProcessorService.processLog(logEntryDto);
        } catch (Exception e) {
            log.error("Error processing log message: {}", e.getMessage(), e);
        }
    }
}
