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
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
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
import java.util.*;

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

    @Spy //진짜 객체를 InjectMocks에 주입한다. om의 메서드 호출 결과를 Mockito를 사용해 조적하려면 @Spy를 달아야 함. 만약 굳이 Mockito로 호출 결과를 조작(when())할 필요 없으면 @Spy 안달아도 됨
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

    // Account -> balance 변경됐는지
    // Transaction -> balance 잘 기록했는지
    @Test
    public void 계좌입금_test() throws Exception{
        //given
        AccountDepositReqDto accountDepositReqDto = new AccountDepositReqDto(1111L, 100L, "DEPOSIT", "01001234567");

        //stub 1
        User user = newMockUser(1L, "ssar","쌀");//실행됨
        Account account = newMockAccount(1L, 1000L, 1111L, user);//실행됨 - 1000원
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(account)); //실행안됨 -> service 호출 후 실행됨 -> 1100원이 됨


        //stub 2 (서비스 테스트는 스텁이 진행될 때마다 연관된 객체는 새로 만들어서 주입해야 함 -> 타이밍 때문에 꼬인다!!
        Account account2 = newMockAccount(1L, 1000L, 1111L, user);
        Transaction transaction = newMockDepositTransaction(1L, account2); //실행됨 -> 1100원
        when(transactionRepository.save(any())).thenReturn(transaction); //실행안됨

        AccountDepositRespDto accountDepositRespDto = accountService.depositAccount(accountDepositReqDto);
        String responseBody = om.writeValueAsString(accountDepositRespDto);


        //then
        assertThat(accountDepositRespDto.getNumber()).isEqualTo(account.getNumber());
        assertThat(accountDepositRespDto.getTransaction().getDepositAccountBalance()).isEqualTo(account.getBalance());
    }

    @Test
    public void 계좌입금_test2() throws Exception{
        //given
        AccountDepositReqDto accountDepositReqDto = new AccountDepositReqDto(1111L, 100L, "DEPOSIT", "01001234567");

        //stub 1
        User user = newMockUser(1L, "ssar","쌀");//실행됨
        Account account = newMockAccount(1L, 1000L, 1111L, user);//실행됨 - 1000원
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(account)); //실행안됨 -> service 호출 후 실행됨 -> 1100원이 됨

        //stub 2 (서비스 테스트는 스텁이 진행될 때마다 연관된 객체는 새로 만들어서 주입해야 함 -> 타이밍 때문에 꼬인다!!
        User user2 = newMockUser(1L, "ssar","쌀");
        Account account2 = newMockAccount(1L, 1000L, 1111L, user2);
        Transaction transaction = newMockDepositTransaction(1L, account2); //실행됨 -> 1100원
        when(transactionRepository.save(any())).thenReturn(transaction); //실행안됨


        //when
        AccountDepositRespDto accountDepositRespDto = accountService.depositAccount(accountDepositReqDto);

        //then
        String responseBody = om.writeValueAsString(accountDepositRespDto);
        System.out.println("responseBody = " + responseBody);

    }

    // 서비스 테스트를 보여준 건, 기술적이 테크닉을 보여준거뿐!!
    // 진짜 서비스를 테스트 하고 싶으면, 내가 지금 무엇을 여기서 테스트해야 할지 명확히 구분 필요(책임 분리)
    // DTO를 만드는 책임 -> 서비스에 있지만 !! (Controller 테스트 해볼 것이니까 서비스에서 DTO 검증 안할 수 있음!!)
    // DB 관련된 것도 -> 실제로는 서비스 것이 아니니까... 볼필요없어
    // DB 관련된 것을 조회했을 때, 그 값을 통해서 어떤 비즈니스 로직이 흘러가는 것이 있으면, 그리고 그걸 검증하는 게 중요하다면 -> stub으로 검증하면 됨

    // 팀장이 맞춤형 설문지를 만들 때, DB 스텁 만들어서 질문 목록들 다 스텁에 넣고, 검증하고, 넣고 ...-> 진짜 필요한가 생각해야 함

    // 계좌 입금 테스트에서 DB 스텁 2개 만들어서 deposit 검증하고 0 검증하고... -> 진짜 필요한가 생각해야 함
    @Test
    public void 계좌입금_test3() throws Exception{
        //given
        Account account = newMockAccount(1L,1000L,1111L, null);
        Long amount = 0L;

        //when
        if(amount<=0L){
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다");
        }
        account.deposit(100L);

        //then
        assertThat(account.getBalance()).isEqualTo(1100L);
    }

    //계좌 출금_테스트
    @Test
    public void 계좌출금테스트() throws Exception{
        //given
        Long amount = 100L;
        Long password = 1234L;
        Long userId = 1L;

        User ssar = newMockUser(1L, "ssar", "쌀");
        Account ssarAccount = newMockAccount(1L, 1000L, 1000L, ssar);

        //when
        // 0원 체크
        if(amount <=0L){
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다");
        }
        //출금 소유자 확인
        ssarAccount.checkOwner(userId);

        //계좌 비밀번호 확인
        ssarAccount.checkPassword(password);

        //잔액 확인 & 출금
        ssarAccount.withdraw(amount);

        //then
        assertThat(ssarAccount.getBalance()).isEqualTo(900L);
    }

    //계좌 이체_테스트
    @Test
    public void 계좌이체_test() throws  Exception{
        //given
        Long userId = 1L;
        AccountTransferReqDto accountTransferReqDto = new AccountTransferReqDto();
        accountTransferReqDto.setAmount(100L);
        accountTransferReqDto.setGubun("TRANSFER");
        accountTransferReqDto.setWithdrawNumber(1111L);
        accountTransferReqDto.setDepositNumber(2222L);
        accountTransferReqDto.setWithdrawPassword(1234L);

        User withdrawUser = newMockUser(userId, "ssar","쌀");

        Account withdrawAccountPS = newMockAccount(1L, 1000L, 1111L, withdrawUser);
        Account depositAccountPS = newMockAccount(2L, 1000L, 2222L, null);


        //when
        //출금 계좌와 입금 계좌가 동일하면 안됨
        if(accountTransferReqDto.getWithdrawNumber().equals(accountTransferReqDto.getDepositNumber()))
            throw new CustomApiException("출금 계좌와 입금 계좌는 동일할 수 없습니다");

        //0원 체크
        if(accountTransferReqDto.getAmount() <= 0L)
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다");

        //출금 소유자 확인
        withdrawAccountPS.checkOwner(userId);

        //출금 계좌 비번 확인
        withdrawAccountPS.checkPassword(accountTransferReqDto.getWithdrawPassword());

        //이체하기
        withdrawAccountPS.withdraw(accountTransferReqDto.getAmount());
        depositAccountPS.deposit(accountTransferReqDto.getAmount());

        //then
        assertThat(withdrawAccountPS.getBalance()).isEqualTo(900L);
        assertThat(depositAccountPS.getBalance()).isEqualTo(1100L);
    }

    @Test
    public void 계좌이체_test2() throws Exception{
        //when
        AccountTransferReqDto accountTransferReqDto = new AccountTransferReqDto();
        accountTransferReqDto.setAmount(100L);
        accountTransferReqDto.setGubun("TRANSFER");
        accountTransferReqDto.setWithdrawNumber(1111L);
        accountTransferReqDto.setDepositNumber(2222L);
        accountTransferReqDto.setWithdrawPassword(1234L);

        User withdrawUser = newMockUser(1L, "ssar","쌀");
        User depositUser = newMockUser(2L, "cos","코스");

        Account withdrawAccountPS = newMockAccount(1L, 1000L, 1111L, withdrawUser);
        Account depositAccountPS = newMockAccount(2L, 1000L, 2222L, depositUser);

        //출금 계좌와 입금 계좌가 동일하면 안됨
        if(accountTransferReqDto.getWithdrawNumber().equals(accountTransferReqDto.getDepositNumber()))
            throw new CustomApiException("출금 계좌와 입금 계좌는 동일할 수 없습니다");

        //0원 체크
        if(accountTransferReqDto.getAmount() <= 0L)
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다");

        //stub1 - 출금 계좌 확인
        when(accountRepository.findByNumber(withdrawAccountPS.getNumber())).thenReturn(Optional.of(withdrawAccountPS));

        //stub2 - 입금 계좌 확인
        when(accountRepository.findByNumber(depositAccountPS.getNumber())).thenReturn(Optional.of(depositAccountPS));

        //출금 소유자 확인
        withdrawAccountPS.checkOwner(withdrawUser.getId());

        //출금 계좌번호 확인
        withdrawAccountPS.checkPassword(accountTransferReqDto.getWithdrawPassword());

        //이체하기
        withdrawAccountPS.withdraw(accountTransferReqDto.getAmount());
        depositAccountPS.deposit(accountTransferReqDto.getAmount());

        //거래내역 남기기(출금 계좌에서 입금 계좌로)
        Transaction transaction = Transaction.builder()
                .withdrawAccount(withdrawAccountPS)
                .depositAccount(depositAccountPS)
                .withdrawAccountBalance(withdrawAccountPS.getBalance())
                .depositAccountBalance(depositAccountPS.getBalance())
                .amount(accountTransferReqDto.getAmount())
                .gubun(TransactionEnum.TRANSFER)
                .sender(accountTransferReqDto.getWithdrawNumber()+"")
                .receiver(accountTransferReqDto.getDepositNumber()+"")
                .createdAt(LocalDateTime.now())
                .build();

        when(transactionRepository.save(any())).thenReturn(transaction);

        //then
        assertThat(withdrawAccountPS.getBalance()).isEqualTo(900L);
        assertThat(depositAccountPS.getBalance()).isEqualTo(1100L);

        AccountTransferRespDto accountTransferRespDto = accountService.transferAccount(accountTransferReqDto, withdrawUser.getId());
        String responseBody = om.writeValueAsString(accountTransferRespDto);
        System.out.println("responseBody = " + responseBody);

    }

    //계좌 상세보기_테스트
}
