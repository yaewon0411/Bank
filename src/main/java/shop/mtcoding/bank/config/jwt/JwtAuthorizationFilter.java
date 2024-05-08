package shop.mtcoding.bank.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import shop.mtcoding.bank.config.auth.LoginUser;

import java.io.IOException;

/**
 * 모든 주소에서 동작함 (토큰 검증)
 */
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final static Logger log = LoggerFactory.getLogger(JwtAuthorizationFilter.class);
    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    //JWT 토큰 헤더를 추가하지 않아도 해당 필터는 통과할 수 있지만, 결국 시큐리티단에서 세션 값 검증에 실패함
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if(isHeaderVerify(request, response)){
            //토큰이 존재함
            log.debug("디버그 : 토큰이 존재함");
            String token = request.getHeader(JwtVo.HEADER).replace(JwtVo.TOKEN_PREFIX, ""); //Bearer 제거

            //토큰 검증
            LoginUser loginUser = JwtProcess.verify(token);
            log.debug("디버그 : 토큰 검증 완료됨");

            //임시 세션(UserDetails 타입 or username). 넘기는 유저 role이 중요
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities()); //id와 role만 존재함
            log.debug("디버그 : 임시 세션이 생성됨");

            //강제 로그인
            SecurityContextHolder.getContext().setAuthentication(authentication);


        }
        //다시 체인 타기
        chain.doFilter(request, response);
    }

    // 헤더 검증
    private boolean isHeaderVerify(HttpServletRequest request, HttpServletResponse response){
        String header = request.getHeader(JwtVo.HEADER);
        if(header == null || !header.startsWith(JwtVo.TOKEN_PREFIX)){
            return false;
        }else{
            return true;
        }
    }
}
