package gabia.library.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import gabia.library.config.BookRequestJsonView;
import gabia.library.domain.Destination;
import gabia.library.domain.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class BookRequestDto {

    private Long id;

    @Column(name = "user_id")
    @JsonView({BookRequestJsonView.Request.class, BookRequestJsonView.Cancel.class})
    private Long userId;

    @Column(name = "book_name")
    @JsonView({BookRequestJsonView.List.class, BookRequestJsonView.Request.class, BookRequestJsonView.Cancel.class})
    private String bookName;

    @JsonView(BookRequestJsonView.List.class)
    private String author;

    @JsonView({BookRequestJsonView.List.class, BookRequestJsonView.Request.class})
    private Destination destination;

    @JsonView(BookRequestJsonView.List.class)
    private String url;

    @JsonView(BookRequestJsonView.List.class)
    private Status status;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    private String publisher;
    private String publishDate;
    private String thumbnail;
    private String content;

    @Column(name = "created_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime updatedDate;
}
