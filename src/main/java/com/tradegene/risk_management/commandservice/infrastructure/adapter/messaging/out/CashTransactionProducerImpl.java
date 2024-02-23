package com.tradegene.risk_management.commandservice.infrastructure.adapter.messaging.out;

import com.tradegene.app.utils.KafkaProducerUtil;
import org.springframework.stereotype.Service;

import com.tradegene.risk_management.commandservice.application.ports.out.CashTransactionProducer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CashTransactionProducerImpl implements CashTransactionProducer {

	private final KafkaProducerUtil kafkaProducerUtil;

	@Override
	public void pubRegisterInitialNack(String message) {

		Map<String, String> headers = new HashMap<>();
		headers.put("message", message);

		kafkaProducerUtil.sendMessageWithHeader("risk-management-cash-transaction.register.initialNack", headers,"");
	}

	@Override
	public void pubRegisterFinalAck(String message) {

		Map<String, String> headers = new HashMap<>();
		headers.put("message", message);

		kafkaProducerUtil.sendMessageWithHeader("risk-management-cash-transaction.register.finalAck", headers, "");
	}
}
