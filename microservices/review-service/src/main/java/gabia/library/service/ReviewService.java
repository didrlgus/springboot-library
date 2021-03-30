package gabia.library.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import gabia.library.domain.Review;
import gabia.library.domain.ReviewRepository;
import gabia.library.dto.BookRequestDto;
import gabia.library.dto.ReviewRequestDto;
import gabia.library.dto.ReviewResponseDto;
import gabia.library.exception.*;
import gabia.library.kafka.sender.KafkaReviewCreateMessageSender;
import gabia.library.mapper.ReviewMapper;
import gabia.library.utils.jwt.JwtUtils;
import gabia.library.utils.page.PageUtils;
import gabia.library.utils.page.PagingResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

import static gabia.library.config.CommonUrlPathPrefix.BOOK_SERVICE_PREFIX;
import static gabia.library.exception.message.CommonExceptionMessage.*;
import static gabia.library.exception.message.ReviewExceptionMessage.INVALID_REVIEW_IDENTIFIER;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewService {

    private final static String REVIEW_IN_BOOK_URL = BOOK_SERVICE_PREFIX + "/books/";

    private final ReviewRepository reviewRepository;
    private final RestTemplate restTemplate;
    private final JwtUtils jwtUtils;
    private final PageUtils pageUtils;
    private final KafkaReviewCreateMessageSender kafkaReviewCreateMessageSender;

    private static final int REVIEWS_OF_USER_PAGE = 10;
    private static final int REVIEW_OF_USER_SCALE_SIZE = 10;
    private static final int REVIEWS_OF_BOOK_PAGE = 10;
    private static final int REVIEW_OF_BOOK_SCALE_SIZE = 10;

    @Transactional
    public ReviewResponseDto.Add addReview(Long bookId, ReviewRequestDto.Post reviewRequestDto, String identifier) {

        /**
         * send to add review message to kafka
         */
        kafkaReviewCreateMessageSender.send(reviewRequestDto.toReviewCreateMessage(bookId, identifier));

        return ReviewMapper.INSTANCE.reviewRequestDtoToResponseDto(reviewRequestDto);
    }

    public PagingResponseDto getReviewsOfUser(String identifier, Integer page) {
        if (pageUtils.isInvalidPageValue(page)) {
            throw new InvalidPageValueException(INVALID_PAGE_VALUE);
        }

        Page<Review> reviewPage = reviewRepository.findAllByIsDeletedAndIdentifier(false, identifier,
                pageUtils.getPageable(page, REVIEWS_OF_USER_PAGE, Sort.Direction.DESC, "createdDate"));

        return pageUtils.getCommonPagingResponseDto(reviewPage, getReviewResponseDtoList(reviewPage), REVIEW_OF_USER_SCALE_SIZE);
    }

    public ReviewResponseDto.Details getReviewDetails(Long id) {

        return ReviewMapper.INSTANCE.reviewToReviewDetailsResponseDto(reviewRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException(ENTITY_NOT_FOUND)));
    }

    public PagingResponseDto getReviewsOfBook(Long bookId, Integer page) {
        if (pageUtils.isInvalidPageValue(page)) {
            throw new InvalidPageValueException(INVALID_PAGE_VALUE);
        }

        Page<Review> reviewPage = reviewRepository.findAllByIsDeletedAndBookId(false, bookId,
                pageUtils.getPageable(page, REVIEWS_OF_BOOK_PAGE, Sort.Direction.DESC, "createdDate"));

        return pageUtils.getCommonPagingResponseDto(reviewPage, getReviewResponseDtoList(reviewPage), REVIEW_OF_BOOK_SCALE_SIZE);
    }

    @Transactional
    public ReviewResponseDto.Normal updateReview(Long id, ReviewRequestDto.Put reviewRequestDto, String jwt) {
        Review review = reviewRepository.findByIdAndIsDeleted(id, false).orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        String identifier = jwtUtils.getIdentifierFromJwt(jwt);

        if(!review.getIdentifier().equals(identifier)) {
            throw new InvalidReviewIdentifierException(INVALID_REVIEW_IDENTIFIER);
        }

        review.update(reviewRequestDto);

        return ReviewMapper.INSTANCE.reviewToReviewResponseDto(review);
    }

    @Transactional
    public ReviewResponseDto.Delete deleteReview(Long bookId, Long reviewId, String jwt) throws JsonProcessingException {
        Review review = reviewRepository.findByIdAndIsDeleted(reviewId, false).orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        String identifier = jwtUtils.getIdentifierFromJwt(jwt);

        if(!review.getIdentifier().equals(identifier)) {
            throw new InvalidReviewIdentifierException(INVALID_REVIEW_IDENTIFIER);
        }

        review.delete();

        ResponseEntity<ReviewResponseDto.Delete> response
                = restTemplate.exchange(REVIEW_IN_BOOK_URL + bookId + "/reviews",
                HttpMethod.PUT,
                new HttpEntity<>(BookRequestDto.builder().rating(review.getRating()).build(),
                jwtUtils.getHttpHeadersIncludedJwt(jwt)), ReviewResponseDto.Delete.class);

        return response.getBody();
    }

    private List<ReviewResponseDto.Normal> getReviewResponseDtoList(Page<Review> reviewPage) {

        return reviewPage.stream().map(ReviewMapper.INSTANCE::reviewToReviewResponseDto).collect(Collectors.toList());
    }

}
