package shop.mtcoding.bank.dto.account;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import shop.mtcoding.bank.domain.account.Account;

public class AccountReqDto {

    @Data
    @NoArgsConstructor
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

        public AccountDepositReqDto(Long number, Long amount, String gubun, String tel) {
            this.number = number;
            this.amount = amount;
            this.gubun = gubun;
            this.tel = tel;
        }
    }

    @Data
    public static class AccountSaveRespDto{
        private Long id;
        private Long number;
        private Long balance;

        public AccountSaveRespDto(Account account) {
            id = account.getId();
            number = account.getNumber();
            balance = account.getBalance();
        }
    }
}
