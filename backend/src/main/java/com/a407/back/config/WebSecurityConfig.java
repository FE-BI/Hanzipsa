package com.a407.back.config;


import com.a407.back.config.constants.ErrorCode;
import com.a407.back.config.jwt.TokenAuthenticationFilter;
import com.a407.back.config.jwt.TokenProvider;
import com.a407.back.dto.util.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final TokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.exceptionHandling(
            exception -> exception.authenticationEntryPoint(customTempAuthenticationEntryPoint()));
        http.exceptionHandling(
            exception -> exception.accessDeniedHandler(customAccessDeniedHandler()));
        http.sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(new TokenAuthenticationFilter(tokenProvider, userDetailsService),
            UsernamePasswordAuthenticationFilter.class);
        http.authorizeHttpRequests(
            requests -> requests.requestMatchers("/admin/**", "/temp").hasAuthority("ADMIN"));
        http.authorizeHttpRequests(
            requests -> requests.requestMatchers("/auth", "/users").permitAll().anyRequest()
                .authenticated());
        http.logout(logout -> logout.invalidateHttpSession(true).logoutSuccessUrl("/auth"));
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(AbstractHttpConfigurer::disable);

        return http.build();
    }


    @Bean
    public static BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationEntryPoint customTempAuthenticationEntryPoint() {
        return (request, response, authException) -> errorResponse(ErrorCode.USER_NOT_FOUND,
            response);
    }


    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return (request, response, accessDeniedException) -> errorResponse(
            ErrorCode.UNAUTHORIZED_ACCESS, response);
    }

    private static void errorResponse(ErrorCode errorCode, HttpServletResponse response)
        throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorResponse = new ErrorResponse(errorCode);
        objectMapper.writeValueAsString(errorResponse);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }


}
