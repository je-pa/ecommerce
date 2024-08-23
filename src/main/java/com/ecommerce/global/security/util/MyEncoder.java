/**
 * @Date : 2024. 08. 23.
 * @author : jieun(je-pa)
 */
package com.ecommerce.global.security.util;

import com.ecommerce.global.exception.CustomException;
import com.ecommerce.global.exception.ExceptionCode;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MyEncoder {
  @Value("${encoder.algorithm}")
  private String ALGORITHM;
  @Value("${encoder.secret-key}")
  private String KEY;

  public String encrypt(String plainText) {
    Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
    byte[] encryptedBytes = null;
    try {
      encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
    } catch (IllegalBlockSizeException e) {
      throw CustomException.from(ExceptionCode.ILLEGAL_BLOCK_SIZE);
    } catch (BadPaddingException e) {
      throw CustomException.from(ExceptionCode.BAD_PADDING);
    }
    return Base64.getEncoder().encodeToString(encryptedBytes);
  }

  public String decrypt(String encryptedText) {
    Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
    byte[] decryptedBytes = null;
    try {
      decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
    } catch (IllegalBlockSizeException e) {
      throw CustomException.from(ExceptionCode.ILLEGAL_BLOCK_SIZE);
    } catch (BadPaddingException e) {
      throw CustomException.from(ExceptionCode.BAD_PADDING);
    }
    return new String(decryptedBytes, StandardCharsets.UTF_8);
  }

  private Cipher getCipher(int decryptMode) {
    SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
    Cipher cipher = null;
    try {
      cipher = Cipher.getInstance(ALGORITHM);
    } catch (NoSuchAlgorithmException e) {
      throw CustomException.from(ExceptionCode.NO_SUCH_ALGORITHM);
    } catch (NoSuchPaddingException e) {
      throw CustomException.from(ExceptionCode.NO_SUCH_PADDING);
    }
    try {
      cipher.init(decryptMode, secretKey);
    } catch (InvalidKeyException e) {
      throw CustomException.from(ExceptionCode.INVALID_KEY);
    }
    return cipher;
  }
}
