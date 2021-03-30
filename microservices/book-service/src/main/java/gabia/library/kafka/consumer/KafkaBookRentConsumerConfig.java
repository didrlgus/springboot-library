package gabia.library.kafka.consumer;

import gabia.library.kafka.BookRentMessage;
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
public class KafkaBookRentConsumerConfig {

    @Value("${kafka.bootstrapServers}")
    private String bootstrapServers;

    @Value("${kafka.consumer.rent.groupName}")
    private String groupName;

    @Bean(name = "bookRentConsumerFactory")
    public ConsumerFactory<String, BookRentMessage> bookRentConsumerFactory() {
        JsonDeserializer<BookRentMessage> deserializer = JsonDeserializer();

        return new DefaultKafkaConsumerFactory<>(
                consumerFactoryConfig(deserializer),
                new StringDeserializer(),
                deserializer);
    }

    @Bean(name = "kafkaBookRentListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, BookRentMessage> kafkaBookRentListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BookRentMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(bookRentConsumerFactory());

        return factory;
    }

    @ConditionalOnMissingBean(name = "kafkaBookRentListenerContainerFactory")
    private Map<String, Object> consumerFactoryConfig(JsonDeserializer<BookRentMessage> deserializer) {
        return KafkaUtils.consumerFactoryConfig(bootstrapServers, groupName, deserializer);
    }

    private JsonDeserializer<BookRentMessage> JsonDeserializer() {
        JsonDeserializer<BookRentMessage> deserializer = new JsonDeserializer<>(BookRentMessage.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        return deserializer;
    }

}
