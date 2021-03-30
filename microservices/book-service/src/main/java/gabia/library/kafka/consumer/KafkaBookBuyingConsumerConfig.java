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

    @Value("${kafka.consumer.buying.groupName}")
    private String groupName;

    @Bean(name = "bookBuyingConsumerFactory")
    public ConsumerFactory<String, BookBuyingMessage> bookBuyingConsumerFactory() {
        JsonDeserializer<BookBuyingMessage> deserializer = JsonDeserializer();

        return new DefaultKafkaConsumerFactory<>(
                consumerFactoryConfig(deserializer),
                new StringDeserializer(),
                deserializer);
    }

    @Bean(name = "kafkaBookBuyingListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, BookBuyingMessage> kafkaBookBuyingListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BookBuyingMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(bookBuyingConsumerFactory());

        return factory;
    }

    @ConditionalOnMissingBean(name = "kafkaBookBuyingListenerContainerFactory")
    private Map<String, Object> consumerFactoryConfig(JsonDeserializer<BookBuyingMessage> deserializer) {
        return KafkaUtils.consumerFactoryConfig(bootstrapServers, groupName, deserializer);
    }

    private JsonDeserializer<BookBuyingMessage> JsonDeserializer() {
        JsonDeserializer<BookBuyingMessage> deserializer = new JsonDeserializer<>(BookBuyingMessage.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        return deserializer;
    }


}
