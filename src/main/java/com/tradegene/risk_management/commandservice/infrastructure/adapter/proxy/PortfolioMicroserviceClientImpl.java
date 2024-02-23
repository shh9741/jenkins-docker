package com.tradegene.risk_management.commandservice.infrastructure.adapter.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.tradegene.app.dto.common.response.ApiResponse;
import com.tradegene.risk_management.commandservice.application.dto.PortfolioDetailResponse;
import com.tradegene.risk_management.commandservice.application.ports.out.PortfolioMicroserviceClient;

@FeignClient(name = "portfolio", url = "${feign.client.portfolio.url}")
public interface PortfolioMicroserviceClientImpl extends PortfolioMicroserviceClient {

	@Override
	@GetMapping(value = "/api/portfolio/{id}")
	ApiResponse<PortfolioDetailResponse> detailPortfolio(@PathVariable("id") Long portfolioId);
}