package com.ecommerce.api.service.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import com.ecommerce.IntegrationTestSupport;
import com.ecommerce.api.controller.ApiResponse;
import com.ecommerce.api.controller.auth.dto.request.MailRequest;
import com.ecommerce.api.controller.auth.dto.request.SignupRequest;
import com.ecommerce.domain.auth.repository.EmailAuthCodeRepository;
import com.ecommerce.domain.member.entity.Member;
import com.ecommerce.domain.member.repository.MemberRepository;
import com.ecommerce.domain.member.type.Role;
import com.ecommerce.global.exception.CustomException;
import com.ecommerce.global.mail.service.EmailServiceImpl.SendMailDto;
import com.ecommerce.global.security.util.MyEncoder;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthServiceTest extends IntegrationTestSupport {
  @Autowired
  private AuthService authService;

  @Autowired
  private EmailAuthCodeRepository emailAuthCodeRepository;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  @Autowired
  private MyEncoder myEncoder;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @AfterEach
  void tearDown() {
    memberRepository.deleteAllInBatch();
    Set<String> keys = redisTemplate.keys("*"); // 모든 키를 가져옴
    if (keys != null && !keys.isEmpty()) {
      redisTemplate.delete(keys); // 키들 삭제
    }
  }

  @DisplayName("이메일을 지정하여 인증 코드를 보낸다.")
  @Test
  void sendAuthCode(){
    // given
    String email = "test@email.com";
    MailRequest mailRequest = new MailRequest(email);
    String encrypted = myEncoder.encrypt(email);

    // when
    ApiResponse<String> stringApiResponse = authService.sendAuthCode(mailRequest);

    // then
    Mockito.verify(emailService, times(1)).sendMailMessage(any(SendMailDto.class));
    assertThat(stringApiResponse.getCode()).isEqualTo(200);
    assertThat(stringApiResponse.getStatus()).isEqualTo(HttpStatus.OK);
    assertThat(stringApiResponse.getMessage()).isEqualTo(HttpStatus.OK.name());
    assertThat(stringApiResponse.getData())
        .isEqualTo("인증코드를 이메일로 전송하였습니다. 인증코드의 유효시간은 1분입니다.");
    assertThat(emailAuthCodeRepository.getByKey(encrypted)).isNotBlank();
  }

  @DisplayName("이미 가입된 이메일은 인증 코드를 전송할 수 없다.")
  @Test
  void SendAuthCodeWithEmailAlreadyExists(){
    // given
    String email = "test@email.com";
    MailRequest mailRequest = new MailRequest(email);
    memberRepository.save(createMember(email));
    // when
    // then
    assertThatThrownBy(() -> authService.sendAuthCode(mailRequest))
        .isInstanceOf(CustomException.class)
        .hasMessage("중복된 이메일 입니다.");
  }

  @DisplayName("회원가입을 진행한다.")
  @Test
  void signup(){
    // given
    String email = "test@email.com";
    String authCode = "123456";
    SignupRequest signupRequest = getSignupRequest(email, authCode);
    emailAuthCodeRepository.setWithDurationByKey(myEncoder.encrypt(email), authCode);

    // when
    ApiResponse<String> signup = authService.signup(signupRequest);

    // then
    assertThat(signup)
        .extracting("code", "status", "message", "data")
        .contains(200, HttpStatus.OK, "OK", "회원가입이 완료되었습니다.");
  }

  @DisplayName("비밀번호와 비밀번호 확인을 동일하게 입력하지 않으면 회원가입을 진행할 수 없다.")
  @Test
  void signupWithPasswordMismatch(){
    String email = "test@email.com";
    String authCode = "123456";
    String name = "홍길동";
    String password = "Password1!";
    String confirmPassword = "Password2!";
    String tellNumber = "01077777777";
    String address = "서울시 용산구 333";
    SignupRequest signupRequest = new SignupRequest(
        name, email, password, confirmPassword, tellNumber, address, authCode);
    emailAuthCodeRepository.setWithDurationByKey(myEncoder.encrypt(email), authCode);

    // when
    assertThatThrownBy(() -> authService.signup(signupRequest))
        .isInstanceOf(CustomException.class)
        .hasMessage("두 비밀번호가 일치하지 않습니다.");
  }

  @DisplayName("이미 가입된 이메일은 회원가입을 진행할 수 없다.")
  @Test
  void signupWithEmailAlreadyExists(){
    // given
    String email = "test@email.com";
    String authCode = "123456";
    SignupRequest signupRequest = getSignupRequest(email, authCode);
    emailAuthCodeRepository.setWithDurationByKey(myEncoder.encrypt(email), authCode);
    memberRepository.save(createMember(email));

    // when
    // then
    assertThatThrownBy(() -> authService.signup(signupRequest))
        .isInstanceOf(CustomException.class)
        .hasMessage("중복된 이메일 입니다.");
  }

  @DisplayName("인증 코드가 발급되지 않았거나 만료가 되었다면 회원가입이 진행되지 않는다.")
  @Test
  void signupWithCodeExpiredOrInvalid(){
    // given
    String email = "test@email.com";
    String authCode = "123456";
    SignupRequest signupRequest = getSignupRequest(email, authCode);

    // when
    // then
    assertThatThrownBy(() -> authService.signup(signupRequest))
        .isInstanceOf(CustomException.class)
        .hasMessage("인증 코드가 만료되었거나 존재하지 않습니다.");
  }

  @DisplayName("인증코드가 일치하지 않으면 회원가입을 진행할 수 없다.")
  @Test
  void signupWithVerificationCodeMismatch(){
    // given
    String email = "test@email.com";
    String authCode = "123456";
    String savedAuthCode = "123457";

    SignupRequest signupRequest = getSignupRequest(email, authCode);
    emailAuthCodeRepository.setWithDurationByKey(myEncoder.encrypt(email), savedAuthCode);

    // when
    // then
    assertThatThrownBy(() -> authService.signup(signupRequest))
        .isInstanceOf(CustomException.class)
        .hasMessage("인증코드가 일치하지 않습니다.");
  }

  private static SignupRequest getSignupRequest(String email, String authCode) {
    String name = "홍길동";
    String password = "Password1!";
    String confirmPassword = "Password1!";
    String tellNumber = "01077777777";
    String address = "서울시 용산구 333";
    SignupRequest signupRequest = new SignupRequest(
        name, email, password, confirmPassword, tellNumber, address, authCode);
    return signupRequest;
  }

  private Member createMember(String email){
    String name = "박땡땡";
    String password = "Password123@";
    String tellNumber = "010-2222-1111";
    String address = "서울특별시 송파구 올림픽로 240 여기동 어디게호"; // 암호화 길이가 108이 된걸로 확인됨.
    Role role = Role.GENERAL;
    return Member.builder()
        .address(myEncoder.encrypt(address))
        .email(myEncoder.encrypt(email))
        .name(myEncoder.encrypt(name))
        .password(passwordEncoder.encode(password))
        .tellNumber(myEncoder.encrypt(tellNumber))
        .role(role)
        .build();
  }
}