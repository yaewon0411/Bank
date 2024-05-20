package shop.mtcoding.bank.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.transaction.TransactionEnum;
import shop.mtcoding.bank.domain.transaction.TransactionRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.account.AccountReqDto;
import shop.mtcoding.bank.dto.account.AccountReqDto.AccountSaveRespDto;
import shop.mtcoding.bank.dto.account.AccountRespDto;
import shop.mtcoding.bank.ex.CustomApiException;
import shop.mtcoding.bank.util.CustomDateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static shop.mtcoding.bank.dto.account.AccountRespDto.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public AccountSaveRespDto accountRegister(AccountSaveReqDto accountSaveReqDto, Long userId){
        //User가 db에 있는지 검증. 유저 엔티티 가져오기 (PS는 persist의 약자)
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new CustomApiException("유저를 찾을 수 없습니다")
        );
        //해당 계좌가 이미 존재하는지 중복 여부 체크
        Optional<Account> accountOP = accountRepository.findByNumber(accountSaveReqDto.getNumber());
        if(accountOP.isPresent()) {
            throw new CustomApiException("해당 계좌가 이미 존재합니다.");
        }

        //계좌 등록
        Account accountPS = accountRepository.save(accountSaveReqDto.toEntity(userPS));

        //dto롤 응답
        return new AccountSaveRespDto(accountPS);
    }

    public AccountListRespDto getAccountList(Long userId){
        //User가 db에 있는지 검증. 유저 엔티티 가져오기
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new CustomApiException("유저를 찾을 수 없습니다")
        );
        //유저의 모든 계좌 목록
        List<Account> accountListPS = accountRepository.findByUser_id(userId);
        return new AccountListRespDto(accountListPS, userPS);
    }

    @Transactional
    public void deleteAccount(Long number, Long userId){
        // 1. 계좌 확인
        Account accountPS = accountRepository.findByNumber(number).orElseThrow(
                () -> new CustomApiException("계좌를 찾을 수 없습니다")
        );
        //2. 계좌 소유자 확인
        accountPS.checkOwner(userId);

        //3. 계좌 삭제
        accountRepository.deleteById(accountPS.getId());
    }

    //인증이 필요 없음
    @Transactional
    public AccountDepositRespDto depositAccount(AccountDepositReqDto accountDepositReqDto){ //ATM -> 누군가의 계좌
        // 0원 체크
        if(accountDepositReqDto.getAmount()<=0L){
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다");
        }

        // 입금 계좌 확인
        Account depositAccountPS = accountRepository.findByNumber(accountDepositReqDto.getNumber())
                .orElseThrow(
                        () -> new CustomApiException("계좌를 찾을 수 없습니다"));

        // 입금 (해당 계좌의 balance 조정 - update문 - 더티체킹)
        depositAccountPS.deposit(accountDepositReqDto.getAmount());

        // 거래 내역 남기기
        Transaction transaction = Transaction.builder()
                .depositAccount(depositAccountPS)
                .withdrawAccount(null)
                .depositAccountBalance(depositAccountPS.getBalance())
                .withdrawAccount(null)
                .amount(accountDepositReqDto.getAmount())
                .gubun(TransactionEnum.DEPOSIT)
                .sender("ATM")
                .receiver(accountDepositReqDto.getNumber()+"")
                .tel(accountDepositReqDto.getTel())
                .build();

        Transaction transactionPS = transactionRepository.save(transaction);

        return new AccountDepositRespDto(depositAccountPS, transactionPS);
    }

    @Data
    public static class AccountDepositRespDto{
        private Long id; //계좌 id
        private Long number; //계좌 번호
        private TransactionDto transaction;

        public AccountDepositRespDto(Account account, Transaction transaction) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.transaction = new TransactionDto(transaction);
        }

        @Data
        public class TransactionDto{
            private Long id;
            private String gubun;
            private String sender;
            private String receiver;
            private Long amount;
            @JsonIgnore
            private Long depositAccountBalance; // 클라이언트에게 전달 x -> 서비스단에서 테스트 용도
            private String tel;
            private String createdAt;

            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.gubun = transaction.getGubun().getValue();
                this.sender = transaction.getSender();
                this.receiver = transaction.getReceiver();
                this.amount = transaction.getAmount();
                this.depositAccountBalance = transaction.getDepositAccountBalance();
                this.tel = transaction.getTel();
                this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
            }
        }
    }

    @Data
    public static class AccountDepositReqDto{
        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long number;

        @NotNull
        private Long amount; //여기서 0원 유효성 검사해도 됨

        @NotEmpty
        @Pattern(regexp = "^(DEPOSIT)$")
        private String gubun; //DEPOSIT

        @NotEmpty
        @Pattern(regexp = "^[0-9]{11}") //010-0000-0000
        private String tel;
    }





}
