package com.tradegene.risk_management.commandservice.infrastructure.adapter.messaging.out;

import com.google.gson.Gson;
import com.tradegene.app.utils.KafkaProducerUtil;
import com.tradegene.risk_management.commandservice.application.dto.BondExecutionResultRegisterAckNackProducerDto;
import com.tradegene.risk_management.commandservice.application.ports.out.BondExecutionResultProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BondExecutionResultProducerImpl implements BondExecutionResultProducer {

    private final KafkaProducerUtil kafkaProducerUtil;

    @Override
    public void pubRegisterInitialNack(BondExecutionResultRegisterAckNackProducerDto input, String message) {

        String jsonString = new Gson().toJson(input);

        Map<String, String> headers = new HashMap<>();
        headers.put("message", message);

        kafkaProducerUtil.sendMessageWithHeader("risk-management-bond-execution-result.register.initialNack", headers, jsonString);
    }

    @Override
    public void pubRegisterFinalAck(BondExecutionResultRegisterAckNackProducerDto input, String message) {

        String jsonString = new Gson().toJson(input);

        Map<String, String> headers = new HashMap<>();
        headers.put("message", message);

        kafkaProducerUtil.sendMessageWithHeader("risk-management-bond-execution-result.register.finalAck", headers, jsonString);
    }
}
