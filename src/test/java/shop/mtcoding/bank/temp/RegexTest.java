package shop.mtcoding.bank.temp;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.assertj.core.api.FactoryBasedNavigableListAssert.assertThat;

//java.util.regex.Pattern 연습
public class RegexTest {

    @Test
    public void 한글만된다_test() throws Exception{
        String value = "한글만된다";
        boolean result = Pattern.matches("^[가-힣]+$", value);
        System.out.println("result = " + result);

    }
    @Test
    public void 한글은안된다_test() throws Exception{
        String value = "only eng";
        boolean result = Pattern.matches("^[^ㄱ-ㅎ^가-힣]*$", value); //공백 허용
        System.out.println("result = " + result);
    }
    @Test
    public void 영어만된다_test() throws Exception{
        String value = "ssar";
        boolean result = Pattern.matches("^[a-zA-Z]+$", value);
        System.out.println("result = " + result);
    }
    @Test
    public void 영어는안된다_test() throws Exception{
        String value = "한글이랑 숫자만 됨333";
        boolean result = Pattern.matches("^[^a-z^A-Z]*$", value); //공백 허용
        System.out.println("result = " + result);
    }
    @Test
    public void 영어와숫자만된다_test() throws Exception{
        String value = "ssar3d3";
        boolean result = Pattern.matches("^[a-zA-Z0-9]+$", value);
        System.out.println("result = " + result);
    }
    @Test
    public void 영어만되고_길이는최소2최대4이다_test() throws Exception{
        String value = "jj";
        boolean result = Pattern.matches("^[a-zA-Z]{2,4}$", value);
        System.out.println("result = " + result);
    }

    @Test
    //username : 길이 2-20자 제한, 영문, 숫자
    public void user_username테스트() throws Exception{
        String username = "s0980";
        boolean result = Pattern.matches("^[a-zA-Z0-9]{2,20}$", username);
        System.out.println("result = " + result);
    }
    @Test
    //fullname : 영어, 한글, 1-20
    public void user_fullname테스트() throws Exception{
        String fullname = "일부터이십까지sdgsdfasdfsdfsadgs";
        boolean result = Pattern.matches("^[a-zA-Z가-힣]{1,20}$", fullname);
        System.out.println("result = " + result);
    }

    @Test
    //fullname : 영어, 한글, 1-20
    public void user_email테스트() throws Exception{
        String email = "ssar@nate.com"; //ac.kr co.kr or.kr 은 못잡음 지금
        boolean result = Pattern.matches("^[a-zA-Z0-9]{2,10}@[a-zA-Z0-9]{2,6}\\.[a-zA-Z]{2,3}$", email);
        System.out.println("result = " + result);
    }

    @Test
    //fullname : 영어, 한글, 1-20
    public void account_gubun_테스트1() throws Exception{
        String gubun = "DEPOSIT";
        boolean result = Pattern.matches("^(DEPOSIT)$", gubun);
        System.out.println("result = " + result);
    }

    @Test
    //fullname : 영어, 한글, 1-20
    public void account_gubun_테스트2() throws Exception{
        String gubun = "TRANSFER";
        boolean result = Pattern.matches("^(DEPOSIT|TRANSFER)$", gubun);
        System.out.println("result = " + result);
    }

    @Test
    //fullname : 영어, 한글, 1-20
    public void account_tel_테스트1() throws Exception{
        String tel = "010-1234-5678";
        boolean result = Pattern.matches("^[0-9]{3}-[0-9]{4}-[0-9]{4}", tel);
        System.out.println("result = " + result);
    }

    @Test
    //fullname : 영어, 한글, 1-20
    public void account_tel_테스트2() throws Exception{
        String tel = "01012345678";
        boolean result = Pattern.matches("^[0-9]{11}", tel);
        System.out.println("result = " + result);
    }
}
