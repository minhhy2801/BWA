package capstone.bwa.demo.security;

import capstone.bwa.demo.services.TokenAuthencationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Collections;

public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {


    protected JWTLoginFilter(String url, AuthenticationManager manager) {
        super(new AntPathRequestMatcher(url));
        setAuthenticationManager(manager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");

//        System.out.println("phone: " + phone + " pass: " + password);
        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(phone, password));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        Collection<? extends GrantedAuthority> authorityCollections = authResult.getAuthorities();
        boolean isAdmin = authorityCollections.contains(new SimpleGrantedAuthority("ADMIN"));

        TokenAuthencationService.addAuthentication(response, authResult.getName(), isAdmin);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
