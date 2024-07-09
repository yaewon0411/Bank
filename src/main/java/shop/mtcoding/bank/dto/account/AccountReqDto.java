package shop.mtcoding.bank.dto.account;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.user.User;

public class AccountReqDto {

    @Data
    public static class AccountTransferReqDto{
        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long withdrawNumber;

        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long depositNumber;

        @NotNull
        @Digits(integer=4, fraction=4)
        private Long withdrawPassword;

        @NotNull
        private Long amount;

        @NotEmpty
        @Pattern(regexp = "^(TRANSFER)$")
        private String gubun;
    }

    @Data
    public static class AccountWithdrawReqDto{
        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long number;

        @NotNull
        @Digits(integer=4, fraction=4)
        private Long password;

        @NotNull
        private Long amount;

        @NotEmpty
        @Pattern(regexp = "^(WITHDRAW)$")
        private String gubun;
    }

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
    public static class AccountSaveReqDto{
        @NotNull
        @Digits(integer = 4, fraction = 4) //최대 4자
        private Long number;

        @NotNull
        @Digits(integer = 4, fraction = 4) //최대 4자
        private Long password;

        public Account toEntity(User user){
            return Account.builder()
                    .number(number)
                    .password(password)
                    .balance(1000L)
                    .user(user)
                    .build();
        }
    }


}
