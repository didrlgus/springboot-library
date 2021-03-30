package gabia.library.kafka.consumer;

import gabia.library.kafka.ReviewUpdateOfBookMessage;
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
public class KafkaReviewUpdateOfBookConsumerConfig {

    @Value("${kafka.bootstrapServers}")
    private String bootstrapServers;

    @Value("${kafka.consumer.review.update.groupName}")
    private String groupName;

    @Bean(name = "reviewUpdateOfBookConsumerFactory")
    public ConsumerFactory<String, ReviewUpdateOfBookMessage> reviewUpdateOfBookConsumerFactory() {
        JsonDeserializer<ReviewUpdateOfBookMessage> deserializer = JsonDeserializer();

        return new DefaultKafkaConsumerFactory<>(
                consumerFactoryConfig(deserializer),
                new StringDeserializer(),
                deserializer);
    }

    @Bean(name = "kafkaReviewUpdateOfBookListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, ReviewUpdateOfBookMessage> kafkaReviewUpdateOfBookListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ReviewUpdateOfBookMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(reviewUpdateOfBookConsumerFactory());

        return factory;
    }

    @ConditionalOnMissingBean(name = "kafkaReviewUpdateOfBookListenerContainerFactory")
    private Map<String, Object> consumerFactoryConfig(JsonDeserializer<ReviewUpdateOfBookMessage> deserializer) {
        return KafkaUtils.consumerFactoryConfig(bootstrapServers, groupName, deserializer);
    }

    private JsonDeserializer<ReviewUpdateOfBookMessage> JsonDeserializer() {
        JsonDeserializer<ReviewUpdateOfBookMessage> deserializer = new JsonDeserializer<>(ReviewUpdateOfBookMessage.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        return deserializer;
    }
}
