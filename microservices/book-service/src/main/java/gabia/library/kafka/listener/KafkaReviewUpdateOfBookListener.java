package gabia.library.kafka.listener;

import gabia.library.domain.book.Book;
import gabia.library.domain.book.BookRepository;
import gabia.library.exception.EntityNotFoundException;
import gabia.library.kafka.ReviewCreateResultMessage;
import gabia.library.kafka.status.ReviewStatus;
import gabia.library.kafka.ReviewUpdateOfBookMessage;
import gabia.library.kafka.sender.KafkaReviewCreateResultMessageSender;
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
public class KafkaReviewUpdateOfBookListener {

    private final BookRepository bookRepository;
    private final KafkaReviewCreateResultMessageSender kafkaReviewCreateResultMessageSender;

    @Transactional
    @KafkaListener(topics = "${kafka.topic.review.update.name}",
            groupId = "${kafka.consumer.review.update.groupName}",
            containerFactory = "kafkaReviewUpdateOfBookListenerContainerFactory")
    public void updateReview(@Payload ReviewUpdateOfBookMessage message,
                                     @Headers MessageHeaders messageHeaders) {

        try {
            Book book = bookRepository.findById(message.getBookId()).orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

            book.addReviewRating(message);

            bookRepository.save(book);

            /**
             * send review create result `COMPLETED` message to kafka
             */
            kafkaReviewCreateResultMessageSender.send(ReviewCreateResultMessage.builder()
                    .reviewId(message.getReviewId())
                    .reviewStatus(ReviewStatus.COMPLETED)
                    .build());
        } catch (Exception exception) {

            log.info("[gabia.library.kafka.listener.updateReview] failed create review exception {}", exception.getMessage());

            /**
             * send review create result `CANCELED` message to kafka
             */
            kafkaReviewCreateResultMessageSender.send(ReviewCreateResultMessage.builder()
                    .reviewId(message.getReviewId())
                    .reviewStatus(ReviewStatus.CANCELED)
                    .build());
        }
    }

}
