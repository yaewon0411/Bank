package shop.mtcoding.bank.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.cassandra.DataCassandraTest;
import org.springframework.test.context.ActiveProfiles;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.transaction.TransactionEnum;
import shop.mtcoding.bank.domain.transaction.TransactionRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.account.AccountReqDto;
import shop.mtcoding.bank.dto.account.AccountRespDto;
import shop.mtcoding.bank.dto.account.AccountRespDto.AccountListRespDto.AccountDto;
import shop.mtcoding.bank.ex.CustomApiException;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static shop.mtcoding.bank.dto.account.AccountReqDto.*;
import static shop.mtcoding.bank.dto.account.AccountRespDto.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class AccountServiceTest extends DummyObject {

    @InjectMocks //모든 Mock 개체들이 InjectMocks로 주입
    private AccountService accountService;
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

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
    @Test
    public void 본인계좌목록보기테스트 () throws Exception{
        Long userId = 1L;

        //stub 1 : userRepository에서 userId조회
        User user = newMockUser(userId, "ssar","쌀");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        //stub 2 : 계좌 목록 생성 후 userId로 조회
        Account a1 = newMockAccount(1L, 111L, 1111L, user);
        Account a2 = newMockAccount(2L, 222L, 2222L, user);
        List<Account> accounts = Arrays.asList(a1, a2);

        when(accountRepository.findByUser_id(userId)).thenReturn(accounts);

        AccountListRespDto accountListRespDto = accountService.getAccountList(userId);
        String response = om.writeValueAsString(accountListRespDto);
        System.out.println("response = " + response);


        assertThat(accountListRespDto.getFullname()).isEqualTo(user.getFullname());
        assertThat(accountListRespDto.getAccounts().size()).isEqualTo(2);

    }
    @Test
    public void 계좌삭제테스트() throws Exception{
        //given
        Long number = 1111L;
        Long userId = 2L;

        //stub
        User ssar = newMockUser(1L, "ssar","쌀");
        Account ssarAccount = newMockAccount(1L, 1000L, 1111L, ssar);
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(ssarAccount));

        //when
        assertThrows(CustomApiException.class, () -> accountService.deleteAccount(number, userId));
    }

    @Test
    public void 계좌입금_test() throws Exception{
        //given
        AccountDepositReqDto accountDepositReqDto = new AccountDepositReqDto(1111L, 1000L, "DEPOSIT", "01001234567");
        User user = newMockUser(1L, "ssar","쌀");
        Account account = newMockAccount(1L, 100L, 1111L, user);

        //stub 1
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(account));


        //stub 2
        Transaction transaction = Transaction.builder()
                .depositAccount(account)
                .withdrawAccount(null)
                .depositAccountBalance(account.getBalance()+accountDepositReqDto.getAmount())
                .withdrawAccount(null)
                .amount(accountDepositReqDto.getAmount())
                .gubun(TransactionEnum.DEPOSIT)
                .sender("ATM")
                .createdAt(LocalDateTime.now()) //createdAt에 걸린 이벤트는 실제 db에 insert될 때 작동하는 거라서, mock으로 테스트할 때 직접 지정해줘야함
                .receiver(accountDepositReqDto.getNumber()+"")
                .tel(accountDepositReqDto.getTel())
                .build();

        when(transactionRepository.save(any())).thenReturn(transaction);

        AccountDepositRespDto accountDepositRespDto = accountService.depositAccount(accountDepositReqDto);
        String responseBody = om.writeValueAsString(accountDepositRespDto);


        //then
        assertThat(accountDepositRespDto.getNumber()).isEqualTo(account.getNumber());
        assertThat(accountDepositRespDto.getTransaction().getDepositAccountBalance()).isEqualTo(account.getBalance());
    }
}
