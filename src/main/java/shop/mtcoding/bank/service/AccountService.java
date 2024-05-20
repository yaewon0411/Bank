package shop.mtcoding.bank.service;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.account.AccountReqDto;
import shop.mtcoding.bank.dto.account.AccountReqDto.AccountSaveRespDto;
import shop.mtcoding.bank.dto.account.AccountRespDto;
import shop.mtcoding.bank.ex.CustomApiException;

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





}
