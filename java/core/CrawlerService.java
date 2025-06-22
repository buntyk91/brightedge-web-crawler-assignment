package core;

import db.MongoWriter;
import db.MySQLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;

@Service
public class CrawlerService {

    @Autowired
    private AppConfig config;

    public List<Map<String, Object>> run() throws Exception {
        Connection mysqlConn = DriverManager.getConnection(
                config.getMysqlUrl(), config.getMysqlUsername(), config.getMysqlPassword()
        );

        UrlFetcher fetcher = new UrlFetcher(mysqlConn);
        // KafkaDeadLetterProducer kafkaProducer = new KafkaDeadLetterProducer(getKafkaProps());
        Crawler crawler = new Crawler(null);
        Parser parser = new Parser();
        MongoWriter mongoWriter = new MongoWriter(config.getMongoUri(), config.getMongoDatabase(), config.getMongoCollection());
        MySQLWriter mysqlWriter = new MySQLWriter(mysqlConn);

        List<Map<String, Object>> summaryList = new ArrayList<>();

        List<String> urls = fetcher.fetchNextBatch(10);
        for (String url : urls) {
            Optional<String> html = crawler.fetchWithRetries(url, 3);
            if (html.isPresent()) {
                Map<String, Object> parsed = parser.parse(html.get(), url);
                mongoWriter.write(parsed);
                mysqlWriter.write(parsed);

                // Extract fixed fields for return
                Map<String, Object> fixedFields = new LinkedHashMap<>();
                fixedFields.put("url", parsed.get("url"));
                fixedFields.put("title", parsed.get("title"));
                fixedFields.put("description", parsed.get("description"));
                fixedFields.put("timestamp", parsed.get("timestamp"));
                fixedFields.put("body", parsed.get("body"));
                summaryList.add(fixedFields);
            }
        }
        return summaryList;
    }

    private Properties getKafkaProps() {
        Properties props = new Properties();
        props.put("bootstrap.servers", config.getKafkaBootstrapServers());
        props.put("key.serializer", config.getKafkaKeySerializer());
        props.put("value.serializer", config.getKafkaValueSerializer());
        return props;
    }
}
