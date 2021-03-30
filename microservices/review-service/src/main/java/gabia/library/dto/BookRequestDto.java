package gabia.library.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BookRequestDto {

    Integer rating;

    @Builder
    public BookRequestDto(Integer rating) {
        this.rating = rating;
    }
}
