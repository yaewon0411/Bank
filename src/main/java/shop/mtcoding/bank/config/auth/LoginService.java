package shop.mtcoding.bank.config.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;

@RequiredArgsConstructor
@Service
public class LoginService implements UserDetailsService {

    private final UserRepository userRepository;

    // 시큐리티로 로그인이 될 때, 시큐리티가 loadUserByUsername()을 실행한다
    // 여기서 username이 DB에 있는지 체크하는 건 내가 해야 함
    // DB에 없으면 오류,
    // 있으면 정상적으로 시큐리티 컨텍스트 내부 세션에 로그인 된 세션이 만들어진다.

    // 시큐리티를 타고 있을 때는 개발자에게 제어권이 없다.
    // 따라서 만약 DB에 username이 없을 때 오류를 터뜨려서 개발자가 알 수 있게 해야함!!
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userPs = userRepository.findByUsername(username).orElseThrow(
                () -> new InternalAuthenticationServiceException("인증 실패")
        );
        return new LoginUser(userPs); //이 객체가 세션을 만든다
    }
}
