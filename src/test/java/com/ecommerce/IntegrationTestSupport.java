package com.ecommerce;

import com.ecommerce.global.mail.service.EmailService;
import com.ecommerce.global.security.jwt.util.TokenProvider;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
public abstract class IntegrationTestSupport {

  @MockBean
  protected TokenProvider tokenProvider;

  @MockBean
  protected EmailService emailService;
}
