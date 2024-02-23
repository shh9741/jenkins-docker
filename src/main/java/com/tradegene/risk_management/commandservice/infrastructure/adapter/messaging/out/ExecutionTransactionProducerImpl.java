package com.tradegene.risk_management.commandservice.infrastructure.adapter.messaging.out;

import com.google.gson.Gson;
import com.tradegene.app.utils.KafkaProducerUtil;
import com.tradegene.risk_management.commandservice.application.dto.ExecutionTransactionRegisterAckNackProducerDto;
import com.tradegene.risk_management.commandservice.application.dto.ExecutionTransactionRegisterProducerDto;
import com.tradegene.risk_management.commandservice.application.ports.out.ExecutionTransactionProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExecutionTransactionProducerImpl implements ExecutionTransactionProducer {

	private final KafkaProducerUtil kafkaProducerUtil;

	@Override
	public void pubRegister(ExecutionTransactionRegisterProducerDto input) {

		String jsonString = new Gson().toJson(input);

		kafkaProducerUtil.sendMessage("risk-management-execution-transaction.register", jsonString);
	}

	@Override
	public void pubRegisterInitialAck(String message) {

		Map<String, String> headers = new HashMap<>();
		headers.put("message", message);

		kafkaProducerUtil.sendMessageWithHeader("risk-management-execution-transaction.register.initialAck", headers, "");
	}

	@Override
	public void pubRegisterInitialNack(ExecutionTransactionRegisterAckNackProducerDto input, String message) {

		String jsonString = new Gson().toJson(input);

		Map<String, String> headers = new HashMap<>();
		headers.put("message", message);

		kafkaProducerUtil.sendMessageWithHeader("risk-management-execution-transaction.register.initialNack", headers, jsonString);
	}

	@Override
	public void pubRegisterFinalAck(ExecutionTransactionRegisterAckNackProducerDto input, String message) {

		String jsonString = new Gson().toJson(input);

		Map<String, String> headers = new HashMap<>();
		headers.put("message", message);

		kafkaProducerUtil.sendMessageWithHeader("risk-management-execution-transaction.register.finalAck", headers, jsonString);
	}

	@Override
	public void pubRegisterFinalNack(ExecutionTransactionRegisterAckNackProducerDto input, String message) {

		String jsonString = new Gson().toJson(input);

		Map<String, String> headers = new HashMap<>();
		headers.put("message", message);

		kafkaProducerUtil.sendMessageWithHeader("risk-management-execution-transaction.register.finalNack", headers, jsonString);
	}
}
