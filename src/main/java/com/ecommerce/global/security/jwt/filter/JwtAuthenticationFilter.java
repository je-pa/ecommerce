package com.ecommerce.global.security.jwt.filter;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.ecommerce.global.security.jwt.util.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final TokenProvider tokenProvider;


  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String accessToken = request.getHeader(AUTHORIZATION);
    log.info("[JwtAuthenticationFilter]{}", accessToken);
    if (tokenProvider.validAccessToken(accessToken)
    ) {
      SecurityContextHolder.getContext().setAuthentication(
          this.tokenProvider.getAuthentication(accessToken));
    }


    filterChain.doFilter(request, response);
  }

}
