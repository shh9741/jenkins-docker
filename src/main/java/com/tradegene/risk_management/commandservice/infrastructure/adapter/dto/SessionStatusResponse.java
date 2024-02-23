package com.tradegene.risk_management.commandservice.infrastructure.adapter.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@ToString
@Getter
@SuperBuilder
public class SessionStatusResponse {

    private List<Grid> objects;

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @ToString
    @Getter
    @Builder
    public static class Grid {

        private String topic;
        private boolean isRunning;
    }
}
