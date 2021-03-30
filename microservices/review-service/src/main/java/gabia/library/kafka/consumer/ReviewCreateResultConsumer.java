package gabia.library.kafka.consumer;

import gabia.library.kafka.ReviewCreateResultMessage;
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
public class ReviewCreateResultConsumer {

    @Value("${kafka.bootstrapServers}")
    private String bootstrapServers;

    @Value("${kafka.consumer.create.result.groupName}")
    private String groupName;

    @Bean(name = "reviewCreateResultConsumerFactory")
    public ConsumerFactory<String, ReviewCreateResultMessage> reviewCreateResultConsumerFactory() {
        JsonDeserializer<ReviewCreateResultMessage> deserializer = JsonDeserializer();

        return new DefaultKafkaConsumerFactory<>(
                consumerFactoryConfig(deserializer),
                new StringDeserializer(),
                deserializer);
    }

    @Bean(name = "kafkaReviewCreateResultListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, ReviewCreateResultMessage> kafkaReviewCreateResultListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ReviewCreateResultMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(reviewCreateResultConsumerFactory());

        return factory;
    }

    @ConditionalOnMissingBean(name = "kafkaReviewCreateResultListenerContainerFactory")
    private Map<String, Object> consumerFactoryConfig(JsonDeserializer<ReviewCreateResultMessage> deserializer) {
        return KafkaUtils.consumerFactoryConfig(bootstrapServers, groupName, deserializer);
    }

    private JsonDeserializer<ReviewCreateResultMessage> JsonDeserializer() {
        JsonDeserializer<ReviewCreateResultMessage> deserializer = new JsonDeserializer<>(ReviewCreateResultMessage.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        return deserializer;
    }
}
