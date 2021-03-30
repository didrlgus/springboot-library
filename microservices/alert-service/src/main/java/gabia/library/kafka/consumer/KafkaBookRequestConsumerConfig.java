package gabia.library.kafka.consumer;

import gabia.library.kafka.BookRequestMessage;
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
public class KafkaBookRequestConsumerConfig {

    @Value("${kafka.bootstrapServers}")
    private String bootstrapServers;

    @Value("${kafka.consumer.request.alert.groupName}")
    private String bookRequestAlertGroup;

    @Value("${kafka.consumer.request.mail.groupName}")
    private String bookRequestMailGroup;

    @Bean(name = "bookRequestAlertConsumerFactory")
    public ConsumerFactory<String, BookRequestMessage> bookRequestAlertConsumerFactory() {
        JsonDeserializer<BookRequestMessage> deserializer = JsonDeserializer();

        return new DefaultKafkaConsumerFactory<>(
                bookRequestAlertConsumerFactoryConfig(deserializer),
                new StringDeserializer(),
                deserializer);
    }

    @Bean(name = "bookRequestMailConsumerFactory")
    public ConsumerFactory<String, BookRequestMessage> bookRequestMailConsumerFactory() {
        JsonDeserializer<BookRequestMessage> deserializer = JsonDeserializer();

        return new DefaultKafkaConsumerFactory<>(
                bookRequestMailConsumerFactoryConfig(deserializer),
                new StringDeserializer(),
                deserializer);
    }

    @Bean(name = "bookRequestAlertListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, BookRequestMessage> bookRequestAlertListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BookRequestMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(bookRequestAlertConsumerFactory());

        return factory;
    }

    @Bean(name = "bookRequestMailListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, BookRequestMessage> bookRequestMailListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BookRequestMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(bookRequestMailConsumerFactory());

        return factory;
    }

    @ConditionalOnMissingBean(name = "bookRequestAlertListenerContainerFactory")
    private Map<String, Object> bookRequestAlertConsumerFactoryConfig(JsonDeserializer<BookRequestMessage> deserializer) {
        return KafkaUtils.consumerFactoryConfig(bootstrapServers, bookRequestAlertGroup, deserializer);
    }

    @ConditionalOnMissingBean(name = "bookRequestMailListenerContainerFactory")
    private Map<String, Object> bookRequestMailConsumerFactoryConfig(JsonDeserializer<BookRequestMessage> deserializer) {
        return KafkaUtils.consumerFactoryConfig(bootstrapServers, bookRequestMailGroup, deserializer);
    }

    private JsonDeserializer<BookRequestMessage> JsonDeserializer() {
        JsonDeserializer<BookRequestMessage> deserializer = new JsonDeserializer<>(BookRequestMessage.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        return deserializer;
    }

}
