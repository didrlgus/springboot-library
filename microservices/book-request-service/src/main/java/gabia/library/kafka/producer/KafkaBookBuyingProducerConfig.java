package gabia.library.kafka.producer;

import gabia.library.kafka.BookBuyingMessage;
import gabia.library.kafka.config.KafkaUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

@Configuration
public class KafkaBookBuyingProducerConfig {

    @Value("${kafka.bootstrapServers}")
    private String bootstrapServers;

    @Bean(name = "bookBuyingProducerFactory")
    public ProducerFactory<String, BookBuyingMessage> producerFactory() {
        Map<String, Object> configProps = KafkaUtils.producerFactoryConfig(bootstrapServers);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean(name = "bookBuyingKafkaTemplate")
    public KafkaTemplate<String, BookBuyingMessage> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
