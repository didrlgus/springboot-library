package gabia.library.kafka.consumer;

import gabia.library.kafka.BookReturnMessage;
import gabia.library.kafka.config.KafkaUtils;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

@EnableKafka
@Configuration
public class KafkaBookReturnConsumerConfig {

    @Value("${kafka.bootstrapServers}")
    private String bootstrapServers;

    @Value("${kafka.consumer.return.groupName}")
    private String groupName;

    @Bean(name = "bookReturnConsumerFactory")
    public ConsumerFactory<String, BookReturnMessage> bookReturnConsumerFactory() {
        JsonDeserializer<BookReturnMessage> deserializer = JsonDeserializer();

        return new DefaultKafkaConsumerFactory<>(
                consumerFactoryConfig(deserializer),
                new StringDeserializer(),
                deserializer);
    }

    @Bean(name = "kafkaBookReturnListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, BookReturnMessage> kafkaBookReturnListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BookReturnMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(bookReturnConsumerFactory());

        return factory;
    }

    @ConditionalOnMissingBean(name = "kafkaBookReturnListenerContainerFactory")
    private Map<String, Object> consumerFactoryConfig(JsonDeserializer<BookReturnMessage> deserializer) {
        return KafkaUtils.consumerFactoryConfig(bootstrapServers, groupName, deserializer);
    }

    private JsonDeserializer<BookReturnMessage> JsonDeserializer() {
        JsonDeserializer<BookReturnMessage> deserializer = new JsonDeserializer<>(BookReturnMessage.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        return deserializer;
    }

}
