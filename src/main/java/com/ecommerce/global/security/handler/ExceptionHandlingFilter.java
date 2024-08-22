package com.ecommerce.global.security.handler;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import com.ecommerce.global.exception.CustomException;
import com.ecommerce.global.exception.handler.dto.ExceptionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
@Component
public class ExceptionHandlingFilter extends OncePerRequestFilter {
  private final ObjectMapper objectMapper;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    try {
      filterChain.doFilter(request, response);
    } catch (CustomException e) {
      log.error(e.getMessage());
      response.setStatus(e.getStatusCode().value());
      response.setContentType(APPLICATION_JSON_VALUE);
      response.setCharacterEncoding("UTF-8");

      response.getWriter().print(
          objectMapper.writeValueAsString(ExceptionResponse.of(e.getStatusCode(),e.getMessage())));
    }
  }
}
