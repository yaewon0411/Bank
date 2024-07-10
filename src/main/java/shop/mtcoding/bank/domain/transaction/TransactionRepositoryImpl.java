package shop.mtcoding.bank.domain.transaction;


import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

interface Dao{
    List<Transaction> findTransactionList(@Param("accountId")Long accountId, @Param("gubun") String gubun, @Param("page")Integer page);
}


//Impl은 꼭 레포지토리 클래스 명 뒤에 붙어야 한다
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements Dao {

    private final EntityManager em;
    @Override
    public List<Transaction> findTransactionList(Long accountId, String gubun, Integer page) {
        //동적 쿼리 (gubun 값을 가지고 동적 쿼리 생성할 것 = DEPOSIT, WITHDRAW, ALL)
        String sql = "";
        sql += "select t from Transaction t ";

        if(gubun.equals("WITHDRAW")){
            sql += "join fetch t.withdrawAccount wa ";
            sql += "where t.withdrawAccount.id = :withdrawAccountId";
        }
        else if(gubun.equals("DEPOSIT")){
            sql += "join fetch t.depositAccount da ";
            sql += "where t.depositAccount.id = :depositAccountId ";
        }
        else{ //gubun == ALL
            sql += "left join fetch t.withdrawAccount wa ";
            sql += "left join fetch t.depositAccount da ";
            sql += "where t.withdrawAccount.id = :withdrawAccountId ";
            sql +="or ";
            sql +="t.depositAccount.id = :depositAccountId";
        }

        TypedQuery<Transaction> query = em.createQuery(sql, Transaction.class);

        if(gubun.equals("WITHDRAW")){
            query = query.setParameter("withdrawAccountId",accountId);
        }else if(gubun.equals("DEPOSIT")){
            query = query.setParameter("depositAccountId",accountId);
        }else{
            query = query.setParameter("withdrawAccountId",accountId);
            query = query.setParameter("depositAccountId",accountId);
        }

        query.setFirstResult(page*5); //page = 0 -> 0, 1 -> 5, 2 -> 10
        query.setMaxResults(5);

        return query.getResultList();
    }
}
