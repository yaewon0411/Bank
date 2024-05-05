package shop.mtcoding.bank.config.jwt;

/*
SECRET은 노출되면 안된다 !!
리프레시 토큰은 X 액세스 토큰만 사용

 */
public interface JwtVo {
    public static final String SECRET = "메타코딩";  //HS256(대칭키) 사용할 것임
    public static final int EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; //일주일
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER = "Authorization";
}
