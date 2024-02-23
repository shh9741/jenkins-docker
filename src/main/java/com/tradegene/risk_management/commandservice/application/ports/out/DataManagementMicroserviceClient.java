package com.tradegene.risk_management.commandservice.application.ports.out;

import com.tradegene.app.dto.common.response.ApiResponse;
import com.tradegene.risk_management.commandservice.application.dto.ProductCashDetailResponse;
import com.tradegene.risk_management.commandservice.application.dto.ProductDetailResponse;

public interface DataManagementMicroserviceClient {

    ApiResponse<ProductDetailResponse> detailProduct(Long productId);
    ApiResponse<ProductCashDetailResponse> detailCashProduct(String productCurrencyCode);
}
