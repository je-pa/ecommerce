/**
 * @Date : 2024. 08. 23.
 * @author : jieun(je-pa)
 */
package com.ecommerce.global.mail.service;

import com.ecommerce.global.mail.service.EmailServiceImpl.SendMailDto;

public interface EmailService {
  void sendMailMessage(SendMailDto mailInfo);
}
