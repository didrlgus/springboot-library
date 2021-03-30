package gabia.library.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewUpdateOfBookMessage {

    private Long reviewId;

    private Long bookId;

    private Integer reviewRating;

}
