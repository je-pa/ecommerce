/**
 * @Date : 2024. 08. 23.
 * @author : jieun(je-pa)
 */
package com.ecommerce.global.mail.event;


public record SendAuthCodeEmailEvent(
    String toEmail,
    String authCode
) implements SendEmailEvent {

  @Override
  public String getToEmail() {
    return this.toEmail;
  }

  @Override
  public String getSubject(){
    return  "이메일 인증을 해주세요.";
  }

  @Override
  public String getMessage(){
    return String.format("안녕하세요! %s님.\n\n "
            + "인증번호를 입력하여 이메일 인증을 완료해주세요.\n"
            + "인증번호: %s"
        , this.toEmail, this.authCode);
  }

  public static SendAuthCodeEmailEvent from(String toEmail){
    return new SendAuthCodeEmailEvent(toEmail, setAuthCode());
  }

  private static String setAuthCode(){
    return String.format("%06d", (int)(Math.random()*1_000_000));
  }
}

