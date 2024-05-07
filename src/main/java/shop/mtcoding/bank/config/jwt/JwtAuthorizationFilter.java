package shop.mtcoding.bank.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if(isHeaderVerify(request, response)){
            //토큰이 존재함
            String token = request.getHeader(JwtVo.HEADER).replace(JwtVo.TOKEN_PREFIX, ""); //Bearer 제거

            //토큰 검증
            LoginUser loginUser = JwtProcess.verify(token);

            //임시 세션(UserDetails 타입 or username). 넘기는 유저 role이 중요
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities()); //id와 role만 존재함

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
