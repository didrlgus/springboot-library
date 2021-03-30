package gabia.library.kafka.message;

import gabia.library.domain.Review;
import gabia.library.kafka.status.ReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewCreateMessage {

    private Long bookId;

    private String identifier;

    private String reviewTitle;

    private Integer reviewRating;

    private String reviewContent;

    private ReviewStatus reviewStatus;

    public Review toEntity() {

        return Review.builder()
                .bookId(this.bookId)
                .identifier(this.identifier)
                .title(this.reviewTitle)
                .rating(this.reviewRating)
                .content(this.reviewContent)
                .reviewStatus(reviewStatus)
                .isDeleted(true)
                .build();

    }
}
