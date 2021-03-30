package gabia.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gabia.library.config.JwtConfig;
import gabia.library.config.MockMvcTest;
import gabia.library.domain.Review;
import gabia.library.domain.ReviewRepository;
import gabia.library.dto.ReviewRequestDto;
import gabia.library.dto.ReviewResponseDto;
import gabia.library.utils.page.PageResponseData;
import gabia.library.utils.page.PagingResponseDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
public class ReviewApiTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext ctx;
    @Autowired
    private JwtConfig jwtConfig;
    @Autowired
    private ReviewRepository reviewRepository;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();
    }

    @AfterEach
    public void after() {
        reviewRepository.deleteAll();
    }

    @DisplayName("특정 유저의 리뷰리스트 조회 테스트")
    @Test
    public void getReviewsOfUserTest() throws Exception {
        String jwt = makeTestJwt();

        reviewRepository.save(getTestReview());

        int curPage = 1;

        PagingResponseDto pagingResponseDto = objectMapper.readValue(getReviewsOfUserAPI(curPage, jwt, status().is2xxSuccessful()).getContentAsString(), PagingResponseDto.class);

        List<?> reviewList = pagingResponseDto.getResponseDtoList();
        PageResponseData pagingResponseData = pagingResponseDto.getPageResponseData();

        assertEquals(reviewList.size(), 1);
        assertEquals(pagingResponseData.getPage(), curPage);
        assertEquals(pagingResponseData.getScaleStartPage(), 1);
        assertEquals(pagingResponseData.getScaleEndPage(), 1);
        assertNull(pagingResponseData.getPrevPage());
        assertNull(pagingResponseData.getNextPage());
    }

    @DisplayName("특정 유저의 리뷰리스트 조회 페이징 테스트")
    @Test
    public void getReviewOfUserPagingTest() throws Exception {
        String jwt = makeTestJwt();

        int curPage = 25;
        int totalReviewDataSize = 500;
        int expectedScaleStartPage = 21;
        int expectedScaleEndPage = 30;
        int expectedPrevPage = 20;
        int expectedNextPage = 31;

        for (int i = 0; i < totalReviewDataSize; i++) {
            reviewRepository.save(getTestReview());
        }

        PagingResponseDto pagingResponseDto = objectMapper.readValue(getReviewsOfUserAPI(curPage, jwt, status().is2xxSuccessful()).getContentAsString(), PagingResponseDto.class);

        List<?> reviewList = pagingResponseDto.getResponseDtoList();
        PageResponseData pagingResponseData = pagingResponseDto.getPageResponseData();

        assertEquals(10, reviewList.size());
        assertEquals(curPage, pagingResponseData.getPage());
        assertEquals(expectedScaleStartPage, pagingResponseData.getScaleStartPage());
        assertEquals(expectedScaleEndPage, pagingResponseData.getScaleEndPage());
        assertEquals(expectedPrevPage, pagingResponseData.getPrevPage());
        assertEquals(expectedNextPage, pagingResponseData.getNextPage());
    }

    @DisplayName("리뷰 상세 조회 테스트")
    @Test
    public void getReviewsDetailsTest() throws Exception {
        Review review = reviewRepository.save(getTestReview());

        ReviewResponseDto.Details responseDto = objectMapper.readValue(getReviewDetailsAPI(review.getId(), status().is2xxSuccessful()).getContentAsString(), ReviewResponseDto.Details.class);

        assertEquals(review.getId(), responseDto.getId());
        assertEquals(review.getBookId(), responseDto.getBookId());
        assertEquals(review.getIdentifier(), responseDto.getIdentifier());
        assertEquals(review.getRating(), responseDto.getRating());
        assertEquals(review.getContent(), responseDto.getContent());
    }

    @DisplayName("특정 책의 리뷰리스트 조회 테스트")
    @Test
    public void getReviewsOfBookTest() throws Exception {
        Review review = reviewRepository.save(getTestReview());

        int curPage = 1;

        PagingResponseDto pagingResponseDto = objectMapper.readValue(getReviewsOfBookAPI(review.getBookId(), curPage, status().is2xxSuccessful()).getContentAsString(), PagingResponseDto.class);

        List<?> reviewList = pagingResponseDto.getResponseDtoList();
        PageResponseData pagingResponseData = pagingResponseDto.getPageResponseData();

        assertEquals(reviewList.size(), 1);
        assertEquals(pagingResponseData.getPage(), curPage);
        assertEquals(pagingResponseData.getScaleStartPage(), 1);
        assertEquals(pagingResponseData.getScaleEndPage(), 1);
        assertNull(pagingResponseData.getPrevPage());
        assertNull(pagingResponseData.getNextPage());
    }

    @DisplayName("리뷰 업데이트 테스트")
    @Test
    public void updateReviewTest() throws Exception {
        String jwt = makeTestJwt();

        Review review = reviewRepository.save(getTestReview());

        ReviewResponseDto.Normal responseDto = objectMapper.readValue(updateReviewAPI(review.getId(), getReviewPutRequestDto(), jwt, status().is2xxSuccessful()).getContentAsString(), ReviewResponseDto.Normal.class);

        assertEquals(review.getId(), responseDto.getId());
        assertEquals(review.getBookId(), responseDto.getBookId());
        assertEquals(review.getIdentifier(), responseDto.getIdentifier());
    }

    private ReviewRequestDto.Put getReviewPutRequestDto() {
        return ReviewRequestDto.Put.builder()
                .title("update title test")
                .content("update content test")
                .build();
    }

    private MockHttpServletResponse updateReviewAPI(Long id, ReviewRequestDto.Put requestDto, String jwt, ResultMatcher status) throws Exception {
        return mockMvc.perform(put("/reviews/" + id)
                .header(jwtConfig.getHeader(), jwtConfig.getPrefix() + " " + jwt)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status)
                .andReturn().getResponse();
    }

    private MockHttpServletResponse getReviewsOfBookAPI(Long id, int page, ResultMatcher status) throws Exception {
        return mockMvc.perform(get("/books/" + id + "/reviews?page=" + page)
                .contentType(APPLICATION_JSON))
                .andExpect(status)
                .andReturn().getResponse();
    }

    private MockHttpServletResponse getReviewDetailsAPI(Long id, ResultMatcher status) throws Exception {
        return mockMvc.perform(get("/books/reviews/" + id)
                .contentType(APPLICATION_JSON))
                .andExpect(status)
                .andReturn().getResponse();
    }

    private MockHttpServletResponse getReviewsOfUserAPI(int page, String jwt, ResultMatcher status) throws Exception {
        return mockMvc.perform(get("/books/reviews?page=" + page)
                .header(jwtConfig.getHeader(), jwtConfig.getPrefix() + " " + jwt)
                .contentType(APPLICATION_JSON))
                .andExpect(status)
                .andReturn().getResponse();
    }

    private Review getTestReview() {
        return Review.builder()
                .bookId(1L)
                .identifier("test")
                .title("test title")
                .content("test content")
                .rating(3)
                .isDeleted(false)
                .build();
    }

    private String makeTestJwt() {
        Instant now = Instant.now();

        return Jwts.builder()
                .setSubject("test")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(jwtConfig.getExpiration())))
                .signWith(SignatureAlgorithm.HS256, jwtConfig.getSecret().getBytes())
                .compact();
    }
}
