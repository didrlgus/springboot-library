package gabia.library.kafka.producer;

import gabia.library.kafka.ReviewUpdateOfBookMessage;
import gabia.library.kafka.config.KafkaUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

@Configuration
public class KafkaReviewUpdateOfBookProducerConfig {

    @Value("${kafka.bootstrapServers}")
    private String bootstrapServers;

    @Bean(name = "reviewUpdateOfBookProducerFactory")
    public ProducerFactory<String, ReviewUpdateOfBookMessage> producerFactory() {
        Map<String, Object> configProps = KafkaUtils.producerFactoryConfig(bootstrapServers);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean(name = "reviewUpdateOfBookKafkaTemplate")
    public KafkaTemplate<String, ReviewUpdateOfBookMessage> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
