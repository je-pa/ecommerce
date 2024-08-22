package com.ecommerce.global.security.filter;

import static javax.swing.text.html.FormSubmitEvent.MethodType.POST;

import com.ecommerce.global.exception.CustomException;
import com.ecommerce.global.exception.ExceptionCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class LoginFilter extends AbstractAuthenticationProcessingFilter {
  private final ObjectMapper objectMapper;
  public static String loginPath = "/api/auth/login";

  public LoginFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper) {
    super(new AntPathRequestMatcher(loginPath, POST.name()), authenticationManager);
    this.objectMapper = objectMapper;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response)
      throws AuthenticationException, IOException {
    if (!POST.name().equals(request.getMethod())) {
      throw CustomException.from(ExceptionCode.AUTHENTICATION_METHOD_NOT_SUPPORTED);
    }

    if (request.getContentType() == null || !request.getContentType()
        .equals(MediaType.APPLICATION_JSON_VALUE)) {
      throw CustomException.from(ExceptionCode.CONTENT_TYPE_NOT_SUPPORTED);
    }

    LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(),
        LoginRequest.class);

    return this.getAuthenticationManager().authenticate(
        UsernamePasswordAuthenticationToken.unauthenticated(
            loginRequest.username, loginRequest.password));
  }
  private record LoginRequest(String username, String password) {

  }
}
