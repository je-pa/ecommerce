package com.ecommerce.global.security.service;

import static com.ecommerce.global.exception.ExceptionCode.USER_NOT_FOUND;

import com.ecommerce.domain.member.repository.MemberRepository;
import com.ecommerce.global.security.service.dto.CustomUserDetails;
import com.ecommerce.global.security.service.dto.UserDetailsDto;
import com.ecommerce.global.security.util.MyEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
  private final MyEncoder myEncoder;
  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return new CustomUserDetails(getUserDetailsDomain(username));
  }

  private UserDetailsDto getUserDetailsDomain(String username) {
    return UserDetailsDto.from(memberRepository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException(
            USER_NOT_FOUND.getMessage()
        )));
  }
}