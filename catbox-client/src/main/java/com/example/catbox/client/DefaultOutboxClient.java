package com.example.catbox.client;

import com.example.catbox.common.entity.OutboxEvent;
import com.example.catbox.common.repository.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
class DefaultOutboxClient implements OutboxClient {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void write(String aggregateType, String aggregateId, String eventType, Object payload) {
        // Delegate to the method with correlationId, passing null
        this.write(aggregateType, aggregateId, eventType, null, payload);
    }
    
    @Override
    public void write(String aggregateType, String aggregateId, String eventType, String correlationId, Object payload) {
        try {
            // 1. Serialize the domain-agnostic object
            String jsonPayload = objectMapper.writeValueAsString(payload);

            // 2. Create and save the event
            OutboxEvent event = new OutboxEvent(aggregateType, aggregateId, eventType, correlationId, jsonPayload);
            outboxEventRepository.save(event);
            
        } catch (JsonProcessingException e) {
            // This is a fatal error in the service, so we use an unchecked exception
            throw new RuntimeException("Failed to serialize outbox event payload for: " + eventType, e);
        }
    }
}
