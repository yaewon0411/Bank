package shop.mtcoding.bank.dto.account;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AccountRespDto {

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
    @Data
    public static class AccountListRespDto{
        private List<AccountDto> accounts = new ArrayList<>();
        private String fullname;

        public AccountListRespDto(List<Account> accounts, User user) {
            this.accounts = accounts.stream().map(AccountDto::new).collect(Collectors.toList());
            this.fullname = user.getFullname();
        }
        @Data
        public class AccountDto{
            private Long id;
            private Long number;
            private Long balance;

            public AccountDto(Account account) {
                this.id = account.getId();
                this.number = account.getNumber();
                this.balance = account.getBalance();
            }
        }
    }
}
