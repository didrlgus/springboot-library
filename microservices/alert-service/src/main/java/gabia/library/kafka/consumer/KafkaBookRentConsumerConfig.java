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

    @Value("${kafka.consumer.rent.alert.groupName}")
    private String bookRentAlertGroup;

    @Value("${kafka.consumer.rent.mail.groupName}")
    private String bookRentMailGroup;

    @Bean(name = "bookRentAlertConsumerFactory")
    public ConsumerFactory<String, BookRentMessage> bookRentAlertConsumerFactory() {
        JsonDeserializer<BookRentMessage> deserializer = JsonDeserializer();

        return new DefaultKafkaConsumerFactory<>(
                bookRentAlertConsumerFactoryConfig(deserializer),
                new StringDeserializer(),
                deserializer);
    }

    @Bean(name = "bookRentMailConsumerFactory")
    public ConsumerFactory<String, BookRentMessage> bookRentMailConsumerFactory() {
        JsonDeserializer<BookRentMessage> deserializer = JsonDeserializer();

        return new DefaultKafkaConsumerFactory<>(
                bookRentMailConsumerFactoryConfig(deserializer),
                new StringDeserializer(),
                deserializer);
    }

    @Bean(name = "bookRentAlertListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, BookRentMessage> bookRentAlertListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BookRentMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(bookRentAlertConsumerFactory());

        return factory;
    }

    @Bean(name = "bookRentMailListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, BookRentMessage> bookRentMailListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BookRentMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(bookRentMailConsumerFactory());

        return factory;
    }

    @ConditionalOnMissingBean(name = "bookRentAlertListenerContainerFactory")
    private Map<String, Object> bookRentAlertConsumerFactoryConfig(JsonDeserializer<BookRentMessage> deserializer) {
        return KafkaUtils.consumerFactoryConfig(bootstrapServers, bookRentAlertGroup, deserializer);
    }

    @ConditionalOnMissingBean(name = "bookRentMailListenerContainerFactory")
    private Map<String, Object> bookRentMailConsumerFactoryConfig(JsonDeserializer<BookRentMessage> deserializer) {
        return KafkaUtils.consumerFactoryConfig(bootstrapServers, bookRentMailGroup, deserializer);
    }

    private JsonDeserializer<BookRentMessage> JsonDeserializer() {
        JsonDeserializer<BookRentMessage> deserializer = new JsonDeserializer<>(BookRentMessage.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        return deserializer;
    }

}
