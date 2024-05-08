package shop.mtcoding.bank.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.account.AccountRespDto;
import shop.mtcoding.bank.service.AccountService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shop.mtcoding.bank.dto.account.AccountRespDto.*;

@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
public class AccountControllerTest extends DummyObject {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ObjectMapper om;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mvc;
    @BeforeEach
    public void setUp(){
        userRepository.save(newUser("ssar","쌀"));
    }

    //jwt token -> 인증필터 -> 시큐리티 세션 생성. 따라서 시큐리티 세션을 만들어면 됨
    //setupBefore=TEST_METHOD (setUp 메서드 실행 전에 수행)
    //setupBefore = TestExecutionEvent.TEST_EXECUTION (saveAccount_test 메서드 실행 전에 수행)
    @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION) //디비에서 username=ssar 조회를 해서 세션에 담아주는 어노테이션
    @Test
    public void saveAccount_test() throws Exception{

        //given
        AccountSaveReqDto accountSaveReqDto = new AccountSaveReqDto();
        accountSaveReqDto.setNumber(9999L);
        accountSaveReqDto.setPassword(1234L);
        String requestBody = om.writeValueAsString(accountSaveReqDto);
        System.out.println("requestBody = " + requestBody);

        //when
        ResultActions resultActions =
                mvc.perform(post("/api/s/account")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("responseBody = " + responseBody);

        //then
        resultActions.andExpect(status().isCreated());
    }

}
