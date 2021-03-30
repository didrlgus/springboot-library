package gabia.library.filter;

import gabia.library.config.JwtConfig;
import gabia.library.config.MockMvcTest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Date;

import static org.apache.http.HttpStatus.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
public class JwtFilterTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtConfig jwtConfig;

    private static final int DAY = 24 * 60 * 60;
    private static final String USER_AUTH = "[ROLE_USER]";
    private static final String ADMIN_AUTH = "[ROLE_ADMIN]";

    private static final String USER_AUTH_TEST_URL =  "/users";
    private static final String ADMIN_AUTH_TEST_URL = "/admin";

    /**
        유효한 JWT로 api 요청 시 : 404 status code response
        ADMIN 권한을 요구하는 api를 USER 권한의 JWT로 요청 시 : 403 status code response
        request header에 JWT를 포함하지 않고 권한이 필요한 API 요청 시 : 401 status code response
        유효기간이 지난 JWT로 api 권한이 필요한 API 요청 시 : 401 status code response
     */

//    @DisplayName("유효한 JWT로 JWT filter 테스트")
//    @Test
//    public void jwtFilterByValidJwtTest() throws Exception {
//
//        assertEquals(callAPIAndGetStatusCode(USER_AUTH_TEST_URL, makeValidJwt(USER_AUTH)), SC_NOT_FOUND);
//    }
//
//    @DisplayName("USER 권한을 갖는 JWT로 ADMIN 권한을 요구하는 요청 시 JWT filter 테스트")
//    @Test
//    public void jwtFilterAuthTest() throws Exception {
//
//        assertEquals(callAPIAndGetStatusCode(ADMIN_AUTH_TEST_URL, makeValidJwt(USER_AUTH)), SC_FORBIDDEN);
//    }
//
//    @DisplayName("ADMIN 권한을 갖는 JWT로 ADMIN 권한을 요구하는 요청 시 JWT filter 테스트")
//    @Test
//    public void jwtFilterAdminAuthTest() throws Exception {
//        System.out.println(ADMIN_AUTH_TEST_URL);
//        assertEquals(callAPIAndGetStatusCode(ADMIN_AUTH_TEST_URL, makeValidJwt(ADMIN_AUTH)), SC_NOT_FOUND);
//    }
//
//    @DisplayName("요청 header에 JWT를 포함하지 않고 권한이 필요한 API 요청 시 JWT filter 테스트")
//    @Test
//    public void jwtFilterByNoJwtTest() throws Exception {
//
//        assertEquals(callAPIAndGetStatusCode(USER_AUTH_TEST_URL, null), SC_UNAUTHORIZED);
//    }
//
//    @DisplayName("유효기간이 지난 JWT로 JWT filter 테스트")
//    @Test
//    public void jwtFilterByExpiredJwtTest() throws Exception {
//
//        assertEquals(callAPIAndGetStatusCode(USER_AUTH_TEST_URL, makeExpiredUserJwt()), SC_UNAUTHORIZED);
//    }

    private int callAPIAndGetStatusCode(String url, String jwt) throws Exception {
        return mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getStatus();
    }

    private String makeValidJwt(String authority) {
        Instant now = Instant.now();

        return Jwts.builder()
                .setSubject("wade")
                .claim("authorities", authority)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(jwtConfig.getExpiration())))
                .signWith(SignatureAlgorithm.HS256, jwtConfig.getSecret().getBytes())
                .compact();
    }

    private String makeExpiredUserJwt() {
        Instant fiveDaysAgo = Instant.now().minusSeconds(5 * DAY);

        return Jwts.builder()
                .setSubject("wade")
                .claim("authorities", USER_AUTH)
                .setIssuedAt(Date.from(fiveDaysAgo))
                .setExpiration(Date.from(fiveDaysAgo.plusSeconds(jwtConfig.getExpiration())))
                .signWith(SignatureAlgorithm.HS256, jwtConfig.getSecret().getBytes())
                .compact();
    }

}
