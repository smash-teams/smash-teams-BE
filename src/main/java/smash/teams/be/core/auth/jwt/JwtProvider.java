package smash.teams.be.core.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import smash.teams.be.model.user.User;

import java.util.Date;

@Component
public class JwtProvider {

    private static final int EXP = 1000 * 60 * 60 * 24; // 24시간
    public static final String TOKEN_PREFIX = "Bearer "; // 스페이스 필요함
    public static final String HEADER = "Authorization";
    private static String SECRET = "승민"; // UserService 유닛 테스트를 위한 임시값 할당

    @Value("${JWT_SECRET_KEY}")
    public void setSecret(String secret) {
        SECRET = secret;
    }

    public static String create(User user) {
        String jwt = JWT.create()
                .withExpiresAt(new Date(System.currentTimeMillis() + EXP))
                .withClaim("id", user.getId())
                .withClaim("role", user.getRole())
                .sign(Algorithm.HMAC512(SECRET));
        return TOKEN_PREFIX + jwt;
    }

    public static DecodedJWT verify(String jwt) throws SignatureVerificationException, TokenExpiredException {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(SECRET))
                .build().verify(jwt);
        return decodedJWT;
    }
}