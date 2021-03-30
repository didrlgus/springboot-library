package gabia.library.dto;

import gabia.library.domain.Review;
import gabia.library.kafka.status.ReviewStatus;
import gabia.library.kafka.message.ReviewCreateMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ReviewRequestDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Post {
        @Size(min = 1, max = 255)
        @NotNull(message = "제목은 null일 수 없습니다.")
        private String title;

        @Min(0)
        @Max(5)
        @NotNull(message = "평점은 null일 수 없습니다.")
        private Integer rating;

        @NotNull(message = "내용은 null일 수 없습니다.")
        private String content;

        public Review toEntity(Long bookId, String identifier) {
            return Review.builder()
                    .bookId(bookId)
                    .title(this.title)
                    .identifier(identifier)
                    .rating(this.rating)
                    .content(this.content)
                    .isDeleted(false)
                    .build();
        }

        public ReviewCreateMessage toReviewCreateMessage(Long bookId, String identifier) {

            return ReviewCreateMessage.builder()
                    .bookId(bookId)
                    .identifier(identifier)
                    .reviewTitle(this.title)
                    .reviewRating(this.rating)
                    .reviewContent(this.content)
                    .reviewStatus(ReviewStatus.PENDING)
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Put {

        @Size(min = 1, max = 255)
        @NotNull(message = "제목은 null일 수 없습니다.")
        private String title;

        @NotNull(message = "내용은 null일 수 없습니다.")
        private String content;
    }

}
