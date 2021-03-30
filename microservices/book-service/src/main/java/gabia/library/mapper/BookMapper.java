package gabia.library.mapper;

import gabia.library.domain.book.Book;
import gabia.library.domain.rent.Rent;
import gabia.library.dto.BookResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookMapper {

    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "rentId", target = "rentId")
    @Mapping(source = "identifier", target = "identifier")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "author", target = "author")
    @Mapping(source = "publisher", target = "publisher")
    @Mapping(source = "publishDate", target = "publishDate")
    @Mapping(source = "category", target = "category")
    @Mapping(source = "intro", target = "intro")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "referenceUrl", target = "referenceUrl")
    @Mapping(source = "location", target = "location")
    @Mapping(source = "thumbnail", target = "thumbnail")
    @Mapping(source = "rent", target = "isRent")
    @Mapping(source = "etc", target = "etc")
    @Mapping(source = "extensionCount", target = "extensionCount")
    @Mapping(source = "reviewCount", target = "reviewCount")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    BookResponseDto bookToBookResponseDto(Book book);

    @Mapping(source = "book.id", target = "id")
    @Mapping(source = "book.identifier", target = "identifier")
    @Mapping(source = "book.title", target = "title")
    @Mapping(source = "book.author", target = "author")
    @Mapping(source = "book.publisher", target = "publisher")
    @Mapping(source = "book.publishDate", target = "publishDate")
    @Mapping(source = "book.category", target = "category")
    @Mapping(source = "book.intro", target = "intro")
    @Mapping(source = "book.content", target = "content")
    @Mapping(source = "book.referenceUrl", target = "referenceUrl")
    @Mapping(source = "book.location", target = "location")
    @Mapping(source = "book.thumbnail", target = "thumbnail")
    @Mapping(source = "book.rent", target = "isRent")
    @Mapping(source = "book.etc", target = "etc")
    @Mapping(source = "book.extensionCount", target = "extensionCount")
    @Mapping(source = "book.reviewCount", target = "reviewCount")
    @Mapping(source = "book.createdDate", target = "createdDate")
    @Mapping(source = "book.modifiedDate", target = "modifiedDate")
    @Mapping(source = "book.rentExpiredDate", target = "rentExpiredDate")
    @Mapping(source = "rent.id", target = "rentId")
    BookResponseDto bookToBookAndRentResponseDto(Book book, Rent rent);
}
