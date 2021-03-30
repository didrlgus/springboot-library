package gabia.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gabia.library.config.JwtConfig;
import gabia.library.config.MockMvcTest;
import gabia.library.domain.Alert;
import gabia.library.domain.AlertRepository;
import gabia.library.dto.AlertResponseDto;
import gabia.library.utils.alert.AlertType;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
public class AlertApiTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext ctx;
    @Autowired
    private JwtConfig jwtConfig;
    @Autowired
    private AlertRepository alertRepository;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();
    }

    @AfterEach
    public void after() {
        alertRepository.deleteAll();
    }

    @DisplayName("특정 유저의 알람 조회 테스트")
    @Test
    public void getAlertsOfUserTest() throws Exception {
        String jwt = makeTestJwt();

        alertRepository.save(getTestAlert());

        int curPage = 1;

        PagingResponseDto pagingResponseDto = objectMapper.readValue(getAlertsOfUserAPI(curPage, jwt, status().is2xxSuccessful()).getContentAsString(), PagingResponseDto.class);

        List<?> alertList = pagingResponseDto.getResponseDtoList();
        PageResponseData pagingResponseData = pagingResponseDto.getPageResponseData();

        assertEquals(alertList.size(), 1);
        assertEquals(pagingResponseData.getPage(), curPage);
        assertEquals(pagingResponseData.getScaleStartPage(), 1);
        assertEquals(pagingResponseData.getScaleEndPage(), 1);
        assertNull(pagingResponseData.getPrevPage());
        assertNull(pagingResponseData.getNextPage());
    }

    @DisplayName("알람 상세조회 테스트")
    @Test
    public void getAlertsDetailsTest() throws Exception {
        String jwt = makeTestJwt();

        Alert alert = alertRepository.save(getTestAlert());

        AlertResponseDto.Details responseDto = objectMapper.readValue(getAlertsDetailsAPI(alert.getId(), jwt, status().is2xxSuccessful()).getContentAsString(), AlertResponseDto.Details.class);

        assertEquals(alert.getId(), responseDto.getId());
        assertEquals(alert.getIdentifier(), responseDto.getIdentifier());
        assertEquals(alert.getEmail(), responseDto.getEmail());
        assertEquals(alert.getTitle(), responseDto.getTitle());
        assertEquals(alert.getMessage(), responseDto.getMessage());
        assertEquals(alert.getAlertType(), responseDto.getAlertType());
    }

    private MockHttpServletResponse getAlertsOfUserAPI(int page, String jwt, ResultMatcher status) throws Exception {
        return mockMvc.perform(get("/alerts?page=" + page)
                .header(jwtConfig.getHeader(), jwtConfig.getPrefix() + " " + jwt)
                .contentType(APPLICATION_JSON))
                .andExpect(status)
                .andReturn().getResponse();
    }

    private MockHttpServletResponse getAlertsDetailsAPI(Long id, String jwt, ResultMatcher status) throws Exception {
        return mockMvc.perform(get("/alerts/" + id)
                .header(jwtConfig.getHeader(), jwtConfig.getPrefix() + " " + jwt)
                .contentType(APPLICATION_JSON))
                .andExpect(status)
                .andReturn().getResponse();
    }

    private Alert getTestAlert() {
        return Alert.builder()
                .title("test title")
                .message("test message")
                .identifier("test")
                .email("test@gabia.com")
                .alertType(AlertType.RENT)
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
