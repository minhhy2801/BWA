package capstone.bwa.demo.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Date;

public class TokenAuthencationService {
    public static final String SECRET = "BWA";
    public static final long EXPIRATION_TIME = 864000000; // 10 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";


    public static void addAuthentication(HttpServletResponse responses, String phone, boolean isAdmin) {
        String token = JWT.create().withSubject(phone)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .withClaim("isAdmin", isAdmin)
                .sign(Algorithm.HMAC512(SECRET.getBytes()));
        System.out.println("ADD TOKEN");
        responses.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + token);
    }

    public static Authentication getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);
        if (token != null) {
            // parse the token.
            String user = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                    .build()
                    .verify(token.replace(TOKEN_PREFIX, ""))
                    .getSubject();

            if (user != null)
                return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
            return null;
        }
        return null;
    }
}
