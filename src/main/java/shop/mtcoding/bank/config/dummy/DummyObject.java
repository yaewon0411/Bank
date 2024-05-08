package shop.mtcoding.bank.config.dummy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;

import java.time.LocalDateTime;

public class DummyObject {

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
