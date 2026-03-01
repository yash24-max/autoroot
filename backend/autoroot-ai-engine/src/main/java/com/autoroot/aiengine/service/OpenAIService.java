package com.autoroot.aiengine.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Service for OpenAI integration.
 */
@Service
@Slf4j
public class OpenAIService {

        @Value("${autoroot.openai.model:gpt-4}")
        private String model;

        /**
         * Generate an incident summary using OpenAI.
         */
        public Mono<String> summarizeIncident(String incidentTitle, String logExcerpts) {
                log.debug("Generating summary for incident: {}", incidentTitle);

                // Simple placeholder for OpenAI API call
                // In a real implementation, this would call the Chat Completions API

                // This is a stub that returns a mock response
                // Once the user provides a real API key, this can be switched to real calls
                return Mono.just(
                                "AI Summary: This incident appears to be caused by a database connection timeout. Recommended fix: Check the connection pool settings.")
                                .doOnSuccess(summary -> log.info("Successfully generated summary for incident: {}",
                                                incidentTitle));
        }
}
