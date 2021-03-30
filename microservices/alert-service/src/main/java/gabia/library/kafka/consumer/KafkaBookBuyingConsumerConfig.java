package gabia.library.kafka.consumer;

import gabia.library.kafka.BookBuyingMessage;
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
public class KafkaBookBuyingConsumerConfig {

    @Value("${kafka.bootstrapServers}")
    private String bootstrapServers;

    @Value("${kafka.consumer.buying.alert.groupName}")
    private String bookBuyingAlertGroup;

    @Value("${kafka.consumer.buying.mail.groupName}")
    private String bookBuyingMailGroup;

    @Bean(name = "bookBuyingAlertConsumerFactory")
    public ConsumerFactory<String, BookBuyingMessage> bookBuyingAlertConsumerFactory() {
        JsonDeserializer<BookBuyingMessage> deserializer = JsonDeserializer();

        return new DefaultKafkaConsumerFactory<>(
                bookBuyingAlertConsumerFactoryConfig(deserializer),
                new StringDeserializer(),
                deserializer);
    }

    @Bean(name = "bookBuyingMailConsumerFactory")
    public ConsumerFactory<String, BookBuyingMessage> bookBuyingMailConsumerFactory() {
        JsonDeserializer<BookBuyingMessage> deserializer = JsonDeserializer();

        return new DefaultKafkaConsumerFactory<>(
                bookBuyingMailConsumerFactoryConfig(deserializer),
                new StringDeserializer(),
                deserializer);
    }

    @Bean(name = "bookBuyingAlertListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, BookBuyingMessage> bookBuyingAlertListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BookBuyingMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(bookBuyingAlertConsumerFactory());

        return factory;
    }

    @Bean(name = "bookBuyingMailListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, BookBuyingMessage> bookBuyingMailListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BookBuyingMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(bookBuyingMailConsumerFactory());

        return factory;
    }

    @ConditionalOnMissingBean(name = "bookBuyingAlertListenerContainerFactory")
    private Map<String, Object> bookBuyingAlertConsumerFactoryConfig(JsonDeserializer<BookBuyingMessage> deserializer) {
        return KafkaUtils.consumerFactoryConfig(bootstrapServers, bookBuyingAlertGroup, deserializer);
    }

    @ConditionalOnMissingBean(name = "bookBuyingMailListenerContainerFactory")
    private Map<String, Object> bookBuyingMailConsumerFactoryConfig(JsonDeserializer<BookBuyingMessage> deserializer) {
        return KafkaUtils.consumerFactoryConfig(bootstrapServers, bookBuyingMailGroup, deserializer);
    }

    private JsonDeserializer<BookBuyingMessage> JsonDeserializer() {
        JsonDeserializer<BookBuyingMessage> deserializer = new JsonDeserializer<>(BookBuyingMessage.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        return deserializer;
    }

}
