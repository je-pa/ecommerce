package com.ecommerce.domain.member.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {
  GENERAL("일반유저", Authority.GENERAL);

  private final String value;
  @Getter
  private final String authority;

  public static class Authority {
    public static final String ADMIN = "ROLE_ADMIN";
    public static final String GENERAL = "ROLE_GENERAL";
  }
}
