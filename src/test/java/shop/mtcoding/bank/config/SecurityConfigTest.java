package shop.mtcoding.bank.config;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@AutoConfigureMockMvc //Mock(가짜) 환경에 MockMv가 등록됨
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class SecurityConfigTest {

    //가짜 환경에 등록된 MockMvc를 DI
    @Autowired
    private MockMvc mvc;

    @Test
    public void authentication_test() throws Exception{
        //given
        System.out.println("테스트 실행");
        //when
        ResultActions resultActions = mvc.perform(get("/api/s/hello"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();

        int statusCode = resultActions.andReturn().getResponse().getStatus();

        //then
        assertThat(statusCode).isEqualTo(401);

    }

    @Test
    public void authorization_test() throws Exception{
        //given

        //when
        ResultActions resultActions = mvc.perform(get("/api/admin/hello"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();

        int statusCode = resultActions.andReturn().getResponse().getStatus();

        //then
        assertThat(statusCode).isEqualTo(401);
    }

}
