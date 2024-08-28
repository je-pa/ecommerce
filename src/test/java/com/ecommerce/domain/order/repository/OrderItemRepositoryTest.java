package com.ecommerce.domain.order.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

import com.ecommerce.IntegrationTestSupport;
import com.ecommerce.domain.member.entity.Member;
import com.ecommerce.domain.member.repository.MemberRepository;
import com.ecommerce.domain.member.type.Role;
import com.ecommerce.domain.order.entity.Order;
import com.ecommerce.domain.order.entity.OrderItem;
import com.ecommerce.domain.order.type.OrderStatus;
import com.ecommerce.domain.product.entity.Product;
import com.ecommerce.domain.product.entity.ProductOption;
import com.ecommerce.domain.product.repository.ProductOptionRepository;
import com.ecommerce.domain.product.repository.ProductRepository;
import com.ecommerce.domain.product.type.OptionType;
import com.ecommerce.domain.product.type.ProductCategory;
import com.ecommerce.domain.store.entity.Store;
import com.ecommerce.domain.store.repository.StoreRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class OrderItemRepositoryTest extends IntegrationTestSupport {
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

  @DisplayName("orderId로 OrderItem을 조회할 수 있다.")
  @Test
  void findAllByOrder(){
    // given
    Product product1 = productRepository.save(createProduct("상품1"));
    Product product2 = productRepository.save(createProduct("상품2"));

    Member member1 = memberRepository.save(createMember("email1@email.com"));

    ProductOption option1 = productOptionRepository.save(createProductOption("옵션1-1", product1));
    ProductOption option2 = productOptionRepository.save(createProductOption("옵션2-2", product2));
    ProductOption option3 = productOptionRepository.save(createProductOption("옵션2-3", product2));
    ProductOption option4 = productOptionRepository.save(createProductOption("옵션2-4", product2));

    Order order1 = orderRepository.save(
        createOrder(member1, OrderStatus.CREATED, option1.getPrice()));

    Order order2 = orderRepository.save(
        createOrder(member1, OrderStatus.CREATED, option2.getPrice()+option3.getPrice()+option4.getPrice()));

    OrderItem orderItem1 = createOrderItem(order1, option1, 1);
    OrderItem orderItem2 = createOrderItem(order2, option2, 2);
    OrderItem orderItem3 = createOrderItem(order2, option3, 3);
    OrderItem orderItem4 = createOrderItem(order2, option4, 4);
    orderItemRepository.saveAll(List.of(orderItem1, orderItem2, orderItem3, orderItem4));

    // when
    List<OrderItem> result = orderItemRepository.findAllByOrder(order2.getId());

    // then
    assertThat(result)
        .extracting("option.name", "quantity")
        .containsExactlyInAnyOrder(
            tuple("옵션2-2", 2),
            tuple("옵션2-3", 3),
            tuple("옵션2-4", 4)
        );

  }

  private OrderItem createOrderItem(Order order, ProductOption option, int quantity){
    return OrderItem.builder()
        .order(order)
        .price(option.getPrice())
        .option(option)
        .quantity(quantity)
        .build();
  }

  private Order createOrder(Member member, OrderStatus status, int amountPayment) {
    return Order.builder()
        .member(member)
        .status(status)
        .amountPayment(amountPayment)
        .build();
  }

  private ProductOption createProductOption(String name, Product product) {
    return ProductOption.builder()
        .product(product)
        .count(5)
        .name(name)
        .optionType(OptionType.MANDATORY)
        .price(10000)
        .build();
  }

  private Product createProduct(String name) {
    return Product.builder()
        .name(name)
        .stockQuantity(5)
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