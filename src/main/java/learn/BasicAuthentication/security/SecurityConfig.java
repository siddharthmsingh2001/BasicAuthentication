package learn.BasicAuthentication.security;

import learn.BasicAuthentication.exception.handlers.AuthFailureHandler;
import learn.BasicAuthentication.security.filter.JWTAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
// https://medium.com/@ZiaurrahmanAthaya/how-to-create-session-authentication-using-spring-boot-801320adcd26

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final LogoutSuccessHandler logoutSuccessHandler;
    private final AuthFailureHandler authFailureHandler;
    private final JWTAuthFilter jwtAuthFilter;
    private static final String[] MVC_WHITE_LIST_LABEL = {"/favicon.ico"};
    private static final String[] REST_WHITE_LIST_LABEL ={
            "/v2/api-docs",
            "/v3/api-docs",
            "/v2/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"
    };
    private static final String[] MVC_AUTH_LABEL = {
            "/register",
            "/register-admin",
            "/register-user",
            "/verify-token",
            "/login"
    };
    private static final String[] REST_AUTH_LABEL ={
            "/api/auth/**"
    };
    private static final String[] MVC_CSRF_LABEL = {
    };
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @Bean
    @Order(1)
    public SecurityFilterChain mvcFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(req -> req
                        .requestMatchers(MVC_WHITE_LIST_LABEL).permitAll()
                        .requestMatchers(MVC_AUTH_LABEL).anonymous()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasRole("USER")
                        .anyRequest().authenticated())
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(MVC_CSRF_LABEL)
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .securityContext((context) ->
                        context.securityContextRepository(securityContextRepository))
                .sessionManagement(session -> session
                        .sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::newSession)
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(true))
                .authenticationProvider(authenticationProvider)
                .formLogin(form->form
                        .loginPage("/login")
                        .usernameParameter("accountEmail")
                        .passwordParameter("accountPassword")
                        .failureHandler(authFailureHandler)
                        .successHandler(authenticationSuccessHandler))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(logoutSuccessHandler)
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID"));
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain restFilterChain(HttpSecurity http) throws Exception{
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> req
                        .requestMatchers(REST_WHITE_LIST_LABEL).permitAll()
                        .requestMatchers(REST_AUTH_LABEL).anonymous()
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
