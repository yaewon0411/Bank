package shop.mtcoding.bank.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.dto.ResponseDto;
import shop.mtcoding.bank.service.AccountService;
import static shop.mtcoding.bank.dto.account.AccountReqDto.*;
import static shop.mtcoding.bank.dto.account.AccountRespDto.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/s/account")
    public ResponseEntity<?> saveAccount(@RequestBody @Valid AccountSaveReqDto accountSaveReqDto, BindingResult bindingResult,
                                         @AuthenticationPrincipal LoginUser loginUser) {
        AccountSaveRespDto accountSaveRespDto = accountService.accountRegister(accountSaveReqDto, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 등록 성공", accountSaveRespDto), HttpStatus.CREATED);
    }

    // 인증이 필요하고 account 테이블에 1번 row를 주세요!!
    // cos로 로그인을 헀는데, cos의 id가 2번. 이 상태로 1번 row달라고 요청하면 안되니까 한번 검증하는 게 필요!! -> 권한 처리 하는게 귀찮
    // 인증이 필요하고, account 테이블에 login한 유저의 계좌만 주세요
    @GetMapping("/s/account/login-user")
    public ResponseEntity<?> findUserAccount(@AuthenticationPrincipal LoginUser loginUser){

        AccountListRespDto accountListRespDto = accountService.getAccountList(loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "유저별 계좌 목록 보기 성공", accountListRespDto),HttpStatus.OK);
    }

    @DeleteMapping("/s/account/{number}")
    public ResponseEntity<?> deleteAccount(@PathVariable(name = "number") Long number, @AuthenticationPrincipal LoginUser loginUser){
        accountService.deleteAccount(number, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 삭제 완료", null),HttpStatus.OK);
    }

    @PostMapping("/account/deposit")
    public ResponseEntity<?> depositAccount(@RequestBody @Valid AccountDepositReqDto accountDepositReqDto, BindingResult bindingResult){
        AccountDepositRespDto accountDepositRespDto = accountService.depositAccount(accountDepositReqDto);
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 입금 완료",accountDepositRespDto),HttpStatus.CREATED);
    }

    @PostMapping("/s/account/withdraw")
    public ResponseEntity<?> withdrawAccount(@RequestBody @Valid AccountWithdrawReqDto accountWithdrawReqDto,
                                             BindingResult bindingResult,
                                             @AuthenticationPrincipal LoginUser loginUser){
        AccountWithdrawRespDto accountWithdrawRespDto = accountService.withdrawAccount(loginUser.getUser().getId(), accountWithdrawReqDto);
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 출금 완료",accountWithdrawRespDto),HttpStatus.CREATED);
    }

    @PostMapping("/s/account/transfer")
    public ResponseEntity<?> transferAccount(@RequestBody @Valid AccountTransferReqDto accountTransferReqDto,
                                             BindingResult bindingResult,
                                             @AuthenticationPrincipal LoginUser loginUser){
        AccountTransferRespDto accountTransferRespDto = accountService.transferAccount(accountTransferReqDto, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 이체 완료",accountTransferRespDto),HttpStatus.CREATED);
    }
    @GetMapping("/s/account/{number}")
    public ResponseEntity<?> 계좌상세보기(@PathVariable("number") Long number,
                                    @RequestParam(value = "page", defaultValue = "0")Integer page,
                                    @AuthenticationPrincipal LoginUser loginUser){
        AccountDetailRespDto accountDetailRespDto = accountService.계좌상세보기(number, loginUser.getUser().getId(), page);
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 상세 보기 성공", accountDetailRespDto), HttpStatus.OK);
    }
}
