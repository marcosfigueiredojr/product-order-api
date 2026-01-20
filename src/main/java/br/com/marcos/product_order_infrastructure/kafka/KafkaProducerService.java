package br.com.marcos.product_order_infrastructure.kafka;

import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String topic, String key, String payload) {
        kafkaTemplate.send(topic, key, payload);
    }

	public void send(String type, UUID aggregateId, String payload) {
		// TODO Auto-generated method stub
		
	}
}
