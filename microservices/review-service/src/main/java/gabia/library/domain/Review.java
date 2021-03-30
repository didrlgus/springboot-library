package gabia.library.domain;

import gabia.library.dto.ReviewRequestDto;
import gabia.library.kafka.status.ReviewStatus;
import gabia.library.kafka.ReviewUpdateOfBookMessage;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@ToString
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Long bookId;

    @Column(length = 25, nullable = false)
    private String identifier;                              // 유저 아이디

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private Integer rating;

    @Lob
    private String content;

    @Column(columnDefinition = "boolean default false", nullable = false)
    private boolean isDeleted;

    @Enumerated(EnumType.STRING)
    private ReviewStatus reviewStatus;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    public void update(ReviewRequestDto.Put reviewRequestDto) {
        this.title = reviewRequestDto.getTitle();
        this.content = reviewRequestDto.getContent();
    }

    public void delete() {
        this.isDeleted = true;
    }

    public ReviewUpdateOfBookMessage toReviewUpdateOfBookMessage() {

        return ReviewUpdateOfBookMessage.builder()
                .reviewId(this.id)
                .bookId(this.bookId)
                .reviewRating(this.rating)
                .build();
    }

    public void updateReviewStatus(ReviewStatus reviewStatus) {
        this.reviewStatus = reviewStatus;
        if (reviewStatus == ReviewStatus.COMPLETED) {
            this.isDeleted = false;
        }
    }
}
