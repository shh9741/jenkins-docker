package com.tradegene.risk_management.commandservice.application.ports.out;

import com.tradegene.app.dto.common.response.ApiResponse;
import com.tradegene.risk_management.commandservice.application.dto.PortfolioDetailResponse;

public interface PortfolioMicroserviceClient {

	ApiResponse<PortfolioDetailResponse> detailPortfolio(Long portfolioId);
}
