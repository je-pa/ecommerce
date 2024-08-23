/**
 * @Date : 2024. 08. 23.
 * @author : jieun(je-pa)
 */
package com.ecommerce.api.controller.auth;

import com.ecommerce.api.controller.ApiResponse;
import com.ecommerce.api.controller.auth.dto.request.MailRequest;
import com.ecommerce.api.controller.auth.dto.request.SignupRequest;
import com.ecommerce.api.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/send-email-auth-code")
  public ResponseEntity<ApiResponse<String>> sendAuthCode(
      @RequestBody @Valid MailRequest mailRequestDto) {
    return ResponseEntity.ok(authService.sendAuthCode(mailRequestDto));
  }

  @PostMapping("/signup")
  public ResponseEntity<ApiResponse<String>> signup(
      @RequestBody @Valid SignupRequest request
  ){
    return ResponseEntity.ok(authService.signup(request));
  }

}
