package br.com.marcos.product_order_infrastructure.outbox;

import br.com.marcos.product_order_domain.entity.OutboxEvent;
import br.com.marcos.product_order_infrastructure.kafka.KafkaProducerService;
import br.com.marcos.product_order_infrastructure.repository.OutboxEventRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class OutboxPublisher {

    private final OutboxEventRepository repository;
    private final KafkaProducerService producer;

    public OutboxPublisher(
            OutboxEventRepository repository,
            KafkaProducerService producer
    ) {
        this.repository = repository;
        this.producer = producer;
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publish() {

        List<OutboxEvent> events = repository.findByStatus("PENDING");

        for (OutboxEvent event : events) {
            try {
                producer.send(
                        event.getType(),
                        event.getAggregateId(),
                        event.getPayload()
                );

                event.setStatus("SENT");
            } catch (Exception e) {
                event.setStatus("ERROR");
            }
        }
    }
}
