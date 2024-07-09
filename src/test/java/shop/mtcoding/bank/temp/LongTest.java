package shop.mtcoding.bank.temp;

import org.junit.jupiter.api.Test;

public class LongTest {

    @Test
    public void long_Test() throws  Exception{
        //given
        Long number1 = 1111L;
        Long number2 = 1111L;

        //when
        if(number2 == number1) System.out.println("테스트 : 동일합니다");
        else System.out.println("테스트 : 동일하지 않습니다");

        Long amount1 = 100L;
        Long amount2 = 1000L;

        //크기 비교는 longValue() 안 거쳐도 확인 가능
        if(amount1 < amount2) System.out.println("테스트 : amount1이 작습니다");
        else System.out.println("테스트: amount1이 큽니다");


        //then
        if(number2.longValue() == number1.longValue()) System.out.println("longValue 써야 동일합니다");
        if(number2.equals(number1)) System.out.println("또는 equals를 써야 동일합니다");
    }
}
