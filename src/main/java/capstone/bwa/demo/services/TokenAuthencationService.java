package capstone.bwa.demo.services;

import capstone.bwa.demo.exceptions.CustomException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
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


    public static void addAuthentication(HttpServletResponse responses, String phone, boolean isAdmin, int id) {
        String token = JWT.create().withSubject(phone)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .withClaim("isAdmin", isAdmin)
                .withClaim("userId", id)
                .withIssuer("FPT")
                .sign(Algorithm.HMAC512(SECRET.getBytes()));
//        System.out.println("ADD TOKEN");
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

    public static String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public static boolean validateToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                    .withIssuer("FPT")
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (SignatureVerificationException e) {
            throw new CustomException("The Token's Signature resulted invalid", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (JwtException e) {
            throw new CustomException("Expired or invalid JWT token", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (InvalidClaimException ice) {
            throw new CustomException("Invalid Claim JWT token or Expired Time", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (JWTDecodeException jde) {
            throw new CustomException("Invalid JWT token", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

