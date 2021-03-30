package gabia.library.kafka;

import com.fasterxml.jackson.annotation.JsonFormat;
import gabia.library.utils.alert.AlertType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookRentMessage {

    private Long bookId;
    private String identifier;
    private String email;
    private String bookTitle;
    private String bookAuthor;
    private AlertType alertType;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate rentExpiredDate;

}
