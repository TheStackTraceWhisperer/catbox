package com.example.catbox.common.repository;

import com.example.catbox.common.entity.OutboxDeadLetterEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxDeadLetterEventRepository extends JpaRepository<OutboxDeadLetterEvent, Long> {
}
