package gabia.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gabia.library.config.JwtConfig;
import gabia.library.config.MockMvcTest;
import gabia.library.dto.BookRequestDto;
import gabia.library.dto.BookResponseDto;
import gabia.library.exception.ErrorResponse;
import gabia.library.utils.page.PagingResponseDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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

import static gabia.library.exception.message.CommonExceptionMessage.INVALID_INPUT_VALUE;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * TODO: 책 조회, 책 상세 API는 review-service와 연동되므로 review 서비스와 연동되므로 추후에 TC 작성
 */

@MockMvcTest
public class BookApiTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext ctx;
    @Autowired
    private JwtConfig jwtConfig;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();
    }

    @DisplayName("책 추가 테스트")
    @Test
    public void addBookTest() throws Exception {
        BookRequestDto.Post bookRequestDto = getPostRequestDto();
        // 책 추가 API 호출
        BookResponseDto bookResponseDto = objectMapper.readValue(addBookAPI(bookRequestDto, status().is2xxSuccessful()).getContentAsString(), BookResponseDto.class);

        assertEquals(bookRequestDto.getTitle(), bookResponseDto.getTitle());
    }

    @DisplayName("책 추가 valid check(null check) 테스트")
    @Test
    public void addBookNullCheckTest() throws Exception {
        BookRequestDto.Post bookRequestDto = getPostRequestDtoForValidCheck(null);
        // 책 추가 API 호출
        ErrorResponse errorResponse = objectMapper.readValue(addBookAPI(bookRequestDto, status().is4xxClientError()).getContentAsString(), ErrorResponse.class);

        assertEquals(errorResponse.getMessage(), INVALID_INPUT_VALUE);
    }

    @DisplayName("책 추가 valid check(length check) 테스트")
    @Test
    public void addBookLengthCheckTest() throws Exception {
        StringBuilder longTitle = new StringBuilder();

        while(longTitle.length() <= 255) {
            longTitle.append('a');
        }

        BookRequestDto.Post bookRequestDto = getPostRequestDtoForValidCheck(longTitle.toString());
        // 책 추가 API 호출
        ErrorResponse errorResponse = objectMapper.readValue(addBookAPI(bookRequestDto, status().is4xxClientError()).getContentAsString(), ErrorResponse.class);

        assertEquals(errorResponse.getMessage(), INVALID_INPUT_VALUE);
    }

    @DisplayName("책 수정 테스트")
    @Test
    public void updateBookTest() throws Exception {
        BookRequestDto.Post bookRequestDto = getPostRequestDto();
        // 책 추가 API 호출
        BookResponseDto bookResponseDto = objectMapper.readValue(addBookAPI(bookRequestDto, status().is2xxSuccessful()).getContentAsString(), BookResponseDto.class);

        BookRequestDto.Put bookPutRequestDto = getPutRequestDto();
        // 책 수정 api 호출
        BookResponseDto updateBookResponseDto = objectMapper.readValue(updateBookAPI(bookResponseDto.getId(), bookPutRequestDto, status().is2xxSuccessful()).getContentAsString(), BookResponseDto.class);

        assertEquals(bookPutRequestDto.getTitle(), updateBookResponseDto.getTitle());
        assertEquals(bookPutRequestDto.getAuthor(), updateBookResponseDto.getAuthor());
        assertEquals(bookPutRequestDto.getPublisher(), updateBookResponseDto.getPublisher());
    }

    @DisplayName("책 삭제 테스트")
    @Test
    public void deleteBookTest() throws Exception {
        BookRequestDto.Post bookRequestDto = getPostRequestDto();
        // 책 추가 API 호출
        BookResponseDto bookResponseDto = objectMapper.readValue(addBookAPI(bookRequestDto, status().is2xxSuccessful()).getContentAsString(), BookResponseDto.class);

        // 책 삭제 API 호출
        MockHttpServletResponse deleteResponse = deleteBookAPI(bookResponseDto.getId(), status().is2xxSuccessful());

        BookResponseDto deleteBookResponseDto = objectMapper.readValue(deleteResponse.getContentAsString(), BookResponseDto.class);

        assertEquals(bookResponseDto.getTitle(), deleteBookResponseDto.getTitle());
    }

    @DisplayName("최신 도서 10개 조회 테스트")
    @Test
    public void getLatestBooksTest() throws Exception {
        for (int i = 0; i < 50; i++) {
            addBookAPI(getPostRequestDto(), status().is2xxSuccessful());
        }

        List<BookResponseDto> bookResponseDtoList = objectMapper.readValue(getLatestOrManyReviewsBookAPI("/books/latest", status().is2xxSuccessful()).getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, BookResponseDto.class));


        assertEquals(10, bookResponseDtoList.size());
    }

    @DisplayName("리뷰개수 많은 10개 조회 테스트")
    @Test
    public void getManyReviewsBooksTest() throws Exception {
        for (int i = 0; i < 50; i++) {
            addBookAPI(getPostRequestDto(), status().is2xxSuccessful());
        }

        List<BookResponseDto> bookResponseDtoList = objectMapper.readValue(getLatestOrManyReviewsBookAPI("/books/many-reviews", status().is2xxSuccessful()).getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, BookResponseDto.class));


        assertEquals(10, bookResponseDtoList.size());
    }

    @DisplayName("책 검색 테스트")
    @Test
    public void getSearchedBookTest() throws Exception {
        for (int i = 0; i < 50; i++) {
            addBookAPI(getPostRequestDto(), status().is2xxSuccessful());
        }

        PagingResponseDto responseDto = objectMapper.readValue(getSearchedBookAPI(1, "스프", status().is2xxSuccessful()).getContentAsString(), PagingResponseDto.class);

        assertEquals(1, responseDto.getPageResponseData().getPage());
        assertEquals(5, responseDto.getPageResponseData().getTotalPage());
        assertEquals(12, responseDto.getResponseDtoList().size());
    }

