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
import shop.mtcoding.bank.dto.account.AccountReqDto.AccountDepositReqDto;
import shop.mtcoding.bank.dto.account.AccountRespDto;
import shop.mtcoding.bank.ex.CustomApiException;
import shop.mtcoding.bank.util.CustomDateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static shop.mtcoding.bank.dto.account.AccountReqDto.*;
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

    @Transactional
    public AccountWithdrawRespDto withdrawAccount(Long userId, AccountWithdrawReqDto accountWithdrawReqDto){
        // 0원 체크
        if(accountWithdrawReqDto.getAmount()<=0L){
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다");
        }

        //출금 계좌 확인
        Account withdrawAccountPS = accountRepository.findByNumber(accountWithdrawReqDto.getNumber())
                .orElseThrow(
                        () -> new CustomApiException("계좌를 찾을 수 없습니다"));

        //출금 소유자 확인 (로그인한 사람과 동일한지)
        withdrawAccountPS.checkOwner(userId);

        //출금 계좌 비밀번호 확인
        withdrawAccountPS.checkPassword(accountWithdrawReqDto.getPassword());

        //출금 계좌 잔액 확인 & 출금하기
        withdrawAccountPS.withdraw(accountWithdrawReqDto.getAmount());

        //거래내역 남기기 (내 계좌에서 ATM으로 출금)
        Transaction transaction = Transaction.builder()
                .withdrawAccount(withdrawAccountPS)
                .depositAccount(null)
                .withdrawAccountBalance(withdrawAccountPS.getBalance())
                .depositAccountBalance(null)
                .amount(accountWithdrawReqDto.getAmount())
                .gubun(TransactionEnum.WITHDRAW)
                .sender(accountWithdrawReqDto.getNumber()+"")
                .receiver("ATM")
                .build();

        Transaction transactionPS = transactionRepository.save(transaction);

        //DTO 응답
        return new AccountWithdrawRespDto(withdrawAccountPS, transactionPS);
    }

    //계좌 이체
    public AccountTransferRespDto transferAccount(AccountTransferReqDto accountTransferReqDto, Long userId){

        //출금 계좌와 입금 계좌가 동일하면 안됨
        if(accountTransferReqDto.getWithdrawNumber().equals(accountTransferReqDto.getDepositNumber()))
            throw new CustomApiException("출금 계좌와 입금 계좌는 동일할 수 없습니다");

        //0원 체크
        if(accountTransferReqDto.getAmount() <= 0L)
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다");


        //출금 계좌 확인
        Account withdrawAccountPS = accountRepository.findByNumber(accountTransferReqDto.getWithdrawNumber())
                .orElseThrow(
                        () -> new CustomApiException("출금 계좌를 찾을 수 없습니다")
                );

        //입금 계좌 확인
        Account depositAccountPS = accountRepository.findByNumber(accountTransferReqDto.getDepositNumber())
                .orElseThrow(
                        () -> new CustomApiException("입금 계좌를 찾을 수 없습니다")
                );

        //출금 소유자 확인
        withdrawAccountPS.checkOwner(userId);

        //출금 계좌 비번 확인
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
                .build();

        Transaction transactionPS
                = transactionRepository.save(transaction);


        return new AccountTransferRespDto(withdrawAccountPS, transactionPS);
    }

    public AccountDetailRespDto 계좌상세보기(Long number, Long userId, Integer page){
        //1. 구분값 고정
        String gubun = "ALL";

        //계좌 찾기
        Account accountPS = accountRepository.findByNumber(number).orElseThrow(
                () -> new CustomApiException("해당 계좌를 찾을 수 없습니다")
        );
        //계좌 소유자 체크
        accountPS.checkOwner(userId);

        //입출금 내역 조회
        List<Transaction> transactionListPS = transactionRepository.findTransactionList(accountPS.getId(), gubun, page);

        return new AccountDetailRespDto(accountPS, transactionListPS);
    }







}
