package com.autoroot.gateway.service;

import com.autoroot.common.dto.LogEntryDto;
import com.autoroot.common.entity.Incident;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Service to relay events from Kafka to WebSocket clients.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketRelayService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Listen for log events and broadcast to interested clients.
     */
    @KafkaListener(topics = "${autoroot.kafka.log-topic:autoroot.logs}", groupId = "autoroot-gateway-ws-broadcaster")
    public void relayLog(LogEntryDto logEntryDto) {
        log.debug("Relaying log to WebSocket for tenant: {}", logEntryDto.getTenantId());
        messagingTemplate.convertAndSend("/topic/logs/" + logEntryDto.getTenantId(), logEntryDto);
    }

    /**
     * Listen for incident events and broadcast.
     * Note: Assumes an 'autoroot.incidents' topic exists for incident status
     * updates.
     */
    @KafkaListener(topics = "${autoroot.kafka.incident-topic:autoroot.incidents}", groupId = "autoroot-gateway-ws-broadcaster")
    public void relayIncident(Incident incident) {
        log.debug("Relaying incident to WebSocket for tenant: {}", incident.getTenantId());
        messagingTemplate.convertAndSend("/topic/incidents/" + incident.getTenantId(), incident);
    }
}
