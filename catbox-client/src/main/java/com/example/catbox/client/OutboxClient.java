package com.example.catbox.client;

public interface OutboxClient {
    void createEvent(String aggregateType, String aggregateId, String eventType, String payload);
    
    void createEvent(String aggregateType, String aggregateId, String eventType, String correlationId, String payload);
}
