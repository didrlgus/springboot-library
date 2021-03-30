package gabia.library.dto;

import gabia.library.domain.BookRequest;
import gabia.library.domain.Status;
import gabia.library.kafka.BookBuyingMessage;
import gabia.library.kafka.BookRequestMessage;
import gabia.library.utils.alert.AlertType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserBookRequestDto {

    private String identifier;
    private String email;

    public BookRequestMessage toBookRequestMessage(BookRequestDto bookRequestDto) {

        return BookRequestMessage.builder()
                .identifier(identifier)
                .email(email)
                .bookTitle(bookRequestDto.getBookName())
                .bookAuthor(bookRequestDto.getAuthor())
                .destination(bookRequestDto.getDestination().value())
                .requestState(Status.REQUESTED.value())
                .alertType(AlertType.REQUESTED)
                .build();
    }

    public BookBuyingMessage toBookBuyingMessage(BookRequest bookRequest) {

        return BookBuyingMessage.builder()
                .identifier(identifier)
                .email(email)
                .bookTitle(bookRequest.getBookName())
                .bookAuthor(bookRequest.getAuthor())
                .referenceUrl(bookRequest.getUrl())
                .destination(bookRequest.getDestination().value())
                .thumbnail(bookRequest.getThumbNail())
                .publisher(bookRequest.getPublisher())
                .publishDate(bookRequest.getPublishDate())
                .requestState(Status.COMPLETED.value())
                .alertType(AlertType.DISPLAY_COMPLETED)
                .content(bookRequest.getContent())
                .build();
    }
}
