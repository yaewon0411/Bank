package shop.mtcoding.bank.domain.user;

import java.time.LocalDateTime;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_tb")
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor //스프링이 User 객체 생성할 때 빈 생성자로 new를 하기 때문!!
@ToString
public class User { // extends 시간 설정(ex. BaseEntity) 상속해도 됨
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String username;

    @Column(nullable = false, length = 60) // 패스워드 인코딩하면 길이가 늘어남
    private String password;

    @Column(nullable = false, length = 20)
    private String email;

    @Column(nullable = false, length = 20)
    private String fullname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserEnum role;

    @CreatedDate // insert 할 때
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate // insert, update 할 때
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public User(Long id, String username, String password, String email, String fullname, UserEnum role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullname = fullname;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
