package com.ecommerce.global.security.service.dto;

import com.ecommerce.domain.member.type.Role;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public record UserDetailsDto(
    @NotNull
    Long id,

    String username,

    String password,

    @NotNull
    Role role

) {

  public static UserDetailsDto from(Authenticatable user) {
    return new UserDetailsDto(
        user.getId(),
        user.getUsername(),
        user.getPassword(),
        user.getRole()
    );
  }

  public Collection<? extends GrantedAuthority> getAuthorities() {
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(role.getAuthority()));
    return authorities;
  }
}
