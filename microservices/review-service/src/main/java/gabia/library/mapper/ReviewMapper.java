package gabia.library.mapper;

import gabia.library.domain.Review;
import gabia.library.dto.ReviewRequestDto;
import gabia.library.dto.ReviewResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReviewMapper {

    ReviewMapper INSTANCE = Mappers.getMapper(ReviewMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "bookId", target = "bookId")
    @Mapping(source = "identifier", target = "identifier")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "rating", target = "rating")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    ReviewResponseDto.Normal reviewToReviewResponseDto(Review review);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "bookId", target = "bookId")
    @Mapping(source = "identifier", target = "identifier")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "rating", target = "rating")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    ReviewResponseDto.Details reviewToReviewDetailsResponseDto(Review review);

    @Mapping(source = "title", target = "title")
    @Mapping(source = "rating", target = "rating")
    @Mapping(source = "content", target = "content")
    ReviewResponseDto.Add reviewRequestDtoToResponseDto(ReviewRequestDto.Post reviewRequestDto);
}
