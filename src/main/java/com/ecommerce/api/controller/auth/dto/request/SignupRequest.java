/**
 * @Date : 2024. 08. 23.
 * @author : jieun(je-pa)
 */
package com.ecommerce.api.controller.auth.dto.request;

import com.ecommerce.api.controller.annotation.ValidPassword;
import com.ecommerce.api.controller.annotation.ValidPhoneNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignupRequest(

    @NotBlank(message = "이름이 입력되지 않았습니다.")
    String name,

    @NotBlank(message = "이메일이 입력되지 않았습니다.")
    @Email(message = "이메일 형식이 아닙니다.")
    String email,

    @NotBlank(message = "비밀번호가 입력되지 않았습니다.")
    @ValidPassword
    String password,

    @NotBlank(message = "비밀번호가 입력되지 않았습니다.")
    @ValidPassword
    String confirmPassword,

    @NotBlank(message = "휴대폰 번호가 입력되지 않았습니다.")
    @ValidPhoneNumber
    String tellNumber,

    @NotBlank(message = "주소가 입력되지 않았습니다.")
    String address,

    @NotBlank(message = "인증 코드가 입력되지 않았습니다.")
    String authCode
) {

}
