package com.ecommerce.global.security.service.dto;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

  private final transient UserDetailsDto userDetailsDto;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return userDetailsDto.getAuthorities();
  }

  public Long getMemberId() {
    return userDetailsDto.id();
  }

  @Override
  public String getPassword() {
    return userDetailsDto.password();
  }

  @Override
  public String getUsername() {
    return userDetailsDto.username();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

}
