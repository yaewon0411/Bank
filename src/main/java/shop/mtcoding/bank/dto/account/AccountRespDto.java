package shop.mtcoding.bank.dto.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.util.CustomDateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AccountRespDto {

    // DTO가 똑같아도 재사용하지 말기 (나중에 만약 출금할 때 먼가 DTO가 조금 달라져야 하면 모든 애들이 한꺼번에 수정되어야 함)
    @Data
    public static class AccountWithdrawRespDto{
        private Long id; //계좌 id
        private Long number; //계좌 번호
        private TransactionDto transaction;
        private Long balance; //잔액

        public AccountWithdrawRespDto(Account account, Transaction transaction) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.transaction = new TransactionDto(transaction);
            this.balance = account.getBalance();
        }

        @Data
        public class TransactionDto{
            private Long id;
            private String gubun;
            private String sender;
            private String receiver;
            private Long amount;
            private String createdAt;

            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.gubun = transaction.getGubun().getValue();
                this.sender = transaction.getSender();
                this.receiver = transaction.getReceiver();
                this.amount = transaction.getAmount();
                this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
            }
        }
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
            private String tel;
            private String createdAt;
            @JsonIgnore
            private Long depositAccountBalance; // 클라이언트에게 전달 x -> 서비스단에서 테스트 용도


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
