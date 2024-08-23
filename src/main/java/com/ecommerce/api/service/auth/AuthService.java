/**
 * @Date : 2024. 08. 23.
 * @author : jieun(je-pa)
 */
package com.ecommerce.api.service.auth;

import com.ecommerce.api.controller.ApiResponse;
import com.ecommerce.api.controller.auth.dto.request.MailRequest;

public interface AuthService {

  ApiResponse<String> sendAuthCode(MailRequest mailRequestDto);
}
