package com.ecommerce.api.controller.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ecommerce.api.ControllerTestSupport;
import com.ecommerce.api.controller.auth.dto.request.MailRequest;
import com.ecommerce.api.controller.auth.dto.request.SignupRequest;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

class AuthControllerTest extends ControllerTestSupport {

  @DisplayName("인증번호를 발송한다.")
  @Test
  @WithMockUser
  void sendAuthCode() throws Exception {
    // given
    String email = "test@email.com";
    MailRequest mailRequest = new MailRequest(email);

    mockMvc.perform(
        post("/api/auth/send-email-auth-code")
            .content(objectMapper.writeValueAsString(mailRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
    ).andDo(print())
        .andExpect(status().isOk());
  }

  @DisplayName("이메일은 필수 값이다.")
  @Test
  @WithMockUser
  void sendAuthCodeWithEmailIsNull() throws Exception {
    // given
    MailRequest mailRequest = new MailRequest(null);

    mockMvc.perform(
            post("/api/auth/send-email-auth-code")
                .content(objectMapper.writeValueAsString(mailRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        ).andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("400"))
        .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.message").value("{email : 이메일이 입력되지 않았습니다.}, "));
  }

  @DisplayName("이메일은 형식을 맞추어야 한다.")
  @Test
  @WithMockUser
  void sendAuthCodeWithEmailForm() throws Exception {
    // given
    String email = "testemail.com";
    MailRequest mailRequest = new MailRequest(email);

    mockMvc.perform(
            post("/api/auth/send-email-auth-code")
                .content(objectMapper.writeValueAsString(mailRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        ).andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("400"))
        .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.message").value("{email : 이메일 형식이 아닙니다.}, "));
  }

  @DisplayName("가입을 한다.")
  @Test
  @WithMockUser
  void signup() throws Exception {
    // given
    String name = "홍길동";
    String email = "test@email.com";
    String password = "Password1!";
    String confirmPassword = "Password1!";
    String tellNumber = "010-7777-7777";
    String address = "서울시 용산구 333";
    String authCode = "123456";
    SignupRequest signupRequest = new SignupRequest(name, email, password, confirmPassword,
        tellNumber, address, authCode);

    mockMvc.perform(
            post("/api/auth/signup")
                .content(objectMapper.writeValueAsString(signupRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        ).andDo(print())
        .andExpect(status().isOk());
  }

  @DisplayName("필수 값을 모두 입력해야 한다.")
  @Test
  @WithMockUser
  void signupWithNotBlank() throws Exception {
    // given
    String name = null;
    String email = null;
    String password = null;
    String confirmPassword = null;
    String tellNumber = null;
    String address = null;
    String authCode = null;
    SignupRequest signupRequest = new SignupRequest(name, email, password, confirmPassword,
        tellNumber, address, authCode);

    mockMvc.perform(
            post("/api/auth/signup")
                .content(objectMapper.writeValueAsString(signupRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        ).andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("400"))
        .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
  }

  @DisplayName("비밀번호는 올바른 형식이어야 한다.")
  @MethodSource("validPassword")
  @ParameterizedTest(name = "[{index}] ''{0}'': 비밀번호는 {1}")
  @WithMockUser
  void signupWithValidPassword(String password, String message) throws Exception {
    // given
    String name = "홍길동";
    String email = "test@email.com";
    String confirmPassword = "Password1!";
    String tellNumber = "010-7777-7777";
    String address = "서울시 용산구 333";
    String authCode = "123456";
    SignupRequest signupRequest = new SignupRequest(name, email, password, confirmPassword,
        tellNumber, address, authCode);

    mockMvc.perform(
            post("/api/auth/signup")
                .content(objectMapper.writeValueAsString(signupRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        ).andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("400"))
        .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.message").value("{password : 올바른 비밀번호 형식이 아닙니다.}, "));
  }

  private static Stream<Arguments> validPassword() {
    return Stream.of(
        Arguments.of("Password!", "적어도 숫자 하나가 포함되어야 합니다."),
        Arguments.of("Password1", "적어도 특수문자 하나가 포함되어야 합니다."),
        Arguments.of("password1!", "적어도 대문자 하나가 포함되어야 합니다."),
        Arguments.of("PASSWORD1!", "적어도 소문자 하나가 포함되어야 합니다."),
        Arguments.of("Pa1!567", "최소 8자 입니다."),
        Arguments.of("Pa1!567890123456", "최대 15자 입니다.")
    );
  }

  @DisplayName("올바른 휴대폰 번호 형식이어야 한다.")
  @Test
  @WithMockUser
  void signupWithValidPhoneNumber() throws Exception {
    // given
    String name = "홍길동";
    String email = "test@email.com";
    String password = "Password1!";
    String confirmPassword = "Password1!";
    String tellNumber = "01077777777";
    String address = "서울시 용산구 333";
    String authCode = "123456";
    SignupRequest signupRequest = new SignupRequest(name, email, password, confirmPassword,
        tellNumber, address, authCode);

    mockMvc.perform(
            post("/api/auth/signup")
                .content(objectMapper.writeValueAsString(signupRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        ).andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("400"))
        .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.message").value("{tellNumber : 올바른 휴대폰 번호 형식이 아닙니다.}, "));
  }
}