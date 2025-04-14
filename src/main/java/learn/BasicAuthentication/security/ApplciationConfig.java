package learn.BasicAuthentication.security;

import learn.BasicAuthentication.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.SessionFlashMapManager;

import java.security.SecureRandom;
import java.util.HashSet;

@RequiredArgsConstructor
@Configuration
public class ApplciationConfig {

    private final AccountService accountService;

    @Bean
    public SecureRandom secureRandom(){
        return new SecureRandom();
    }

    @Bean
    public HashSet<String> hashSetString(){
        return new HashSet<String>();
    }

    // The Job of the AuthenticationManager is to simply configure the AuthenticationProvider. A project can have multiple AuthenticationProviders as OAuth or  username/password it's the job of the AuthenticationManager to delegate the authentication request to the providers in sequence.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // The Job of the AuthenticationProvider is to simply ensure that the entered credentials of the Client is valid or not this can be done in different ways such as OAuth or  username/password Authentication. Here we are using a simple username/password authentication which uses DaoAuthenticationProvider.
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(accountService);
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setHideUserNotFoundExceptions(false);
        return authProvider;
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(){
        return (request, response, authentication) -> {
            String role = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElse("ROLE_USER");  // Default to USER if no role is found

            if (role.equals("ROLE_ADMIN")) {
                response.sendRedirect("/admin/dashboard");
            } else if (role.equals("ROLE_USER")) {
                response.sendRedirect("/user/dashboard");
            } else {
                response.sendRedirect("/login?error");
            }
        };
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler(){
        return (request, response, authentication)->{
            FlashMap outputFlashMap = new FlashMap();
            outputFlashMap.put("message","You have successfully logged out !");
            new SessionFlashMapManager().saveOutputFlashMap(outputFlashMap, request, response);
            redirectStrategy().sendRedirect(request,response,"/login");
        };
    }

    @Bean
    public RedirectStrategy redirectStrategy(){
        return new DefaultRedirectStrategy();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
