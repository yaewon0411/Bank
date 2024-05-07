package shop.mtcoding.bank.config.jwt;

import net.bytebuddy.build.ToStringPlugin;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;

import static org.junit.jupiter.api.Assertions.*;

public class JwtProcessTest {

    @Test
    public void create_test() throws Exception{
        //given
        User user = User.builder().id(1L).role(UserEnum.CUSTOMER).build();
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
        String jwtToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiLthqDtgbDsnZgg7KCc66qpOmJhbmsiLCJyb2xlIjoiQ1VTVE9NRVIiLCJpZCI6MSwiZXhwIjoxNzE1NTE3ODUyfQ.iTrl4m94SrtoAqWTmXaL3U4BwDxnQgcc63cRIlbCuBmFE75yWlwD1GbCVh3W9j8KImR1Eb-t03dHgJF2Uan2Mw";

        //when
        LoginUser loginUser = JwtProcess.verify(jwtToken);
        System.out.println("loginUser = " + loginUser.getUser().getId());

        //then
        assertEquals(true, loginUser.getUser().getUsername().equals(1));
    }
}
