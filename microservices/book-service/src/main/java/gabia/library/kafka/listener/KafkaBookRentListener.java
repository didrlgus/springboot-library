package gabia.library.kafka.listener;

import gabia.library.domain.book.Book;
import gabia.library.domain.book.BookRepository;
import gabia.library.domain.rent.Rent;
import gabia.library.domain.rent.RentRepository;
import gabia.library.exception.EntityNotFoundException;
import gabia.library.kafka.BookRentMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static gabia.library.exception.message.CommonExceptionMessage.ENTITY_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class KafkaBookRentListener {

    private final BookRepository bookRepository;
    private final RentRepository rentRepository;

    @Transactional
    @KafkaListener(topics = "${kafka.topic.rent.name}",
            groupId = "${kafka.consumer.rent.groupName}",
            containerFactory = "kafkaBookRentListenerContainerFactory")
    public void addRentAndUpdateBook(@Payload BookRentMessage message,
                                  @Headers MessageHeaders messageHeaders) {

        Book book = bookRepository.findByIdAndIsDeleted(message.getBookId(), false).orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        Rent rent = rentRepository.save(book.toRent(message.getIdentifier(), message.getRentExpiredDate()));

        book.rent(rent.getIdentifier(), rent.getId());
    }

}
