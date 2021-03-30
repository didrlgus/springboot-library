package gabia.library.kafka.producer;

import gabia.library.kafka.BookReturnMessage;
import gabia.library.kafka.config.KafkaUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class KafkaBookReturnProducerConfig {

    @Value("${kafka.bootstrapServers}")
    private String bootstrapServers;

    @Bean(name = "bookReturnProducerFactory")
    public ProducerFactory<String, BookReturnMessage> producerFactory() {
        Map<String, Object> configProps = KafkaUtils.producerFactoryConfig(bootstrapServers);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean(name = "bookReturnKafkaTemplate")
    public KafkaTemplate<String, BookReturnMessage> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
