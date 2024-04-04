package shop.mtcoding.bank.config.dummy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
}
