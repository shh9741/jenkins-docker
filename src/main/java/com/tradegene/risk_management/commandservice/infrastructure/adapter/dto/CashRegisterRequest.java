package com.tradegene.risk_management.commandservice.infrastructure.adapter.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@ToString
@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CashRegisterRequest {

    @Schema(description = "금액", defaultValue = "10000")
    private BigDecimal amount;
}
