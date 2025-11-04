package com.example.routebox.server.repository;

import com.example.catbox.server.entity.OutboxDeadLetterEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxDeadLetterEventRepository extends JpaRepository<OutboxDeadLetterEvent, Long> {
}
