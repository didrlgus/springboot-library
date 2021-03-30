package gabia.library.service;

import gabia.library.domain.Alert;
import gabia.library.domain.AlertRepository;
import gabia.library.dto.AlertResponseDto;
import gabia.library.exception.AlertIdentifierException;
import gabia.library.exception.EntityNotFoundException;
import gabia.library.mapper.AlertMapper;
import gabia.library.utils.page.PageUtils;
import gabia.library.utils.page.PagingResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static gabia.library.exception.message.AlertExceptionMessage.INVALID_IDENTIFIER_VALUE;
import static gabia.library.exception.message.CommonExceptionMessage.ENTITY_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class AlertService {

    private final AlertRepository alertRepository;
    private final PageUtils pageUtils;

    private static final int ALERT_PAGE_SIZE = 10;
    private static final int ALERT_SCALE_SIZE = 10;

    public PagingResponseDto getAlertOfUser(Integer page, String identifier) {

        Page<Alert> alertPage = alertRepository.findAllByIdentifier(identifier, pageUtils.getPageable(page, ALERT_PAGE_SIZE, Sort.Direction.DESC, "createdDate"));

        List<AlertResponseDto.Normal> alertResponseDtoList = alertPage.stream().map(AlertMapper.INSTANCE::alertToAlertNormalResponseDto).collect(Collectors.toList());

        return pageUtils.getCommonPagingResponseDto(alertPage, alertResponseDtoList, ALERT_SCALE_SIZE);
    }

    public AlertResponseDto.Details getAlertsDetails(Long id, String identifier) {
        Alert alert = alertRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        if (!alert.getIdentifier().equals(identifier)) {
            throw new AlertIdentifierException(INVALID_IDENTIFIER_VALUE);
        }

        return AlertMapper.INSTANCE.alertToAlertDetailsResponseDto(alert);
    }
}
