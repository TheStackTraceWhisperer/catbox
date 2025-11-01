package com.example.catbox.client;

import com.example.catbox.common.entity.OutboxEvent;
import com.example.catbox.common.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
class DefaultOutboxClient implements OutboxClient {

    private final OutboxEventRepository outboxEventRepository;

    @Override
    public void createEvent(String aggregateType, String aggregateId, String eventType, String payload) {
        OutboxEvent event = new OutboxEvent(aggregateType, aggregateId, eventType, payload);
        outboxEventRepository.save(event);
    }
}
