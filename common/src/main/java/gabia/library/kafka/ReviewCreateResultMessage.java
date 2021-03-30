package gabia.library.kafka;

import gabia.library.kafka.status.ReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewCreateResultMessage {

    private Long reviewId;

    private ReviewStatus reviewStatus;

}
