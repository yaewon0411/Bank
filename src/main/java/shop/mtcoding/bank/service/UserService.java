package shop.mtcoding.bank.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.user.UserReqDto;
import shop.mtcoding.bank.dto.user.UserRespDto;
import shop.mtcoding.bank.ex.CustomApiException;

import java.util.Optional;

import static shop.mtcoding.bank.dto.user.UserReqDto.*;
import static shop.mtcoding.bank.dto.user.UserRespDto.*;


@Service
@RequiredArgsConstructor
public class UserService {
    private final Logger log =  LoggerFactory.getLogger(getClass());
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    //충돌 확인용 메서드
    public void getUserInfo(Long userId){
        userRepository.findById(userId)
                .orElseThrow(() -> new CustomApiException("none exists user"));
    }

    //서비스는 DTO를 요청받고, DTO로 응답한다.
    @Transactional //메서드 시작할 때, 시작되고, 종료될 때 함께 종료
    public JoinRespDto join(JoinReqDto joinReqDto){
        //1. 동일 유저네임 존재 검사
        Optional<User> userOP = userRepository.findByUsername(joinReqDto.getUsername());
        if(userOP.isPresent()){
            //유저네임 중복 시
            throw new CustomApiException("동일한 username이 존재합니다");
        }
        //2. 패스워드 인코딩 + 회원가입
        User userPS = userRepository.save(joinReqDto.toEntity(passwordEncoder));

        //3. dto 응답
        return new JoinRespDto(userPS);
    }


}

