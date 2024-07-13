package shop.mtcoding.bank.config.dummy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.transaction.TransactionEnum;
import shop.mtcoding.bank.domain.transaction.TransactionRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;

import java.time.LocalDateTime;

public class DummyObject {

    protected Transaction newWithdrawTransaction(Account account, AccountRepository accountRepository){
        account.withdraw(100L); //1000원이었다면 900원이 됨
        //Repository Test에서는 더티체킹 됨
        //Controller Test에서는 더티체킹 안됨
        if(accountRepository != null){
            accountRepository.save(account);
        }
        Transaction transaction = Transaction.builder()
                .withdrawAccount(account)
                .depositAccount(null)
                .withdrawAccountBalance(account.getBalance())
                .depositAccountBalance(null)
                .amount(100L)
                .gubun(TransactionEnum.WITHDRAW)
                .sender(account.getNumber()+"")
                .receiver("ATM")
                .build();
        return transaction;
    }
    protected Transaction newTransferTransaction(Account withdrawAccount, Account depositAccount, AccountRepository accountRepository){
        withdrawAccount.withdraw(100L);
        depositAccount.deposit(100L);

        //더티체킹 안되기 때문에
        if(accountRepository != null){
            accountRepository.save(withdrawAccount);
            accountRepository.save(depositAccount);
        }
        Transaction transaction = Transaction.builder()
                .withdrawAccount(withdrawAccount)
                .depositAccount(depositAccount)
                .withdrawAccountBalance(withdrawAccount.getBalance())
                .depositAccountBalance(depositAccount.getBalance())
                .amount(100L)
                .gubun(TransactionEnum.TRANSFER)
                .sender(withdrawAccount.getNumber()+"")
                .receiver(depositAccount.getNumber()+"")
                .tel("01011112222")
                .build();
        return transaction;
    }

    protected Transaction newDepositTransaction(Account account, AccountRepository accountRepository){
        account.deposit(100L); //1000원이었다면 900원이 됨
        //더티체킹 안되기 때문에
        if(accountRepository != null){
            accountRepository.save(account);
        }
        Transaction transaction = Transaction.builder()
                .withdrawAccount(null)
                .depositAccount(account)
                .withdrawAccountBalance(null)
                .depositAccountBalance(account.getBalance())
                .amount(100L)
                .gubun(TransactionEnum.DEPOSIT)
                .sender("ATM")
                .receiver(account.getNumber()+"")
                .tel("01011112222")
                .build();
        return transaction;
    }

    //계좌 1111L 1-> 1000원
    //입금 트랜잭션 - 계좌 1100원 변경 -> 입금 트랜잭션 히스토리가 생성되어야 함.
    protected static Transaction newMockDepositTransaction(Long id, Account account){
        account.deposit(100L);
        return Transaction.builder()
                .id(id)
                .depositAccount(account)
                .withdrawAccount(null)
                .withdrawAccountBalance(null)
                .depositAccountBalance(account.getBalance())
                .withdrawAccount(null)
                .amount(100L)
                .gubun(TransactionEnum.DEPOSIT)
                .sender("ATM")
                .createdAt(LocalDateTime.now()) //createdAt에 걸린 이벤트는 실제 db에 insert될 때 작동하는 거라서, mock으로 테스트할 때 직접 지정해줘야함
                .receiver(account.getNumber()+"")
                .tel("01012345678")
                .build();
    }

    protected User newUser(String username, String fullname){ //엔티티 save할 때 쓰일 것
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encPassword = passwordEncoder.encode("1234");
        return  User.builder()
                .username(username)
                .password(encPassword)
                .role(UserEnum.CUSTOMER)
                .email(username+"@naver.com")
                .fullname(fullname)
                .build();
    }
    protected User newMockUser(Long id, String username, String fullname){ //stub으로 사용할 것
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encPassword = passwordEncoder.encode("1234");
        return  User.builder()
                .id(id)
                .username(username)
                .password(encPassword)
                .role(UserEnum.CUSTOMER)
                .email(username+"@naver.com")
                .fullname(fullname)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    protected Account newAccount(Long number, User user){
        return Account.builder()
                .number(number)
                .password(1234L)
                .balance(1000L)
                .user(user)
                .build();
    }

    protected Account newMockAccount(Long id, Long balance, Long number, User user){
        return Account.builder()
                .number(number)
                .id(id)
                .password(1234L)
                .balance(balance)
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

}
