package com.example.catbox.common.repository;

import com.example.catbox.common.entity.OutboxArchiveEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxArchiveEventRepository extends JpaRepository<OutboxArchiveEvent, Long> {
}
