package shop.mtcoding.bank.config.jwt;

import net.bytebuddy.build.ToStringPlugin;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AssertionsKt;
import org.junit.jupiter.api.Test;
import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class JwtProcessTest {

    @Test
    public void create_test() throws Exception{
        //given
        User user = User.builder().id(1L).role(UserEnum.ADMIN).build();
        LoginUser loginUser = new LoginUser(user); //id와 role만

        //when
        String jwtToken = JwtProcess.create(loginUser);
        System.out.println("jwtToken = " + jwtToken);

        //then
        assertTrue(jwtToken.startsWith(JwtVo.TOKEN_PREFIX));
    }

    @Test
    public void verify_test() throws Exception{
        //given
        String jwtToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiLthqDtgbDsnZgg7KCc66qpOmJhbmsiLCJyb2xlIjoiQURNSU4iLCJpZCI6MSwiZXhwIjoxNzE1Njc1Mjc0fQ.fQnCoiyMeh2XtKn7r6SDNbFdFx_0X9BYyKCS2I895yl-3g6BmjHh1pgdICdzJSy2DQw-gw6ZHR2xl1ZzjTBkCw";

        //when
        LoginUser loginUser = JwtProcess.verify(jwtToken);
        System.out.println("loginUser = " + loginUser.getUser().getId());
        System.out.println("loginUser.getUser().getRole().name() = " + loginUser.getUser().getRole().name());
        
        //then
        assertThat(loginUser.getUser().getId()).isEqualTo(1L);
        assertThat(loginUser.getUser().getRole()).isEqualTo(UserEnum.ADMIN);
    }
}
