package com.example.routebox.common.repository;

import com.example.routebox.common.entity.ProcessedMessageArchive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repository for managing archived processed message records. */
@Repository
public interface ProcessedMessageArchiveRepository
    extends JpaRepository<ProcessedMessageArchive, Long> {}
