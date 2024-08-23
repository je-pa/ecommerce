package com.ecommerce.domain.member.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.ecommerce.IntegrationTestSupport;
import com.ecommerce.domain.member.entity.Member;
import com.ecommerce.domain.member.type.Role;
import com.ecommerce.global.security.util.MyEncoder;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class MemberRepositoryTest extends IntegrationTestSupport {

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private MyEncoder myEncoder;

  @DisplayName("개인정보를 암호화한 member를 생성한다.")
  @Test
  void createMember() {
    // given
    String name = "박땡땡";
    String email = "example@example.com";
    String password = "password123@";
    String tellNumber = "01022221111";
    String address = "서울특별시 송파구 올림픽로 240 여기동 어디게호"; // 암호화 길이가 108이 된걸로 확인됨.
    Role role = Role.GENERAL;
    Member member = Member.builder()
        .address(myEncoder.encrypt(address))
        .email(myEncoder.encrypt(email))
        .name(myEncoder.encrypt(name))
        .password(passwordEncoder.encode(password))
        .tellNumber(myEncoder.encrypt(tellNumber))
        .role(role)
        .build();

    // when
    memberRepository.save(member);

    // then
    List<Member> all = memberRepository.findAll();
    assertThat(all).hasSize(1)
        .extracting("name", "email", "tellNumber", "role")
        .containsExactlyInAnyOrder(
            tuple(myEncoder.encrypt(name), myEncoder.encrypt(email), myEncoder.encrypt(tellNumber), role)
        );
    // 복호화된 값이 원본 값과 일치하는지 확인
    assertThat(myEncoder.decrypt(all.get(0).getName())).isEqualTo(name);
    assertThat(myEncoder.decrypt(all.get(0).getEmail())).isEqualTo(email);
    assertThat(myEncoder.decrypt(all.get(0).getTellNumber())).isEqualTo(tellNumber);
    assertThat(passwordEncoder.matches(password, all.get(0).getPassword())).isTrue();
  }

  @DisplayName("이메일로 유저를 조회할 수 있다.")
  @Test
  void findByEmail(){
    // given
    String email1 = "example1@example.com";
    String email2 = "example2@example.com";
    Member member1 = createMember(email1);
    Member member2 = createMember(email2);
    memberRepository.saveAll(List.of(member1, member2));
    String encryptedEmail1 = myEncoder.encrypt(email1);

    // when
    Member member = memberRepository.findByEmail(encryptedEmail1).get();

    // then
    assertThat(member.getEmail()).isEqualTo(encryptedEmail1);
  }

  @DisplayName("이메일로 유저를 조회할 수 있다.")
  @Test
  void existsByEmail(){
    // given
    String email1 = "example1@example.com";
    String email2 = "example2@example.com";
    Member member1 = createMember(email1);
    Member member2 = createMember(email2);
    memberRepository.saveAll(List.of(member1, member2));
    String encryptedEmail1 = myEncoder.encrypt(email1);

    // when
    boolean result = memberRepository.existsByEmail(encryptedEmail1);
    // then
    assertThat(result).isTrue();
  }

  private Member createMember(String email){
    String name = "박땡땡";
    String password = "password123@";
    String tellNumber = "01022221111";
    String address = "서울특별시 송파구 올림픽로 240 여기동 어디게호"; // 암호화 길이가 108이 된걸로 확인됨.
    Role role = Role.GENERAL;
    return Member.builder()
        .address(myEncoder.encrypt(address))
        .email(myEncoder.encrypt(email))
        .name(myEncoder.encrypt(name))
        .password(passwordEncoder.encode(password))
        .tellNumber(myEncoder.encrypt(tellNumber))
        .role(role)
        .build();
  }
}