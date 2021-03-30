package gabia.library.mapper;

import gabia.library.domain.Alert;
import gabia.library.dto.AlertResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AlertMapper {

    AlertMapper INSTANCE = Mappers.getMapper(AlertMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "identifier", target = "identifier")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "alertType", target = "alertType")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    AlertResponseDto.Normal alertToAlertNormalResponseDto(Alert alert);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "identifier", target = "identifier")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "message", target = "message")
    @Mapping(source = "alertType", target = "alertType")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    AlertResponseDto.Details alertToAlertDetailsResponseDto(Alert alert);

}
