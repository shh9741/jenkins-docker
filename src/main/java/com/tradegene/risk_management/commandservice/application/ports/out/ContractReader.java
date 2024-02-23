package com.tradegene.risk_management.commandservice.application.ports.out;

import com.tradegene.risk_management.commandservice.domain.model.Contract;

public interface ContractReader {

    Contract findFirstByPortfolioIdAndProductId(Long portfolioId, Long productId);
}
