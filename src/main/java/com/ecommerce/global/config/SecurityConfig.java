package com.ecommerce.global.config;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import com.ecommerce.global.security.handler.LoginFailureHandler;
import com.ecommerce.global.security.handler.LoginSuccessHandler;
import com.ecommerce.global.security.handler.ExceptionHandlingFilter;
import com.ecommerce.global.security.jwt.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final ExceptionHandlingFilter exceptionHandlingFilter;
  private final JwtAuthenticationFilter authenticationFilter;
  private final LoginFailureHandler loginFailureHandler;
  private final LoginSuccessHandler loginSuccessHandler;
  private final UserDetailsService userDetailsService;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .formLogin(form -> form
            .loginPage("/api/auth/login")
            .usernameParameter("email")
            .passwordParameter("password")
            .successHandler(loginSuccessHandler)
            .failureHandler(loginFailureHandler)
            .permitAll())
        .httpBasic(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(sessionManagement ->
            sessionManagement.sessionCreationPolicy(STATELESS))
        .authorizeHttpRequests(authorizeRequests ->
            authorizeRequests.requestMatchers("/api/auth/signup", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/lectures/**").permitAll()
                .anyRequest().authenticated())
        .addFilterBefore(this.authenticationFilter, LogoutFilter.class)
        .addFilterBefore(this.exceptionHandlingFilter, JwtAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManager() {
    DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
    daoAuthenticationProvider.setUserDetailsService(userDetailsService);
    daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

    return new ProviderManager(daoAuthenticationProvider);
  }
}
