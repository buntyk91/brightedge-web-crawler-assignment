package core;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class AppConfig {
    @Value("${mysql.url}")
    private String mysqlUrl;

    @Value("${mysql.username}")
    private String mysqlUsername;

    @Value("${mysql.password}")
    private String mysqlPassword;

    @Value("${mongo.uri}")
    private String mongoUri;

    @Value("${mongo.database}")
    private String mongoDatabase;

    @Value("${mongo.collection}")
    private String mongoCollection;

    @Value("${kafka.bootstrap.servers}")
    private String kafkaBootstrapServers;

    @Value("${kafka.key.serializer}")
    private String kafkaKeySerializer;

    @Value("${kafka.value.serializer}")
    private String kafkaValueSerializer;
}
