package shop.mtcoding.bank.temp;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class LongTest {


    @Test
    void LongTest3() throws Exception{
    //given
        Long v1 = 1000L;
        Long v2 = 1000L;


    //when


    //then
        assertThat(v1).isEqualTo(v2);

    }
    
    @Test
    void LongTest2() throws Exception{
    //given
    Long v1 = 128L;
    Long v2 = 128L;

    //Long 타입은 값이 작으면 == 비교가 되나, 값이 커지면 등호 비교가 안된다
    // 2의 8승 256범위 내에서는 (-127 - 127) 등호 비교 되지만
    // 이 범위를 넘어가면 등호 비교 안됨
    
    //when
    if(v1 == v2){
        System.out.println("테스트 : 같습니다 ");
    }
    
    //then
    
    
    }
    

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
