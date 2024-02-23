package com.tradegene.risk_management.commandservice.infrastructure.adapter.database;

import org.springframework.stereotype.Component;

import com.tradegene.risk_management.commandservice.application.ports.out.ContractTransactionDetailReader;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ContractTransactionDetailReaderImpl implements ContractTransactionDetailReader {

    private final ContractTransactionDetailRepository repository;
}
