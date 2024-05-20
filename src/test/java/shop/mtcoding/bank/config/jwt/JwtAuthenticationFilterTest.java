package shop.mtcoding.bank.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.user.UserReqDto;
import shop.mtcoding.bank.dto.user.UserReqDto.LoginReqDto;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//가짜 환경에 스프링에 있는 환경들을 스캔해서 올려줘야 함
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc //mock 환경에 mvc가 new돼서 들어감
@ActiveProfiles("test")
@Sql("classpath:db/teadown.sql") //SpringBootTest 하는 곳에는 전부 다 teadown.sql을 붙여주자 (@Transactional 대신!!)
public class JwtAuthenticationFilterTest extends DummyObject {

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() throws  Exception{
       userRepository.save(newUser("ssar","쌀"));
    }
    @Test
    public void successfulAuthentication_test() throws Exception{
        //given - LoginReqDto 생성
        LoginReqDto loginReqDto = new LoginReqDto();
        loginReqDto.setUsername("ssar");
        loginReqDto.setPassword("1234");
        String requestBody = om.writeValueAsString(loginReqDto);
        System.out.println("requestBody = " + requestBody);

        //when - 강제 로그인
        ResultActions resultActions = mvc.perform(
                post("/api/login")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        String responseBody = resultActions.andReturn()
                .getResponse().getContentAsString();
        String jwtToken = resultActions.andReturn()
                        .getResponse()
                                .getHeader(JwtVo.HEADER);
        System.out.println("responseBody = " + responseBody);
        System.out.println("jwtToken = " + jwtToken);

        //then
        resultActions.andExpect(status().isOk());
        assertNotNull(jwtToken);
        assertTrue(jwtToken.startsWith(JwtVo.TOKEN_PREFIX));
        resultActions.andExpect(jsonPath("$.data.username").value("ssar"));
    }

    @Test
    public void unsuccessfulAuthentication_test() throws Exception{
        //given - LoginReqDto 생성
        LoginReqDto loginReqDto = new LoginReqDto();
        loginReqDto.setUsername("ssar");
        loginReqDto.setPassword("12345");
        String requestBody = om.writeValueAsString(loginReqDto);
        System.out.println("requestBody = " + requestBody);

        //when - 강제 로그인
        ResultActions resultActions = mvc.perform(
                post("/api/login")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        String responseBody = resultActions.andReturn()
                .getResponse().getContentAsString();
        String jwtToken = resultActions.andReturn()
                .getResponse()
                .getHeader(JwtVo.HEADER);
        //System.out.println("responseBody = " + responseBody);
        //System.out.println("jwtToken = " + jwtToken);

        //then
        resultActions.andExpect(status().isUnauthorized());
    }

}
