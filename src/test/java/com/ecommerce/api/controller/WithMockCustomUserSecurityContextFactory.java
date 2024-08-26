package com.ecommerce.api.controller;

import com.ecommerce.domain.member.type.Role;
import com.ecommerce.global.security.service.dto.CustomUserDetails;
import com.ecommerce.global.security.service.dto.UserDetailsDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory implements
    WithSecurityContextFactory<WithMockCustomUser> {

  @Override
  public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();

    UserDetailsDto userDetailsDto = new UserDetailsDto(
        customUser.memberId(),
        customUser.username(),
        "password",  // 이 부분은 실제 테스트 환경에 맞게 설정
        Role.GENERAL
    );

    CustomUserDetails principal = new CustomUserDetails(userDetailsDto);
    Authentication auth = new UsernamePasswordAuthenticationToken(principal, "password", principal.getAuthorities());
    context.setAuthentication(auth);
    return context;
  }
}
