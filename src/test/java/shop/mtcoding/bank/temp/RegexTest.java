package shop.mtcoding.bank.temp;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

//java.util.regex.Pattern 연습
public class RegexTest {

    @Test
    public void 한글만된다_test() throws Exception{
        String value = "한글만썻다";
        boolean result = Pattern.matches("^[가-힣]+$", value);
        System.out.println("result = " + result);

    }
    @Test
    public void 한글은안된다_test() throws Exception{

    }
    @Test
    public void 영어만된다_test() throws Exception{

    }
    @Test
    public void 영어는안된다_test() throws Exception{

    }
    @Test
    public void 영어와숫자만된다_test() throws Exception{

    }
    @Test
    public void 영어만되고_길이는최소2최대4이다_test() throws Exception{

    }

    //username, email, fullname
}
