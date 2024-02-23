package com.tradegene.risk_management.commandservice.infrastructure.adapter.messaging.out;

import com.tradegene.app.utils.KafkaProducerUtil;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.tradegene.risk_management.commandservice.application.dto.TransactionQueryProducerDto;
import com.tradegene.risk_management.commandservice.application.ports.out.TransactionQueryProducer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionQueryProducerImpl implements TransactionQueryProducer {

	private final KafkaProducerUtil kafkaProducerUtil;

	@Override
	public void pubQuery(TransactionQueryProducerDto transactionQueryProducerDto) {

		String jsonString = new Gson().toJson(transactionQueryProducerDto);

		kafkaProducerUtil.sendMessage("risk-management-transaction.query", jsonString);
	}
}
