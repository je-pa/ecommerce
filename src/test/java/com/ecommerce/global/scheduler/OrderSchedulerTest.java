package com.ecommerce.global.scheduler;

import com.ecommerce.IntegrationTestSupport;
import com.ecommerce.domain.member.entity.Member;
import com.ecommerce.domain.member.repository.MemberRepository;
import com.ecommerce.domain.member.type.Role;
import com.ecommerce.domain.order.repository.OrderItemRepository;
import com.ecommerce.domain.order.repository.OrderRepository;
import com.ecommerce.domain.order.type.OrderStatus;
import com.ecommerce.domain.product.repository.ProductOptionRepository;
import com.ecommerce.domain.product.repository.ProductRepository;
import com.ecommerce.domain.store.repository.StoreRepository;
import com.ecommerce.domain.wishlist.repository.WishlistItemRepository;
import com.ecommerce.domain.wishlist.repository.WishlistRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

class OrderSchedulerTest extends IntegrationTestSupport {

  @Autowired
  private OrderScheduler orderScheduler;

  @Autowired
  private WishlistItemRepository wishlistItemRepository;

  @Autowired
  private WishlistRepository wishlistRepository;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private StoreRepository storeRepository;

  @Autowired
  private ProductOptionRepository productOptionRepository;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private OrderItemRepository orderItemRepository;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @AfterEach
  void tearDown() {
    wishlistItemRepository.deleteAllInBatch();
    wishlistRepository.deleteAllInBatch();
    orderItemRepository.deleteAllInBatch();
    productOptionRepository.deleteAllInBatch();
    productRepository.deleteAllInBatch();
    storeRepository.deleteAllInBatch();
    orderRepository.deleteAllInBatch();
    memberRepository.deleteAllInBatch();
  }

  @DisplayName("반품 요청된 주문을 반품 완료 처리를 한다.")
  @Test
  void processReturnedOrders(){
    // given
    Member member1 = memberRepository.save(createMember("email1@email.com"));

    LocalDateTime createdDateTime = LocalDateTime.of(2024, 8, 27, 0, 0);

    insertOrder(member1.getId(), OrderStatus.SHIPPING,
        createdDateTime, LocalDateTime.of(2024, 8, 28, 0, 0));
    insertOrder(member1.getId(), OrderStatus.CREATED,
        createdDateTime, LocalDateTime.of(2024, 8, 27, 0, 0));
    insertOrder(member1.getId(), OrderStatus.REQUESTED_RETURN,
        createdDateTime, LocalDateTime.of(2024, 8, 29, 0, 0));
    insertOrder(member1.getId(), OrderStatus.REQUESTED_RETURN,
        createdDateTime, LocalDateTime.of(2024, 8, 28, 0, 1));
    insertOrder(member1.getId(), OrderStatus.REQUESTED_RETURN,
        createdDateTime, LocalDateTime.of(2024, 8, 29, 0, 1));
    insertOrder(member1.getId(), OrderStatus.CREATED,
        createdDateTime, LocalDateTime.of(2024, 8, 27, 23, 59));
    insertOrder(member1.getId(), OrderStatus.RETURNED,
        createdDateTime, LocalDateTime.of(2024, 8, 30, 0, 0));
    // when
    orderScheduler.processReturnedOrders(
        LocalDateTime.of(2024, 8, 30, 0, 0));
  }

  // AuditingEntityListener 관계없이 실행되게 jdbc를 사용
  private void insertOrder(Long memberId, OrderStatus status, LocalDateTime createdDateTime, LocalDateTime modifiedDateTime) {
    String sql = "INSERT INTO orders (member_id, status, amount_payment, created_date_time, modified_date_time) " +
        "VALUES (?, ?, ?, ?, ?)";

    jdbcTemplate.update(sql, memberId, status.toString(), 10000, createdDateTime, modifiedDateTime);
  }

  private Member createMember(String email){
    return Member.builder()
        .address("서울특별시 송파구 올림픽로 240 여기동 어디게호")
        .email(email)
        .name("박땡땡")
        .password("password123@")
        .tellNumber("01022221111")
        .role(Role.GENERAL)
        .build();
  }
}