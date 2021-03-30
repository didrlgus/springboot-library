package gabia.library.kafka;

import gabia.library.utils.alert.AlertType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookBuyingMessage {

    private String identifier;
    private String email;
    private String bookTitle;
    private String bookAuthor;
    private AlertType alertType;
    private String destination;
    private String requestState;
    private String publisher;
    private String publishDate;
    private String referenceUrl;
    private String thumbnail;
    private String content;

}
