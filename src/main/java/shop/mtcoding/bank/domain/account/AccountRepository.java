package shop.mtcoding.bank.domain.account;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    //select * from account where number = :number
    //리팩토링 해야 함! User도 같이 가져오도록 (계좌 소유자 확인 시에 쿼리가 두 번 나가기 때문에 join fetch) - account.getUser().getId()로 이미 갖고 있는 외래키 값으로 검증해서 리팩토링 안해도 됨
    //@Query("select a from Account a join fetch a.user u where a.number = :number") //join fetch를 해서 user 객체를 미리 가져오는 쿼리
    Optional<Account> findByNumber(Long number);

    //jpa query method
    //select * from account where user_id = :id
    List<Account> findByUser_id(Long id); //user의 id로 조회
}
