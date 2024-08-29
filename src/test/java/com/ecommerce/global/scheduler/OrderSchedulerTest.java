/**
 * @Date : 2024. 08. 28.
 * @author : jieun(je-pa)
 */
package com.ecommerce.global.scheduler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ecommerce.IntegrationTestSupport;
import com.ecommerce.domain.member.entity.Member;
import com.ecommerce.domain.member.repository.MemberRepository;
import com.ecommerce.domain.member.type.Role;
import com.ecommerce.domain.order.entity.Order;
import com.ecommerce.domain.order.entity.OrderItem;
import com.ecommerce.domain.order.repository.OrderItemRepository;
import com.ecommerce.domain.order.repository.OrderRepository;
import com.ecommerce.domain.order.type.OrderStatus;
import com.ecommerce.domain.product.entity.Product;
import com.ecommerce.domain.product.entity.ProductOption;
import com.ecommerce.domain.product.repository.ProductOptionRepository;
import com.ecommerce.domain.product.repository.ProductRepository;
import com.ecommerce.domain.product.type.OptionType;
import com.ecommerce.domain.product.type.ProductCategory;
import com.ecommerce.domain.store.entity.Store;
import com.ecommerce.domain.store.repository.StoreRepository;
import com.ecommerce.domain.wishlist.repository.WishlistItemRepository;
import com.ecommerce.domain.wishlist.repository.WishlistRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    LocalDateTime dateTime = LocalDateTime.of(2024, 8, 30, 0, 0);
    List<Order> orders = orderRepository.findByStatusAndModifiedDateTimeBefore(
        OrderStatus.REQUESTED_RETURN, dateTime.minusDays(1));
    int product1Option1Quantity = 100;
    int product1Option2Quantity = 200;
    int totalQuantity = 300;
    Product product = productRepository.save(createProduct("상품1", totalQuantity));
    ProductOption product1Option1 = createProductOption("옵션1-1", product, product1Option1Quantity);
    ProductOption product1Option2 = createProductOption("옵션1-2", product, product1Option2Quantity);
    productOptionRepository.saveAll(List.of(product1Option1, product1Option2));
    int totalOrderItemProduct1Option1Quantity = 0;
    int totalOrderItemProduct1Option2Quantity = 0;
    List<OrderItem> orderItems = new ArrayList<>();
    for(Order order : orders){
      orderItems.add(createOrderItem(order, product1Option1, 10));
      totalOrderItemProduct1Option1Quantity += 10;
      orderItems.add(createOrderItem(order, product1Option2, 20));
      totalOrderItemProduct1Option2Quantity += 20;
    }
    orderItemRepository.saveAll(orderItems);
    int totalOrderItemProduct1Quantity = totalOrderItemProduct1Option1Quantity + totalOrderItemProduct1Option2Quantity;


    // when
    orderScheduler.processReturnedOrders(dateTime);

    // then
    assertThat(productRepository.findById(product.getId()))
        .isNotEmpty()
        .get()
        .extracting("name", "stockQuantity")
        .contains("상품1", totalQuantity + totalOrderItemProduct1Quantity);

  }

  private OrderItem createOrderItem(Order order, ProductOption option, int quantity){
    return OrderItem.builder()
        .order(order)
        .price(option.getPrice())
        .option(option)
        .quantity(quantity)
        .build();
  }

  // AuditingEntityListener 관계없이 실행되게 jdbc를 사용
  private void insertOrder(Long memberId, OrderStatus status, LocalDateTime createdDateTime, LocalDateTime modifiedDateTime) {
    String sql = "INSERT INTO orders (member_id, status, amount_payment, created_date_time, modified_date_time) " +
        "VALUES (?, ?, ?, ?, ?)";

    jdbcTemplate.update(sql, memberId, status.toString(), 10000, createdDateTime, modifiedDateTime);
  }

  private ProductOption createProductOption(String name, Product product, int quantity) {
    return ProductOption.builder()
        .product(product)
        .count(quantity)
        .name(name)
        .optionType(OptionType.MANDATORY)
        .price(10000)
        .build();
  }

  private Product createProduct(String name, int quantity) {
    return Product.builder()
        .name(name)
        .stockQuantity(quantity)
        .store(storeRepository.save(createStore("storeName1")))
        .category(ProductCategory.TOP)
        .info("상품정보")
        .thumbnailImgUrl("url")
        .price(10000)
        .build();
  }

  private Store createStore(String name){
    return Store.builder()
        .name(name)
        .tellNumber("encrypted")
        .info("정보")
        .build();
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