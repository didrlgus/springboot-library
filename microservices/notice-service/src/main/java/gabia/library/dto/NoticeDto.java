package gabia.library.dto;

import com.fasterxml.jackson.annotation.JsonView;
import gabia.library.config.NoticeJsonView;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class NoticeDto {

    @JsonView(NoticeJsonView.Modify.class)
    private Long id;

    @JsonView(NoticeJsonView.Modify.class)
    private String title;

    @JsonView(NoticeJsonView.Modify.class)
    private String content;

    @Column(name = "is_important")
    @JsonView(NoticeJsonView.Modify.class)
    private boolean isImportant;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "created_date")
    @JsonView(NoticeJsonView.Default.class)
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
}
