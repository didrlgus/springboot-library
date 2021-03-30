package gabia.library.kafka.listener;

import gabia.library.domain.Review;
import gabia.library.domain.ReviewRepository;
import gabia.library.kafka.message.ReviewCreateMessage;
import gabia.library.kafka.sender.KafkaReviewUpdateOfBookMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaReviewCreateListener {

    private final ReviewRepository reviewRepository;
    private final KafkaReviewUpdateOfBookMessageSender kafkaReviewUpdateOfBookMessageSender;

    @Transactional
    @KafkaListener(topics = "${kafka.topic.create.name}",
            groupId = "${kafka.consumer.create.groupName}",
            containerFactory = "kafkaReviewCreateListenerContainerFactory")
    public void addReview(@Payload ReviewCreateMessage message,
                        @Headers MessageHeaders messageHeaders) {

        try {
            Review review = reviewRepository.save(message.toEntity());

            /**
             * send review-update-of-book event to kafka
             */
            kafkaReviewUpdateOfBookMessageSender.send(review.toReviewUpdateOfBookMessage());
        } catch (Exception exception) {
            log.info("[gabia.library.kafka.listener.addReview] failed create review exception {}", exception.getMessage());
        }
    }

}
