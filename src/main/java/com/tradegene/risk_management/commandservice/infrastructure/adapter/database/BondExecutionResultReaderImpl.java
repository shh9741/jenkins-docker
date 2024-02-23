package com.tradegene.risk_management.commandservice.infrastructure.adapter.database;

import com.tradegene.risk_management.commandservice.application.ports.out.BondExecutionResultReader;
import com.tradegene.risk_management.commandservice.domain.model.BondExecutionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class BondExecutionResultReaderImpl implements BondExecutionResultReader {

	private final BondExecutionResultRepository repository;

	@Override
	public BondExecutionResult findById(Long id) {
		return repository.findById(id).orElse(null);
	}
}
