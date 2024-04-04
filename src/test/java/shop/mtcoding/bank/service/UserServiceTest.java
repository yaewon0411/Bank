package shop.mtcoding.bank.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.user.UserReqDto;
import shop.mtcoding.bank.dto.user.UserRespDto;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static shop.mtcoding.bank.dto.user.UserReqDto.*;
import static shop.mtcoding.bank.dto.user.UserRespDto.*;
import static shop.mtcoding.bank.service.UserService.*;

//가짜 환경이기 때문에 Spring 관련 Bean들이 하나도 없는 환경!!
@ExtendWith(MockitoExtension.class) //Mokito 환경에서 서비스 테스트
public class UserServiceTest extends DummyObject {

    @Mock //가짜로 띄워서 가져옴. 그리고 InjectMocks가 걸린 곳에 해당 Mock을 주입
    private UserRepository userRepository;

    @Spy //진짜 스프링 IoC에 있는 걸 꺼내옴. 그리고 InjectMocks가 걸린 곳에 주입
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks //가짜 환경에서 주입
    private UserService userService;
    @Test
    public void 회원가입_test() throws Exception{
        //given
        JoinReqDto joinReqDto = new JoinReqDto();
        joinReqDto.setUsername("ssar");
        joinReqDto.setPassword("1234");
        joinReqDto.setEmail("ssar@naver.com");
        joinReqDto.setFullname("쌀");

        //stub 1
        //findByUsername(파라미터에 아무거나 가능)이 실행되면 빈 객체를 반환할 것이라고 가정
        //따라서 findByUsername의 if(userOp.isPresent())에 걸리지 않을 것!!
        when(userRepository.
                findByUsername(any())).thenReturn(Optional.empty());

//        when(userRepository.
//                findByUsername(any())).thenReturn(Optional.of(new User())); //username 중복 조건에 걸리므로 CustomApiException 터짐!!

        //stub 2
        User ssar = newMockUser(1L, "ssar","쌀");

        when(userRepository.save(any())).thenReturn(ssar);

        //when
        JoinRespDto joinRespDto = userService.join(joinReqDto);
        System.out.println("joinRespDto = " + joinRespDto);

        //then
        assertThat(joinRespDto.getId()).isEqualTo(1L);
        assertThat(joinRespDto.getUsername()).isEqualTo("ssar");
    }
}
