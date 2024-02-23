package com.tradegene.risk_management.commandservice.infrastructure.adapter.database;

import org.springframework.stereotype.Component;

import com.tradegene.risk_management.commandservice.application.ports.out.ContractLegTransactionReader;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ContractLegTransactionReaderImpl implements ContractLegTransactionReader {

    private final ContractLegTransactionRepository repository;
}
