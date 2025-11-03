package com.example.catbox.server.repository;

import com.example.catbox.server.entity.OutboxArchiveEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxArchiveEventRepository extends JpaRepository<OutboxArchiveEvent, Long> {
}
