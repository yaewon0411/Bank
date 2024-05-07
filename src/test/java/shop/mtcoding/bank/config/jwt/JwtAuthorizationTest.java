package shop.mtcoding.bank.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.sql.results.jdbc.internal.ResultSetAccess;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//가짜 환경에 스프링에 있는 환경들으 스캔해서 올려줘야 함
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc //mock 환경에 mvc가 new돼서 들어감
@ActiveProfiles("test")
public class JwtAuthorizationTest {

    @Autowired
    private MockMvc mvc;
    
    @Test
    public void authorization_success_test() throws Exception{
        //given
        User user = User.builder().id(1L).role(UserEnum.CUSTOMER).build();
        LoginUser loginUser = new LoginUser(user);
        String jwtToken = JwtProcess.create(loginUser);
        System.out.println("jwtToken = " + jwtToken);

        //when
        ResultActions resultActions = mvc.perform(get("/api/s/hello/test")
                .header(JwtVo.HEADER, jwtToken));

        //then
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    public void authorization_fail_test() throws Exception{
        //given

        //when
        ResultActions resultActions = mvc.perform(get("/api/s/hello/test"));

        //then
        resultActions.andExpect(status().isUnauthorized()); //401
    }

    @Test
    public void authorization_admin_test() throws Exception{
        //given
        User user = User.builder().id(1L).role(UserEnum.CUSTOMER).build();
        LoginUser loginUser = new LoginUser(user);
        String jwtToken = JwtProcess.create(loginUser);
        //System.out.println("jwtToken = " + jwtToken);

        //when
        ResultActions resultActions = mvc.perform(get("/api/admin/hello/test")
                .header(JwtVo.HEADER, jwtToken));

        //then
        resultActions.andExpect(status().isForbidden()); //403
    }

    
}
