package shop.mtcoding.bank.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.cassandra.DataCassandraTest;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.account.AccountReqDto;
import shop.mtcoding.bank.dto.account.AccountRespDto;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static shop.mtcoding.bank.dto.account.AccountReqDto.*;
import static shop.mtcoding.bank.dto.account.AccountRespDto.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest extends DummyObject {

    @InjectMocks //모든 Mock 개체들이 InjectMocks로 주입
    private AccountService accountService;
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Spy //진짜 객체를 InjectMocks에 주입한다
    private ObjectMapper om;
    @Test
    public void 계좌등록_test() throws Exception{     //stub 3개 필요함 (Repository의 findById랑 findByNumber, save)
        //given
        Long userId = 1L;
        AccountSaveReqDto accountSaveReqDto = new AccountSaveReqDto();
        accountSaveReqDto.setNumber(1111L);
        accountSaveReqDto.setPassword(1234L);

        //stub 1
        User ssar = newMockUser(userId, "ssar","쌀");
        when(userRepository.findById(any())).thenReturn(Optional.of(ssar));

        //stub 2
        when(accountRepository.findByNumber(any())).thenReturn(Optional.empty()); //빈 객체가 나와야 함

        //stub 3
        Account ssarAccount = newMockAccount(1L, 1000L,1111L, ssar);
        when(accountRepository.save(any())).thenReturn(ssarAccount);


        //when
        AccountSaveRespDto accountSaveRespDto = accountService.accountRegister(accountSaveReqDto, userId);
        String responseBody = om.writeValueAsString(accountSaveRespDto);
        //System.out.println("responseBody = " + responseBody);
        //then

        assertThat(accountSaveRespDto.getNumber()).isEqualTo(1111L);

    }
}
