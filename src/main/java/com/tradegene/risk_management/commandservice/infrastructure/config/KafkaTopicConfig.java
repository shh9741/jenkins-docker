package com.tradegene.risk_management.commandservice.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin.NewTopics;

@Configuration
public class KafkaTopicConfig {

    @Bean
	public NewTopics topics() {

		return new NewTopics(

				TopicBuilder.name("risk-management-cash-transaction.register.initialNack").partitions(3).build(),
				TopicBuilder.name("risk-management-cash-transaction.register.finalAck").partitions(3).build(),

				TopicBuilder.name("risk-management-bond-execution-result.register.initialNack").partitions(3).build(),
				TopicBuilder.name("risk-management-bond-execution-result.register.finalAck").partitions(3).build(),

				TopicBuilder.name("risk-management-execution-transaction.register").partitions(3).build(),
				TopicBuilder.name("risk-management-execution-transaction.register.initialAck").partitions(3).build(),
				TopicBuilder.name("risk-management-execution-transaction.register.initialNack").partitions(3).build(),
				TopicBuilder.name("risk-management-execution-transaction.register.finalAck").partitions(3).build(),
				TopicBuilder.name("risk-management-execution-transaction.register.finalNack").partitions(3).build(),

				TopicBuilder.name("risk-management-transaction.query").partitions(3).build()
        );
    }
}
