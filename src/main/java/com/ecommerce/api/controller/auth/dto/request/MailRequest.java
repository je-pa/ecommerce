/**
 * @Date : 2024. 08. 23.
 * @author : jieun(je-pa)
 */
package com.ecommerce.api.controller.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class MailRequest {

  @NotBlank(message = "이메일이 입력되지 않았습니다.")
  @Email(message = "이메일 형식이 아닙니다.")
  private String email;
}
