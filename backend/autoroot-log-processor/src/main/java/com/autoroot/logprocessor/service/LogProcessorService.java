package com.autoroot.logprocessor.service;

import com.autoroot.common.dto.LogEntryDto;
import com.autoroot.common.entity.LogEntry;
import com.autoroot.common.mapper.LogEntryMapper;
import com.autoroot.common.repository.LogEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for processing logs.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LogProcessorService {

    private final LogEntryRepository logEntryRepository;
    private final LogEntryMapper logEntryMapper;

    /**
     * Process a log entry: store metadata in DB and actual log content in MinIO.
     */
    @Transactional
    public void processLog(LogEntryDto logEntryDto) {
        log.debug("Processing log entry: {}", logEntryDto.getMessage());

        // 1. Convert DTO to Entity
        LogEntry logEntry = logEntryMapper.toEntity(logEntryDto);

        // 2. Save to Repository (Metadata)
        logEntryRepository.save(logEntry);

        // TODO: Implement MinIO storage for full log content
        // For now, we store everything in PostgreSQL via the entity

        log.info("Log entry processed and saved with ID: {}", logEntry.getId());
    }
}
