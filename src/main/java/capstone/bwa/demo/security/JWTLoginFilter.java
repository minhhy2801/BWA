package capstone.bwa.demo.security;

import capstone.bwa.demo.controllers.AccountController;
import capstone.bwa.demo.repositories.AccountRepository;
import capstone.bwa.demo.services.TokenAuthencationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {


    protected JWTLoginFilter(String url, AuthenticationManager manager) {
        super(new AntPathRequestMatcher(url));
        setAuthenticationManager(manager);
    }

    // 1
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");

//        System.out.println("phone: " + phone + " pass: " + password);
        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(phone, password));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        Collection<? extends GrantedAuthority> authorityCollections = authResult.getAuthorities();  //account service
        boolean isAdmin = authorityCollections.contains(new SimpleGrantedAuthority("ADMIN"));
//        int id = accountController.getAccount(authResult.getName()).getId();
//        int id = 0;
        TokenAuthencationService.addAuthentication(response, authResult.getName().split(";")[1], isAdmin, Integer.parseInt(authResult.getName().split(";")[0]));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
