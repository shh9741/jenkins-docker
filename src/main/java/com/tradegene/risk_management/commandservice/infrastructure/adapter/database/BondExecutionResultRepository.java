package com.tradegene.risk_management.commandservice.infrastructure.adapter.database;

import com.tradegene.risk_management.commandservice.domain.model.BondExecutionResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BondExecutionResultRepository extends JpaRepository<BondExecutionResult, Long> {

    Page<BondExecutionResult> findAllByProcessingYn(String processingYn, Pageable pageable);
}
