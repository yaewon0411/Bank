package shop.mtcoding.bank.domain.transaction;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@ActiveProfiles("test")
@DataJpaTest //DB 관련된 Bean이 다 올라온다
public class TransactionRepositoryImplTest extends DummyObject {


    @Autowired AccountRepository accountRepository;
    @Autowired TransactionRepository transactionRepository;
    @Autowired UserRepository userRepository;
    @Autowired EntityManager em;

    @BeforeEach
    public void setUp(){
        autoincrementReset();
        dataSetting();
        em.clear(); //레퍼지토리 테스트에서 필요!!
    }

    private void autoincrementReset() {
        em.createNativeQuery("alter table user_tb alter column id restart with 1").executeUpdate();
        em.createNativeQuery("alter table account_tb alter column id restart with 1").executeUpdate();
        em.createNativeQuery("alter table transaction_tb alter column id restart with 1").executeUpdate();
    }

    @Test
    void findTransactionList_all_test() throws Exception{
      //given
        Long accountId = 1L;

      //when
        List<Transaction> transactionListPS = transactionRepository.findTransactionList(accountId, "ALL",0);
        transactionListPS.forEach((t) ->{
            System.out.println("t.getId = " + t.getId());
            System.out.println("t.getAmount() = " + t.getAmount());
            System.out.println("t.getSender() = " + t.getSender());
            System.out.println("t.getReceiver() = " + t.getReceiver());
            System.out.println("t.getDepositAccountBalance() = " + t.getDepositAccountBalance());
            System.out.println("t.getWithdrawAccountBalance() = " + t.getWithdrawAccountBalance());
            System.out.println("t.출금계좌의 잔액 = " + t.getWithdrawAccount().getBalance());
            //System.out.println("t.유저풀네임 = " + t.getWithdrawAccount().getUser().getFullname()); //유저는 조인 대상이 아니었기 때문에 이를 위한 조회 쿼리 하나 날라감
            System.out.println("=================================");
        });

      //then
        assertThat(transactionListPS.get(3).getDepositAccountBalance()).isEqualTo(800L);
    }

    @Test
    public void dataJpa_test1(){
        List<Transaction> transactionList = transactionRepository.findAll();
        transactionList.forEach((transaction)->{
            System.out.println("transaction = " + transaction.getId());
            System.out.println("transaction.getSender() = " + transaction.getSender());
            System.out.println("transaction.getReceiver() = " + transaction.getReceiver());
            System.out.println("transaction.getGubun() = " + transaction.getGubun());
            System.out.println(" ==============================");
        });
    }

    @Test
    public void dataJpa_test2(){
        List<Transaction> transactionList = transactionRepository.findAll();
        transactionList.forEach((transaction)->{
            System.out.println("transaction = " + transaction.getId());
            System.out.println("transaction.getSender() = " + transaction.getSender());
            System.out.println("transaction.getReceiver() = " + transaction.getReceiver());
            System.out.println("transaction.getGubun() = " + transaction.getGubun());
            System.out.println(" ==============================");
        });
    }

    private void dataSetting() {
        User ssar = userRepository.save(newUser("ssar", "쌀"));
        User cos = userRepository.save(newUser("cos", "코스,"));
        User love = userRepository.save(newUser("love", "러브"));
        User admin = userRepository.save(newUser("admin", "관리자"));

        Account ssarAccount1 = accountRepository.save(newAccount(1111L, ssar));
        Account cosAccount = accountRepository.save(newAccount(2222L, cos));
        Account loveAccount = accountRepository.save(newAccount(3333L, love));
        Account ssarAccount2 = accountRepository.save(newAccount(4444L, ssar));

        Transaction withdrawTransaction1 = transactionRepository
                .save(newWithdrawTransaction(ssarAccount1, accountRepository));
        Transaction depositTransaction1 = transactionRepository
                .save(newDepositTransaction(cosAccount, accountRepository));
        Transaction transferTransaction1 = transactionRepository
                .save(newTransferTransaction(ssarAccount1, cosAccount, accountRepository));
        Transaction transferTransaction2 = transactionRepository
                .save(newTransferTransaction(ssarAccount1, loveAccount, accountRepository));
        Transaction transferTransaction3 = transactionRepository
                .save(newTransferTransaction(cosAccount, ssarAccount1, accountRepository));
    }

}
