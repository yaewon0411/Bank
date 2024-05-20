package shop.mtcoding.bank.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.user.UserReqDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shop.mtcoding.bank.dto.user.UserReqDto.*;

@ActiveProfiles("test") //DummyDevInit에서 ssar을 db에 넣어주고 있음. 따라서 dev모드에서 동작 안하게 test로 지정
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Sql("classpath:db/teadown.sql")
public class UserControllerTest extends DummyObject { //SpringBootTest에서 하는 Controller 테스트는 통합 테스트

    //가짜 환경에 등록된 MockMvc를 DI
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;
    @BeforeEach
    public void setUp(){
        userRepository.save(newUser("ssar","쌀"));
        em.clear();
    }

    @Test
    public void join_success_test() throws Exception{
        //given
        JoinReqDto joinReqDto=  new JoinReqDto();
        joinReqDto.setUsername("love");
        joinReqDto.setFullname("러부");
        joinReqDto.setEmail("love2@naver.com");
        joinReqDto.setPassword("1234");

        String requestBody = om.writeValueAsString(joinReqDto);
        //System.out.println("requestBody = " + requestBody);

        //when
        ResultActions  resultActions = mvc.perform(post("/api/join")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));

//        mvc.perform(MockMvcRequestBuilders
//                .post("/api/join")
//                .content(requestBody)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isCreated());

        String responseBody = resultActions.andReturn()
                .getResponse()
                .getContentAsString();

        //then
        resultActions.andExpect(status().isCreated());

    }


    @Test
    public void join_fail_test() throws Exception{
        //given
        JoinReqDto joinReqDto=  new JoinReqDto();
        joinReqDto.setUsername("ssar");
        joinReqDto.setFullname("쌀");
        joinReqDto.setEmail("ssar@naver.com");
        joinReqDto.setPassword("1234");

        String requestBody = om.writeValueAsString(joinReqDto);
        //System.out.println("requestBody = " + requestBody);

        //when & then
        ResultActions resultActions =
                mvc.perform(MockMvcRequestBuilders
                        .post("/api/join")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest());

        String responseBody = resultActions.andReturn()
                .getResponse()
                .getContentAsString();

    }


}
