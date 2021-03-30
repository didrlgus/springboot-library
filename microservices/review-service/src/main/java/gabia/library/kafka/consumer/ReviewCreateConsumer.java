package gabia.library.kafka.consumer;

import gabia.library.kafka.config.KafkaUtils;
import gabia.library.kafka.message.ReviewCreateMessage;
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
public class ReviewCreateConsumer {

    @Value("${kafka.bootstrapServers}")
    private String bootstrapServers;

    @Value("${kafka.consumer.create.groupName}")
    private String groupName;

    @Bean(name = "reviewCreateConsumerFactory")
    public ConsumerFactory<String, ReviewCreateMessage> reviewCreateConsumerFactory() {
        JsonDeserializer<ReviewCreateMessage> deserializer = JsonDeserializer();

        return new DefaultKafkaConsumerFactory<>(
                consumerFactoryConfig(deserializer),
                new StringDeserializer(),
                deserializer);
    }

    @Bean(name = "kafkaReviewCreateListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, ReviewCreateMessage> kafkaReviewCreateListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ReviewCreateMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(reviewCreateConsumerFactory());

        return factory;
    }

    @ConditionalOnMissingBean(name = "kafkaReviewCreateListenerContainerFactory")
    private Map<String, Object> consumerFactoryConfig(JsonDeserializer<ReviewCreateMessage> deserializer) {
        return KafkaUtils.consumerFactoryConfig(bootstrapServers, groupName, deserializer);
    }

    private JsonDeserializer<ReviewCreateMessage> JsonDeserializer() {
        JsonDeserializer<ReviewCreateMessage> deserializer = new JsonDeserializer<>(ReviewCreateMessage.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        return deserializer;
    }
}
