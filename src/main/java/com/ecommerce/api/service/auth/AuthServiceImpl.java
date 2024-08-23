/**
 * @Date : 2024. 08. 23.
 * @author : jieun(je-pa)
 */
package com.ecommerce.api.service.auth;

import com.ecommerce.api.controller.ApiResponse;
import com.ecommerce.api.controller.auth.dto.request.MailRequest;
import com.ecommerce.domain.auth.repository.EmailAuthCodeRepository;
import com.ecommerce.domain.member.repository.MemberRepository;
import com.ecommerce.global.exception.CustomException;
import com.ecommerce.global.exception.ExceptionCode;
import com.ecommerce.global.mail.event.SendAuthCodeEmailEvent;
import com.ecommerce.global.security.util.MyEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final MyEncoder myEncoder;
  private final ApplicationEventPublisher eventPublisher;
  private final EmailAuthCodeRepository emailAuthCodeRepository;
  private final MemberRepository memberRepository;

  @Override
  public ApiResponse<String> sendAuthCode(MailRequest mailRequestDto) {
    SendAuthCodeEmailEvent event = SendAuthCodeEmailEvent.from(mailRequestDto.getEmail());

    String encryptedEmail = myEncoder.encrypt(event.toEmail());
    emailAuthCodeRepository.setWithDurationByKey(encryptedEmail, event.authCode());

    if(memberRepository.existsByEmail(encryptedEmail)){
      throw CustomException.from(ExceptionCode.EMAIL_ALREADY_EXISTS);
    }

    if (!emailAuthCodeRepository.hasKey(encryptedEmail)) {
      throw CustomException.from(ExceptionCode.EMAIL_AUTH_CODE_SAVE_FAILED);
    }

    eventPublisher.publishEvent(event);
    String message = "인증코드를 이메일로 전송하였습니다. 인증코드의 유효시간은 1분입니다.";
    return ApiResponse.ok(message);
  }
}
