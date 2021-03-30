package gabia.library.kafka.listener;

import gabia.library.domain.Review;
import gabia.library.domain.ReviewRepository;
import gabia.library.exception.EntityNotFoundException;
import gabia.library.kafka.ReviewCreateResultMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static gabia.library.exception.message.CommonExceptionMessage.ENTITY_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaReviewCreateResultListener {

    private final ReviewRepository reviewRepository;

    @Transactional
    @KafkaListener(topics = "${kafka.topic.create.result.name}",
            groupId = "${kafka.consumer.create.result.groupName}",
            containerFactory = "kafkaReviewCreateResultListenerContainerFactory")
    public void addCreateReviewResult(@Payload ReviewCreateResultMessage message,
                                      @Headers MessageHeaders messageHeaders) {

        try {
            Review review = reviewRepository.findById(message.getReviewId()).orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

            review.updateReviewStatus(message.getReviewStatus());
        } catch (Exception exception) {
            log.info("[gabia.library.kafka.listener.addCreateReviewResult] create review failed! {}", exception.getMessage());
        }
    }

}
