package shop.mtcoding.bank.dto.account;

import lombok.Data;
import shop.mtcoding.bank.domain.account.Account;

public class AccountReqDto {

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
