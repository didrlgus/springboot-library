package gabia.library.kafka.listener;

import gabia.library.domain.book.Book;
import gabia.library.domain.book.BookRepository;
import gabia.library.kafka.BookBuyingMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class KafkaBookBuyingListener {

    private final BookRepository bookRepository;

    @Transactional
    @KafkaListener(topics = "${kafka.topic.buying.name}",
            groupId = "${kafka.consumer.buying.groupName}",
            containerFactory = "kafkaBookBuyingListenerContainerFactory")
    public void addBook(@Payload BookBuyingMessage message,
                                     @Headers MessageHeaders messageHeaders) {

        bookRepository.save(getBookByMessage(message));

    }

    public Book getBookByMessage(BookBuyingMessage message) {
        return Book.builder()
                .title(message.getBookTitle())
                .author(message.getBookAuthor())
                .publisher(message.getPublisher())
                .publishDate(message.getPublishDate())
                .referenceUrl(message.getReferenceUrl())
                .location(message.getDestination())
                .thumbnail(message.getThumbnail())
                .content(message.getContent())
                .isRent(false)
                .isDeleted(false)
                .extensionCount(0)
                .totalRating(0)
                .reviewCount(0)
                .build();
    }

}
