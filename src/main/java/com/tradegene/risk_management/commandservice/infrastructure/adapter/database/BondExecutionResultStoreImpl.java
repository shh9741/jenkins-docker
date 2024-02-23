package com.tradegene.risk_management.commandservice.infrastructure.adapter.database;

import com.tradegene.risk_management.commandservice.application.ports.out.BondExecutionResultStore;
import com.tradegene.risk_management.commandservice.domain.model.BondExecutionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class BondExecutionResultStoreImpl implements BondExecutionResultStore {

    private final BondExecutionResultRepository repository;

    @Override
    public BondExecutionResult save(BondExecutionResult input) {
        return repository.save(input);
    }
}
