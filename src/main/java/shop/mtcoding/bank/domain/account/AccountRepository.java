package shop.mtcoding.bank.domain.account;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    //select * from account where number = :number
    //TODO: 리팩토링 해야 함! User도 같이 가져오도록 (계좌 소유자 확인 시에 쿼리가 두 번 나가기 때문에 join fetch)
    Optional<Account> findByNumber(Long number);
}
