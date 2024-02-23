package com.tradegene.risk_management.commandservice.infrastructure.adapter.database;

import com.tradegene.risk_management.commandservice.application.ports.out.ContractLegReader;
import com.tradegene.risk_management.commandservice.domain.model.ContractLeg;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContractLegReaderImpl implements ContractLegReader {

    private final ContractLegRepository contractLegRepository;

    @Override
    public ContractLeg findFirstByContractIdAndProductId(Long contractId, Long productId) {
    	
        return contractLegRepository.findFirstByContractIdAndProductId(contractId, productId);
    }
}
