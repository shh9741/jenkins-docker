package com.tradegene.risk_management.commandservice.infrastructure.adapter.proxy;

import com.tradegene.app.dto.common.response.ApiResponse;
import com.tradegene.risk_management.commandservice.application.dto.ProductCashDetailResponse;
import com.tradegene.risk_management.commandservice.application.dto.ProductDetailResponse;
import com.tradegene.risk_management.commandservice.application.ports.out.DataManagementMicroserviceClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "data-management", url = "${feign.client.data-management.url}")
public interface DataManagementMicroserviceClientImpl extends DataManagementMicroserviceClient {

    @Override
    @GetMapping("/api/product/{id}")
    ApiResponse<ProductDetailResponse> detailProduct(@PathVariable("id") Long productId);

    @Override
    @GetMapping("/api/product/cash-detail")
    ApiResponse<ProductCashDetailResponse> detailCashProduct(@RequestParam("product_currency_code") String productCurrencyCode);
}
