package gabia.library.kafka.producer;

import gabia.library.kafka.config.KafkaUtils;
import gabia.library.kafka.message.ReviewCreateMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

@Configuration
public class KafkaReviewCreateProducerConfig {

    @Value("${kafka.bootstrapServers}")
    private String bootstrapServers;

    @Bean(name = "reviewCreateProducerFactory")
    public ProducerFactory<String, ReviewCreateMessage> producerFactory() {
        Map<String, Object> configProps = KafkaUtils.producerFactoryConfig(bootstrapServers);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean(name = "reviewCreateKafkaTemplate")
    public KafkaTemplate<String, ReviewCreateMessage> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
