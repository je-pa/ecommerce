/**
 * @Date : 2024. 08. 23.
 * @author : jieun(je-pa)
 */
package com.ecommerce.global.mail.service;

import com.ecommerce.global.exception.CustomException;
import com.ecommerce.global.exception.ExceptionCode;
import com.ecommerce.global.mail.event.SendEmailEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

  private final JavaMailSender javaMailSender;
  @Value("${spring.mail.username}")
  private String mailUsername;

  @Override
  public void sendMailMessage(SendMailDto mailInfo) {
    MimeMessage message = javaMailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
    try {
      helper.setFrom(mailUsername);//보내는사람
      helper.setTo(mailInfo.toEmail());//받는 사람 이메일 주소
      helper.setSubject(mailInfo.subject());//제목
      helper.setText(mailInfo.content, true);//내용 true : html 사용
    } catch (MessagingException e) {
      throw CustomException.from(ExceptionCode.EMAIL_SENDING_FAILED);
    }
    javaMailSender.send(message);
  }

  @Builder
  public record SendMailDto(
      String toEmail,
      String subject,
      String content
  ) {

    public static SendMailDto from(SendEmailEvent event) {
      return SendMailDto.builder()
          .toEmail(event.getToEmail())
          .subject(event.getSubject())
          .content(event.getMessage())
          .build();
    }
  }
}
