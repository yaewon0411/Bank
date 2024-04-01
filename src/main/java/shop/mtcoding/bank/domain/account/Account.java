package shop.mtcoding.bank.domain.account;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import shop.mtcoding.bank.domain.user.User;

import java.time.LocalDateTime;

@Entity
@Getter @Table(name = "account_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Account {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private Long number; //계좌번호

    @Column(nullable = false, length = 4)
    private Long password; //계좌비번

    @Column(nullable = false)
    private Long balance; //잔액 (계좌 생성 시 기본값 100원)

    //항상 ORM에서 FK의 주인은 Many 쪽
    @ManyToOne(fetch = FetchType.LAZY)
    private User user; //JoinColumn 안쓰면 기본 전략으로 '테이블명_기본키필드명' 이렇게 fk 필드 생성


    @CreatedDate // insert 할 때
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate // insert, update 할 때
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Account(Long id, Long number, Long password, Long balance, User user, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.number = number;
        this.password = password;
        this.balance = balance;
        this.user = user;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
