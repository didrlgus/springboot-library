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

    @Value("${kafka.consumer.return.alert.groupName}")
    private String bookReturnAlertGroup;

    @Value("${kafka.consumer.return.mail.groupName}")
    private String bookReturnMailGroup;

    @Bean(name = "bookReturnAlertConsumerFactory")
    public ConsumerFactory<String, BookReturnMessage> bookReturnAlertConsumerFactory() {
        JsonDeserializer<BookReturnMessage> deserializer = JsonDeserializer();

        return new DefaultKafkaConsumerFactory<>(
                bookReturnAlertConsumerFactoryConfig(deserializer),
                new StringDeserializer(),
                deserializer);
    }

    @Bean(name = "bookReturnMailConsumerFactory")
    public ConsumerFactory<String, BookReturnMessage> bookReturnMailConsumerFactory() {
        JsonDeserializer<BookReturnMessage> deserializer = JsonDeserializer();

        return new DefaultKafkaConsumerFactory<>(
                bookReturnMailConsumerFactoryConfig(deserializer),
                new StringDeserializer(),
                deserializer);
    }

    @Bean(name = "bookReturnAlertListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, BookReturnMessage> bookReturnAlertListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BookReturnMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(bookReturnAlertConsumerFactory());

        return factory;
    }

    @Bean(name = "bookReturnMailListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, BookReturnMessage> bookReturnMailListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BookReturnMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(bookReturnMailConsumerFactory());

        return factory;
    }

    @ConditionalOnMissingBean(name = "bookReturnAlertListenerContainerFactory")
    private Map<String, Object> bookReturnAlertConsumerFactoryConfig(JsonDeserializer<BookReturnMessage> deserializer) {
        return KafkaUtils.consumerFactoryConfig(bootstrapServers, bookReturnAlertGroup, deserializer);
    }

    @ConditionalOnMissingBean(name = "bookReturnMailListenerContainerFactory")
    private Map<String, Object> bookReturnMailConsumerFactoryConfig(JsonDeserializer<BookReturnMessage> deserializer) {
        return KafkaUtils.consumerFactoryConfig(bootstrapServers, bookReturnMailGroup, deserializer);
    }

    private JsonDeserializer<BookReturnMessage> JsonDeserializer() {
        JsonDeserializer<BookReturnMessage> deserializer = new JsonDeserializer<>(BookReturnMessage.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        return deserializer;
    }

}
