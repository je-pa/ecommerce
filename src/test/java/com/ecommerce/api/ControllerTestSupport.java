package com.ecommerce.api;

import com.ecommerce.api.controller.auth.AuthController;
import com.ecommerce.api.service.auth.AuthService;
import com.ecommerce.global.security.jwt.util.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
    AuthController.class
})
@ActiveProfiles("test")
public abstract class ControllerTestSupport {
  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @MockBean
  protected AuthService authService;

  @MockBean
  protected TokenProvider tokenProvider;

}
