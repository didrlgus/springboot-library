package gabia.library.kafka.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.bootstrapServers}")
    private String bootstrapServers;

    @Value("${kafka.topic.rent.name}")
    private String rentTopicName;

    @Value("${kafka.topic.return.name}")
    private String returnTopicName;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        return new KafkaAdmin(configs);
    }

    @Bean("bookRentTopic")
    public NewTopic bookRentTopic() {
        return new NewTopic(rentTopicName, 1, (short) 1);
    }

    @Bean("bookReturnTopic")
    public NewTopic bookReturnTopic() {
        return new NewTopic(returnTopicName, 1, (short) 1);
    }

}
