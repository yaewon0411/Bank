package shop.mtcoding.bank.web;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.account.AccountReqDto;
import shop.mtcoding.bank.dto.account.AccountReqDto.AccountDepositReqDto;
import shop.mtcoding.bank.dto.account.AccountRespDto;
import shop.mtcoding.bank.ex.CustomApiException;
import shop.mtcoding.bank.service.AccountService;

import javax.xml.transform.Result;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shop.mtcoding.bank.dto.account.AccountReqDto.*;
import static shop.mtcoding.bank.dto.account.AccountRespDto.*;

@Sql("classpath:db/teadown.sql") //실행 시점 : BeforeEach 실행 직전마다!!
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
    private AccountRepository accountRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc mvc;
    @BeforeEach
    public void setUp(){

        User user = newUser("ssar","쌀");
        User cos = newUser("cos","코스");
        userRepository.save(user);
        userRepository.save(cos);

        Account ssarAccount = accountRepository.save(newAccount(1111L, user));
        Account ssarAccount2 = accountRepository.save(newAccount(1112L, user));
        Account cosAccount = accountRepository.save(newAccount(2222L, cos));
        em.clear();
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
    
    @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void findUserAccount() throws Exception{
        //given
        
        //when
        ResultActions resultActions = 
                mvc.perform(get("/api/s/account/login-user")
                        .contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("responseBody = " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.msg").value("유저별 계좌 목록 보기 성공"));
    }

    @Test
    @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    // 테스트 시에는 insert 한 것들이 전부 PC에 올라감 (영속화)
    // 영속화된 것들을 초기화 해주는 것이 개발 모드와 동일한 환경으로 테스트를 할 수 있게 해준다.
    // 최초 select가 발생하지만!! -> PC에 있으면 1차 캐시를 함
    // PC에 엔티티가 있다면? -> Lazy 로딩은 쿼리도 발생 안 함
    // 즉 Lazy 로딩할 때 PC에 없다면 쿼리가 발생함
    /*
    1. insert 4건 후 - PS 초기화
    2. @WithUserDetails에 의해 cos 조회 -> 쿼리 발생 & PS에 cos가 영속
    3. ssar의 account 조회 -> 쿼리 발생
    4. ssarAccount.getUser().getUsername() 조회 -> 쿼리 발생 (LAZY)
    //만약 ssarAccount.getUser().getId()를 하면 추가 쿼리 발생 안함 -> User 엔티티가 프록시 객체로 생성되어 실제 데이터를 로드하지 않는다 해도, User 엔티티의 ID는 이미 Account 객체에 외래키 값으로 존재하기 때문
     */
    public void deleteAccount_test() throws Exception{
        //given
        Long number = 1111L;

        //when
        ResultActions resultActions = mvc.perform(
                delete("/api/s/account/" + number));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("responseBody = " + responseBody);

        //then
        //JUnit 테스트에서 delete 쿼리는 DB 관련(DML)으로 가장 마지막에 실행되면 발동 안됨.
        //즉 후속 조회 쿼리가 있으면 그 때 콘솔에 delete 쿼리 보임
        assertThrows(CustomApiException.class, () -> accountRepository.findByNumber(number).orElseThrow(
                () -> new CustomApiException("계좌를 찾을 수 없습니다")
        ));
    }

    @Test
    public void 계좌입급_test() throws Exception{
        //given
        AccountDepositReqDto accountDepositReqDto =
                new AccountDepositReqDto(1111L, 10000L, "DEPOSIT","01088403980");
        String requestBody = om.writeValueAsString(accountDepositReqDto);

        //when
        ResultActions resultActions = mvc.perform(post("/api/account/deposit")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn
                ().getResponse().getContentAsString();
        //System.out.println("responseBody = " + responseBody);

        //then
        resultActions.andExpect(status().isCreated());
        resultActions.andExpect(jsonPath("$.msg").value("계좌 입금 완료"));
    }

    @Test
    @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void withdraw_test() throws Exception{
        //given
        AccountWithdrawReqDto accountWithdrawReqDto = new AccountWithdrawReqDto();
        accountWithdrawReqDto.setGubun("WITHDRAW");
        accountWithdrawReqDto.setAmount(100L);
        accountWithdrawReqDto.setPassword(1234L);
        accountWithdrawReqDto.setNumber(1111L);

        String requestBody = om.writeValueAsString(accountWithdrawReqDto);
        System.out.println("requestBody = " + requestBody);

        //when
        ResultActions resultActions = mvc.perform(post("/api/s/account/withdraw")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("responseBody = " + responseBody);
        //then

    }

    @Test
    @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void transfer_test() throws Exception{
        //given
        AccountTransferReqDto accountTransferReqDto = new AccountTransferReqDto();
        accountTransferReqDto.setAmount(100L);
        accountTransferReqDto.setGubun("TRANSFER");
        accountTransferReqDto.setWithdrawNumber(1111L);
        accountTransferReqDto.setDepositNumber(2222L);
        accountTransferReqDto.setWithdrawPassword(1234L);

        String requestBody = om.writeValueAsString(accountTransferReqDto);
        System.out.println("requestBody = " + requestBody);

        //when
        ResultActions resultActions = mvc.perform(post("/api/s/account/transfer")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("responseBody = " + responseBody);
        //then

    }

}
