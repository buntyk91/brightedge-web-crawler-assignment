package core;

import org.apache.kafka.clients.producer.*;
import java.util.*;

public class KafkaDeadLetterProducer {
    private final KafkaProducer<String, String> producer;

    public KafkaDeadLetterProducer(Properties kafkaProps) {
        this.producer = new KafkaProducer<>(kafkaProps);
    }

    public void sendToDeadLetter(String url) {
        producer.send(new ProducerRecord<>("dead-url-topic", url));
    }
}
