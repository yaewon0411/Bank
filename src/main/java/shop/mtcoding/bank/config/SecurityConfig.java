package shop.mtcoding.bank.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import shop.mtcoding.bank.config.jwt.JwtAuthenticationFilter;
import shop.mtcoding.bank.config.jwt.JwtAuthorizationFilter;
import shop.mtcoding.bank.domain.user.UserEnum;
import shop.mtcoding.bank.dto.ResponseDto;
import shop.mtcoding.bank.util.CustomResponseUtil;

import java.util.Collections;


//@Slf4j - Jnuit 테스트 할 때 이거 걸려 있으면 문제 생겨서 일단 Logger로 걸었음
@Configuration //이게 붙어야 Bean이 등록됨
@EnableWebSecurity
public class SecurityConfig {

    private final Logger log = LoggerFactory.getLogger((getClass()));
    @Bean //IoC 컨테이너에 BCryptPasswordEncoder() 객체가 등록됨
    public BCryptPasswordEncoder passwordEncoder(){
        log.debug("디버그 : BCryptPasswordEncoder 빈 등록됨");
        return new BCryptPasswordEncoder();
    }
    //JWT 필터 등록 필요
    public class CustomSecurityFilterManager extends AbstractHttpConfigurer<CustomSecurityFilterManager, HttpSecurity>{

        @Override
        public void configure(HttpSecurity builder) throws Exception {
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
            builder.addFilter(new JwtAuthenticationFilter(authenticationManager)); //만든 필터 등록
            builder.addFilter(new JwtAuthorizationFilter(authenticationManager)); //만든 필터 등록
            super.configure(builder);
        }
    }


    //JWT 서버를 만들 예정!! Session 사용 안함
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        log.debug("디버그 : filterChain 빈 등록됨"); //설정이 잘 되었나 테스트
        http
                .headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(configurationSource()))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/s/**").authenticated()
                        .requestMatchers("/api/admin/**").hasRole("" + UserEnum.ADMIN)
                        .anyRequest().permitAll()
                )
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                //필터 적용
                .with(new CustomSecurityFilterManager(), customSecurityFilterManager -> customSecurityFilterManager.getClass())


                //인증 실패 Exception 가로채기
                .exceptionHandling(handler -> handler.authenticationEntryPoint((request, response, authException) -> {
                    CustomResponseUtil.fail(response,"로그인을 진행해 주세요", HttpStatus.UNAUTHORIZED);
                }))
                //권한 실패 Exception 가로채기
                .exceptionHandling(handler -> handler.accessDeniedHandler((request, response, e) -> {
                    CustomResponseUtil.fail(response, "권한이 없습니다", HttpStatus.FORBIDDEN);
                }));

        return http.build();
    }

    public CorsConfigurationSource configurationSource() {

        log.debug("디버그 : configurationSource cors 설정이 SecurityFilterChain에 등록됨");
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedMethods(Collections.singletonList("*")); //모든 HTTP 메서드(자바스크립트 요청) 허용
        config.setAllowedHeaders(Collections.singletonList("*")); // 모든 HTTP 헤더 허용
        config.setAllowCredentials(true); //클라이언트에서 쿠키 요청 허용
        config.addAllowedOriginPattern("*"); //모든 IP 주소 허용 (프론트엔드 IP만 허용)
        config.setMaxAge(3600L); //1시간 동안 캐시


        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
