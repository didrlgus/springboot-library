package gabia.library.kafka;

import com.fasterxml.jackson.annotation.JsonFormat;
import gabia.library.utils.alert.AlertType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookReturnMessage {

    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private Long rentId;
    private String identifier;
    private String email;
    private AlertType alertType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime returnDateTime;

}
