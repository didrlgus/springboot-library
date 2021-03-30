package gabia.library.dto;

import lombok.*;

@ToString
@Setter
@Getter
@NoArgsConstructor
public class ReviewRequestDto {

    private Integer rating;

    @Builder
    public ReviewRequestDto(Integer rating) {
        this.rating = rating;
    }
}