//    @DisplayName("책 대여 테스트")
//    @Test
//    public void rentBookTest() throws Exception {
//        BookRequestDto.Post bookRequestDto = getPostRequestDto();
//        // 책 추가 API 호출
//        BookResponseDto bookResponseDto = objectMapper.readValue(addBookAPI(bookRequestDto, status().is2xxSuccessful()).getContentAsString(), BookResponseDto.class);
//
//        String testJwt = makeTestJwt();
//        // 책 대여 API 호출
//        MockHttpServletResponse rentResponse = rentBookAPI(bookResponseDto.getId(), testJwt, status().is2xxSuccessful());
//
//        BookResponseDto rentBookResponseDto = objectMapper.readValue(rentResponse.getContentAsString(), BookResponseDto.class);
//
//        assertTrue(rentBookResponseDto.isRent());
//        assertEquals("test", rentBookResponseDto.getIdentifier());
//        assertEquals(LocalDate.now().plusMonths(1), rentBookResponseDto.getRentExpiredDate());
//    }
//
//
//    @DisplayName("책 대여 중복 테스트")
//    @Test
//    public void rentBookDuplicatedTest() throws Exception {
//        BookRequestDto.Post bookRequestDto = getPostRequestDto();
//
//        BookResponseDto bookResponseDto = objectMapper.readValue(addBookAPI(bookRequestDto, status().is2xxSuccessful()).getContentAsString(), BookResponseDto.class);
//
//        String testJwt = makeTestJwt();
//        // 책 대여 API 호출
//        rentBookAPI(bookResponseDto.getId(), testJwt, status().is2xxSuccessful());
//
//        // 이미 대여 완료된 책에 한번 더 대여 API 호출
//        MockHttpServletResponse dupResponse = rentBookAPI(bookResponseDto.getId(), testJwt, status().is4xxClientError());
//
//        ErrorResponse errorResponse = objectMapper.readValue(dupResponse.getContentAsString(), ErrorResponse.class);
//
//        assertEquals(errorResponse.getMessage(), ALREADY_RENT);
//    }
//
//    @DisplayName("책 대여연장 테스트")
//    @Test
//    public void extensionBookTest() throws Exception {
//        BookRequestDto.Post bookRequestDto = getPostRequestDto();
//        // 책 추가 API 호출
//        BookResponseDto bookResponseDto = objectMapper.readValue(addBookAPI(bookRequestDto, status().is2xxSuccessful()).getContentAsString(), BookResponseDto.class);
//
//        String testJwt = makeTestJwt();
//
//        // 책 대여 API 호출
//        rentBookAPI(bookResponseDto.getId(), testJwt, status().is2xxSuccessful());
//
//        // 책 대여연장 API 호출
//        MockHttpServletResponse extensionResponse = extensionBookAPI(bookResponseDto.getId(), testJwt, status().is2xxSuccessful());
//
//        BookResponseDto bookExtensionResponseDto = objectMapper.readValue(extensionResponse.getContentAsString(), BookResponseDto.class);
//
//        assertEquals( bookResponseDto.getExtensionCount() + 1, bookExtensionResponseDto.getExtensionCount());
//        assertEquals(LocalDate.now().plusMonths(2), bookExtensionResponseDto.getRentExpiredDate());
//    }
//
//    @DisplayName("올바르지 않은 id의 jwt를 통한 책 연장 테스트")
//    @Test
//    public void bookRentWithUnValidJwtTest() throws Exception {
//        BookRequestDto.Post bookRequestDto = getPostRequestDto();
//        // 책 추가 API 호출
//        BookResponseDto bookResponseDto = objectMapper.readValue(addBookAPI(bookRequestDto, status().is2xxSuccessful()).getContentAsString(), BookResponseDto.class);
//
//        String validJwt = makeTestJwt();
//
//        // 책 대여 API 호출
//        rentBookAPI(bookResponseDto.getId(), validJwt, status().is2xxSuccessful());
//
//        String unValidJwt = makeUnValidTestJwt();
//
//        // 책 대여연장 API 호출
//        MockHttpServletResponse extensionResponse = extensionBookAPI(bookResponseDto.getId(), unValidJwt, status().is4xxClientError());
//
//        ErrorResponse errorResponse = objectMapper.readValue(extensionResponse.getContentAsString(), ErrorResponse.class);
//
//        assertEquals(errorResponse.getMessage(), INVALID_IDENTIFIER_VALUE);
//    }
//
//    @DisplayName("책 대여 3회 초과 테스트")
//    @Test
//    public void extensionBookExceptionTest() throws Exception {
//        BookRequestDto.Post bookRequestDto = getPostRequestDto();
//        // 책 추가 API 호출
//        BookResponseDto bookResponseDto = objectMapper.readValue(addBookAPI(bookRequestDto, status().is2xxSuccessful()).getContentAsString(), BookResponseDto.class);
//
//        String testJwt = makeTestJwt();
//
//        // 책 대여 API 호출
//        rentBookAPI(bookResponseDto.getId(), testJwt, status().is2xxSuccessful());
//
//        // 책 대여연장 API 3회 호출
//        for(int i = 0; i < 3; i++) {
//            extensionBookAPI(bookResponseDto.getId(), testJwt, status().is2xxSuccessful());
//        }
//
//        // 책 대여연장 API 호출
//        MockHttpServletResponse extensionResponse = extensionBookAPI(bookResponseDto.getId(), testJwt, status().is4xxClientError());
//
//        ErrorResponse errorResponse = objectMapper.readValue(extensionResponse.getContentAsString(), ErrorResponse.class);
//
//        assertEquals(errorResponse.getMessage(), INVALID_EXTENSION_COUNT_VALUE);
//    }
//
//    @DisplayName("책 반납 테스트")
//    @Test
//    public void returnBookTest() throws Exception {
//        BookRequestDto.Post bookRequestDto = getPostRequestDto();
//        // 책 추가 API 호출
//        BookResponseDto bookResponseDto = objectMapper.readValue(addBookAPI(bookRequestDto, status().is2xxSuccessful()).getContentAsString(), BookResponseDto.class);
//
//        String testJwt = makeTestJwt();
//
//        // 책 대여 API 호출
//        rentBookAPI(bookResponseDto.getId(), testJwt, status().is2xxSuccessful());
//
//        // 책 반환 API 호출
//        MockHttpServletResponse returnResponse = returnBookAPI(bookResponseDto.getId(), testJwt, status().is2xxSuccessful());
//
//        BookResponseDto returnBookResponseDto = objectMapper.readValue(returnResponse.getContentAsString(), BookResponseDto.class);
//
//        assertNull(returnBookResponseDto.getIdentifier());
//        assertFalse(returnBookResponseDto.isRent());
//        assertEquals(returnBookResponseDto.getExtensionCount(), 0);
//        assertNull(returnBookResponseDto.getRentExpiredDate());
//    }

    private BookRequestDto.Post getPostRequestDto() {
        return BookRequestDto.Post.builder()
                .title("토비의 스프링")
                .author("토비")
                .publisher("토비 출판사")
                .publishDate("2018-01-01")
                .category("프로그래밍 언어")
                .intro("토비의 스프링 인트로 입니다.")
                .content("토비의 스프링 컨텐트 입니다.")
                .referenceUrl("www.books.com/1/1")
                .location("5층 전공")
                .thumbnail("www.books.com/image=")
                .etc("333쪽/200g/20*20*20")
                .build();
    }

    private MockHttpServletResponse addBookAPI(BookRequestDto.Post bookRequestDto, ResultMatcher status) throws Exception {
        return mockMvc.perform(post("/books")
                .contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(bookRequestDto)))
                .andExpect(status)
                .andReturn().getResponse();
    }

    private BookRequestDto.Put getPutRequestDto() {
        return BookRequestDto.Put.builder()
                .title("마스터링 스프링 클라우드")
                .author("wade")
                .publisher("위키북스")
                .build();
    }

    private BookRequestDto.Post getPostRequestDtoForValidCheck(String title) {
        return BookRequestDto.Post.builder()
                .title(title)
                .author("토비")
                .publisher("토비 출판사")
                .build();
    }

    private MockHttpServletResponse getLatestOrManyReviewsBookAPI(String url, ResultMatcher status) throws Exception {
        return mockMvc.perform(get(url)
                .contentType(APPLICATION_JSON)).andExpect(status)
                .andReturn().getResponse();
    }

    private MockHttpServletResponse updateBookAPI(Long id, BookRequestDto.Put bookPutRequestDto, ResultMatcher status) throws Exception {
        return mockMvc.perform(put("/books/" + id)
                .contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(bookPutRequestDto)))
                .andExpect(status)
                .andReturn().getResponse();
    }

    private MockHttpServletResponse deleteBookAPI(Long id, ResultMatcher status) throws Exception {
        return mockMvc.perform(delete("/books/" + id)
                .contentType(APPLICATION_JSON))
                .andExpect(status)
                .andReturn().getResponse();
    }

    private MockHttpServletResponse getSearchedBookAPI(int page, String keyword, ResultMatcher status) throws Exception {
        return mockMvc.perform(get("/books/search?page=" + page + "&keyword=" + keyword)
                .contentType(APPLICATION_JSON))
                .andExpect(status)
                .andReturn().getResponse();
    }

    private MockHttpServletResponse rentBookAPI(Long id, String jwt, ResultMatcher status) throws Exception {
        return mockMvc.perform(put("/books/" + id + "/rent")
                .header(jwtConfig.getHeader(), jwtConfig.getPrefix() + " " + jwt)
                .contentType(APPLICATION_JSON))
                .andExpect(status)
                .andReturn().getResponse();
    }

    private MockHttpServletResponse extensionBookAPI(Long id, String jwt, ResultMatcher status) throws Exception {
        return mockMvc.perform(put("/books/" + id + "/extension")
                .header(jwtConfig.getHeader(), jwtConfig.getPrefix() + " " + jwt)
                .contentType(APPLICATION_JSON))
                .andExpect(status)
                .andReturn().getResponse();
    }

    private MockHttpServletResponse returnBookAPI(Long id, String jwt, ResultMatcher status) throws Exception {
        return mockMvc.perform(put("/books/" + id + "/return")
                .header(jwtConfig.getHeader(), jwtConfig.getPrefix() + " " + jwt)
                .contentType(APPLICATION_JSON))
                .andExpect(status)
                .andReturn().getResponse();
    }

    public String makeTestJwt() {
        Instant now = Instant.now();

        return Jwts.builder()
                .setSubject("test")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(jwtConfig.getExpiration())))
                .signWith(SignatureAlgorithm.HS256, jwtConfig.getSecret().getBytes())
                .compact();
    }

    public String makeUnValidTestJwt() {
        Instant now = Instant.now();

        return Jwts.builder()
                .setSubject("unValid")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(jwtConfig.getExpiration())))
                .signWith(SignatureAlgorithm.HS256, jwtConfig.getSecret().getBytes())
                .compact();
    }
}
