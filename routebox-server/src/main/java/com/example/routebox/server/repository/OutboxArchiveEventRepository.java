package com.example.routebox.server.repository;

import com.example.routebox.server.entity.OutboxArchiveEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxArchiveEventRepository extends JpaRepository<OutboxArchiveEvent, Long> {
}
