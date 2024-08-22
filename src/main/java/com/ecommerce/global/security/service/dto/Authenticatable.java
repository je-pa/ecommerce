package com.ecommerce.global.security.service.dto;

import com.ecommerce.domain.member.type.Role;

public interface Authenticatable {
  Long getId();
  String getUsername();
  String getPassword();
  Role getRole();
}
