package shop.mtcoding.bank.dto.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;

public class UserReqDto {
    //관련된 DTO들 필요하면 안에 계속 채울 예정
    @Getter
    @Setter
    public static class JoinReqDto{
        //유효성 검사 들어갈 예정
        private String username;
        private String password;
        private String email;
        private String fullname;

        public User toEntity(BCryptPasswordEncoder passwordEncoder){
            return User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .email(email)
                    .fullname(fullname)
                    .role(UserEnum.CUSTOMER)
                    .build();
        }
    }
}
