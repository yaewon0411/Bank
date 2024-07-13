package shop.mtcoding.bank.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.dto.ResponseDto;
import shop.mtcoding.bank.dto.account.AccountRespDto;
import shop.mtcoding.bank.dto.transaction.TransactionRespDto;
import shop.mtcoding.bank.service.AccountService;
import shop.mtcoding.bank.service.TransactionService;
import shop.mtcoding.bank.util.CustomResponseUtil;

import static shop.mtcoding.bank.dto.account.AccountRespDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TransactionController {
    private final TransactionService transactionService;
    private final AccountService accountService;

    @GetMapping("/s/account/{number}/transaction")
    public ResponseEntity<?> 입출금목록보기(@PathVariable("number")Long number,
                                     @RequestParam(value="gubun", defaultValue = "ALL") String gubun,
                                     @RequestParam(value="page", defaultValue = "0") Integer page,
                                     @AuthenticationPrincipal LoginUser loginUser){

        TransactionRespDto.TransactionListRespDto transactionListRespDto = transactionService.입출금목록보기(loginUser.getUser().getId(), number, gubun, page);


        //return new ResponseEntity<>(new ResponseDto<>(1, "입출금 목록 보기 성공", transactionListRespDto), HttpStatus.OK); //일반적으로 사용한 리턴 구문

        return ResponseEntity.ok().body(new ResponseDto<>(1, "입출금 목록 보기 성공", transactionListRespDto)); //이렇게도 리턴 가능한데 위 표현식이 좀 더 가독성이 좋은듯
    }


}
