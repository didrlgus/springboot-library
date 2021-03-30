package gabia.library.mapper;

import gabia.library.domain.book.Book;
import gabia.library.domain.rent.Rent;
import gabia.library.dto.RentResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RentMapper {

    RentMapper INSTANCE = Mappers.getMapper(RentMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "bookId", target = "bookId")
    @Mapping(source = "bookTitle", target = "bookTitle")
    @Mapping(source = "bookAuthor", target = "bookAuthor")
    @Mapping(source = "identifier", target = "identifier")
    @Mapping(source = "rentStatus", target = "rentStatus")
    @Mapping(source = "rentExpiredDate", target = "rentExpiredDate")
    RentResponseDto rentToRentResponseDto(Rent rent);

    @Mapping(source = "id", target = "bookId")
    @Mapping(source = "title", target = "bookTitle")
    @Mapping(source = "author", target = "bookAuthor")
    RentResponseDto bookToRentResponseDto(Book book);
}
