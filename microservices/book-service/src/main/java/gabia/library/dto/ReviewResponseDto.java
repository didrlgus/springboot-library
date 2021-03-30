package gabia.library.dto;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponseDto {

    private String identifier;

    private Double avgReviewRating;

    private Integer reviewCount;

}
