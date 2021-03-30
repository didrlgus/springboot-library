package gabia.library.kafka.producer;

import gabia.library.kafka.BookRentMessage;
import gabia.library.kafka.config.KafkaUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

@Configuration
public class KafkaBookRentProducerConfig {

    @Value("${kafka.bootstrapServers}")
    private String bootstrapServers;

    @Bean(name = "bookRentProducerFactory")
    public ProducerFactory<String, BookRentMessage> producerFactory() {
        Map<String, Object> configProps = KafkaUtils.producerFactoryConfig(bootstrapServers);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean(name = "bookRentKafkaTemplate")
    public KafkaTemplate<String, BookRentMessage> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
