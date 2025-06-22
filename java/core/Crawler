package core;

import java.util.Optional;
import org.jsoup.Jsoup;

public class Crawler {
    private final KafkaDeadLetterProducer dlqProducer;

    public Crawler(KafkaDeadLetterProducer dlqProducer) {
        this.dlqProducer = dlqProducer;
    }

    public Optional<String> fetchWithRetries(String url, int retries) {
        for (int i = 0; i < retries; i++) {
            try {
                Thread.sleep(300);
                return Optional.of(Jsoup.connect(url).timeout(10000).get().html());
            } catch (Exception e) {
                System.err.println("Retry " + (i + 1) + " failed for URL: " + url);
            }
        }
        dlqProducer.sendToDeadLetter(url);
        return Optional.empty();
    }
}
