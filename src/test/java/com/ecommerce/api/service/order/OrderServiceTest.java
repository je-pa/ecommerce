/**
 * @Date : 2024. 08. 27.
 * @author : jieun(je-pa)
 */
package com.ecommerce.api.service.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

import com.ecommerce.IntegrationTestSupport;
import com.ecommerce.api.controller.ApiResponse;
import com.ecommerce.api.controller.order.dto.request.OrderStatusRequest;
import com.ecommerce.api.service.order.dto.request.CreateOrderByWishlistItemsDto;
import com.ecommerce.api.service.order.dto.request.CreateOrderByWishlistItemsDto.WishlistItemsDto;
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
import com.ecommerce.domain.wishlist.entity.Wishlist;
import com.ecommerce.domain.wishlist.entity.WishlistItem;
import com.ecommerce.domain.wishlist.repository.WishlistItemRepository;
import com.ecommerce.domain.wishlist.repository.WishlistRepository;
import com.ecommerce.global.exception.CustomException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class OrderServiceTest extends IntegrationTestSupport {
  @Autowired
  private OrderService orderService;

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

  @DisplayName("위시리스트 항목으로 주문을 진행한다.")
  @Test
  void create(){
    // given
    Product product1 = productRepository.save(createProduct("상품1"));
    Product product2 = productRepository.save(createProduct("상품2"));

    Member member1 = memberRepository.save(createMember("email1@email.com"));

    Wishlist wishlist1 = wishlistRepository.save(createWishlist(member1, product1));
    Wishlist wishlist2 = wishlistRepository.save(createWishlist(member1, product2));

    ProductOption option1 = productOptionRepository.save(createProductOption("옵션1-1", product1));
    ProductOption option2 = productOptionRepository.save(createProductOption("옵션1-2", product1));
    ProductOption option3 = productOptionRepository.save(createProductOption("옵션2-3", product2));
    ProductOption option4 = productOptionRepository.save(createProductOption("옵션2-4", product2));

    WishlistItem item1  = wishlistItemRepository.save(createWishlistItem(wishlist1, option1, 1)); // member1, product1, option1
    WishlistItem item2  = wishlistItemRepository.save(createWishlistItem(wishlist1, option2, 2)); // member1, product1, option2
    WishlistItem item3  = wishlistItemRepository.save(createWishlistItem(wishlist2, option3, 3)); // member1, product2, option3
    wishlistItemRepository.save(createWishlistItem(wishlist2, option4, 3)); // member1, product2, option4

    // when
    ApiResponse<String> result = orderService.create(new CreateOrderByWishlistItemsDto(
        List.of(
            new WishlistItemsDto(item1.getId()),
            new WishlistItemsDto(item2.getId()),
            new WishlistItemsDto(item3.getId())),
        member1.getId()));

    // then
    List<OrderItem> orderItems = orderItemRepository.findAll();
    List<Order> orders = orderRepository.findAll();
    List<WishlistItem> items = wishlistItemRepository.findAll();
    List<Wishlist> wishlists = wishlistRepository.findAll();
    assertThat(orderItems).hasSize(3)
        .extracting("price", "quantity")
        .containsExactlyInAnyOrder(
            tuple(10000, 1),
            tuple(10000, 2),
            tuple(10000, 3)
        );
    assertThat(orders).hasSize(1)
        .extracting("amountPayment", "status")
        .containsExactlyInAnyOrder(
            tuple(60000, OrderStatus.CREATED)
        );
    assertThat(items).hasSize(1);
    assertThat(wishlists).hasSize(1);
    assertThat(result)
        .extracting("code", "status", "message", "data")
        .contains(200, HttpStatus.OK, "OK", "주문 생성이 완료되었습니다.");
  }

  @DisplayName("wishlistId가 중복으로 들어오면 주문을 진행할 수 없다.")
  @Test
  void createWithWishlistItemDuplicate() {
    // given
    Product product1 = productRepository.save(createProduct("상품1"));

    Member member1 = memberRepository.save(createMember("email1@email.com"));

    Wishlist wishlist1 = wishlistRepository.save(createWishlist(member1, product1));

    ProductOption option1 = productOptionRepository.save(createProductOption("옵션1-1", product1));
    ProductOption option2 = productOptionRepository.save(createProductOption("옵션1-2", product1));

    WishlistItem item1  = wishlistItemRepository.save(createWishlistItem(wishlist1, option1, 1)); // member1, product1, option1
    WishlistItem item2  = wishlistItemRepository.save(createWishlistItem(wishlist1, option2, 2)); // member1, product1, option2

    // when
    // then
    assertThatThrownBy(() ->orderService.create(new CreateOrderByWishlistItemsDto(
        List.of(
            new WishlistItemsDto(item1.getId()),
            new WishlistItemsDto(item2.getId()),
            new WishlistItemsDto(item2.getId())),
        member1.getId()))).isInstanceOf(CustomException.class)
        .hasMessage("중복된 상품 옵션이 있습니다.");
  }

  @DisplayName("위시리스트 정보가 없는 wishlistId 로는 주문을 진행할 수 없다.")
  @Test
  void createWithWishlistItemNotFound() {
    // given
    Product product1 = productRepository.save(createProduct("상품1"));

    Member member1 = memberRepository.save(createMember("email1@email.com"));

    Wishlist wishlist1 = wishlistRepository.save(createWishlist(member1, product1));

    ProductOption option1 = productOptionRepository.save(createProductOption("옵션1-1", product1));
    ProductOption option2 = productOptionRepository.save(createProductOption("옵션1-2", product1));

    WishlistItem item1  = wishlistItemRepository.save(createWishlistItem(wishlist1, option1, 1)); // member1, product1, option1
    WishlistItem item2  = wishlistItemRepository.save(createWishlistItem(wishlist1, option2, 2)); // member1, product1, option2

    // when
    // then
    assertThatThrownBy(() ->orderService.create(new CreateOrderByWishlistItemsDto(
        List.of(
            new WishlistItemsDto(item1.getId()),
            new WishlistItemsDto(item2.getId()),
            new WishlistItemsDto(0L)),
        member1.getId()))).isInstanceOf(CustomException.class)
        .hasMessage("위시리스트 항목 개체를 찾지 못했습니다.");
  }

  @DisplayName("본인의 위시리스트 항목만 주문을 진행할 수 있다.")
  @Test
  void createWithUnauthorizedAccess() {
    // given
    Product product1 = productRepository.save(createProduct("상품1"));
    Product product2 = productRepository.save(createProduct("상품2"));

    Member member1 = memberRepository.save(createMember("email1@email.com"));
    Member member2 = memberRepository.save(createMember("email2@email.com"));

    Wishlist wishlist1 = wishlistRepository.save(createWishlist(member1, product1));
    Wishlist wishlist2 = wishlistRepository.save(createWishlist(member2, product2));

    ProductOption option1 = productOptionRepository.save(createProductOption("옵션1-1", product1));
    ProductOption option2 = productOptionRepository.save(createProductOption("옵션1-2", product1));
    ProductOption option3 = productOptionRepository.save(createProductOption("옵션2-3", product2));

    WishlistItem item1  = wishlistItemRepository.save(createWishlistItem(wishlist1, option1, 1)); // member1, product1, option1
    WishlistItem item2  = wishlistItemRepository.save(createWishlistItem(wishlist1, option2, 2)); // member1, product1, option2
    WishlistItem item3  = wishlistItemRepository.save(createWishlistItem(wishlist2, option3, 3)); // member1, product2, option3

    // when
    // then
    assertThatThrownBy(() ->orderService.create(new CreateOrderByWishlistItemsDto(
        List.of(
            new WishlistItemsDto(item1.getId()),
            new WishlistItemsDto(item2.getId()),
            new WishlistItemsDto(item3.getId())),
        member2.getId()))).isInstanceOf(CustomException.class)
        .hasMessage("해당 정보를 조회할 수 있는 권한이 없습니다");
  }

  @DisplayName("주문을 취소한다.")
  @Test
  void updateStatusCancel(){
    // given
    Product product1 = productRepository.save(createProduct("상품1"));

    Member member1 = memberRepository.save(createMember("email1@email.com"));

    ProductOption option1 = productOptionRepository.save(createProductOption("옵션1-1", product1));
    ProductOption option2 = productOptionRepository.save(createProductOption("옵션1-2", product1));
    ProductOption option3 = productOptionRepository.save(createProductOption("옵션1-3", product1));

    Order order = orderRepository.save(
        createOrder(member1, OrderStatus.CREATED, option1.getPrice() + option2.getPrice()));
    OrderItem orderItem1 = createOrderItem(order, option1, 1);
    OrderItem orderItem2 = createOrderItem(order, option2, 2);
    orderItemRepository.saveAll(List.of(orderItem1, orderItem2));

    // when
    ApiResponse<String> result = orderService.updateStatus(member1.getId(),
        order.getId(), OrderStatusRequest.CANCEL);

    // then
    assertThat(orderRepository.findById(order.getId()).get().getStatus()).isEqualTo(OrderStatus.CANCELLED);
    assertThat(productOptionRepository.findAll())
        .extracting("name", "count")
        .containsExactlyInAnyOrder(
            tuple("옵션1-1", 6),
            tuple("옵션1-2", 7),
            tuple("옵션1-3", 5)
        );
    assertThat(productRepository.findById(product1.getId()).get())
        .extracting("name", "stockQuantity")
        .contains("상품1", 6+7+option3.getCount());
    assertThat(result)
        .extracting("code", "status", "message", "data")
        .contains(200, HttpStatus.OK, "OK", "상태가 업데이트 되었습니다.");
  }

  @DisplayName("주문을 반품 요청한다.")
  @Test
  void updateStatusRequestReturn(){
    // given
    Product product1 = productRepository.save(createProduct("상품1"));

    Member member1 = memberRepository.save(createMember("email1@email.com"));

    ProductOption option1 = productOptionRepository.save(createProductOption("옵션1-1", product1));
    ProductOption option2 = productOptionRepository.save(createProductOption("옵션1-2", product1));
    productOptionRepository.save(createProductOption("옵션1-3", product1));

    Order order = orderRepository.save(
        createOrder(member1, OrderStatus.SHIPPED, option1.getPrice() + option2.getPrice()));
    OrderItem orderItem1 = createOrderItem(order, option1, 1);
    OrderItem orderItem2 = createOrderItem(order, option2, 2);
    orderItemRepository.saveAll(List.of(orderItem1, orderItem2));

    // when
    ApiResponse<String> result = orderService.updateStatus(member1.getId(),
        order.getId(), OrderStatusRequest.REQUESTED_RETURN);

    // then
    assertThat(orderRepository.findById(order.getId()).get().getStatus()).isEqualTo(OrderStatus.REQUESTED_RETURN);
    assertThat(productOptionRepository.findAll())
        .extracting("name", "count")
        .containsExactlyInAnyOrder(
            tuple("옵션1-1", 5),
            tuple("옵션1-2", 5),
            tuple("옵션1-3", 5)
        );
    assertThat(result)
        .extracting("code", "status", "message", "data")
        .contains(200, HttpStatus.OK, "OK", "상태가 업데이트 되었습니다.");
  }

  @DisplayName("주문 정보가 없는 주문은 상태 업데이트가 불가능하다.")
  @Test
  void updateStatusWithOrderNotFound(){
    // given
    Product product1 = productRepository.save(createProduct("상품1"));

    Member member1 = memberRepository.save(createMember("email1@email.com"));

    ProductOption option1 = productOptionRepository.save(createProductOption("옵션1-1", product1));
    ProductOption option2 = productOptionRepository.save(createProductOption("옵션1-2", product1));

    Order order = orderRepository.save(
        createOrder(member1, OrderStatus.CREATED, option1.getPrice() + option2.getPrice()));
    OrderItem orderItem1 = createOrderItem(order, option1, 1);
    OrderItem orderItem2 = createOrderItem(order, option2, 2);
    orderItemRepository.saveAll(List.of(orderItem1, orderItem2));

    // when
    // then
    assertThatThrownBy(() ->orderService.updateStatus(member1.getId(),
        order.getId() - 1, OrderStatusRequest.CANCEL)).isInstanceOf(CustomException.class)
        .hasMessage("주문 개체를 찾지 못했습니다.");
  }

  @DisplayName("본인의 주문 정보가 아니면 상태 업데이트가 불가능하다.")
  @Test
  void updateStatusWithUnauthorizedAccess(){
    // given
    Product product1 = productRepository.save(createProduct("상품1"));

    Member member1 = memberRepository.save(createMember("email1@email.com"));
    Member member2 = memberRepository.save(createMember("email2@email.com"));

    ProductOption option1 = productOptionRepository.save(createProductOption("옵션1-1", product1));
    ProductOption option2 = productOptionRepository.save(createProductOption("옵션1-2", product1));

    Order order = orderRepository.save(
        createOrder(member1, OrderStatus.CREATED, option1.getPrice() + option2.getPrice()));
    OrderItem orderItem1 = createOrderItem(order, option1, 1);
    OrderItem orderItem2 = createOrderItem(order, option2, 2);
    orderItemRepository.saveAll(List.of(orderItem1, orderItem2));

    // when
    // then
    assertThatThrownBy(() ->orderService.updateStatus(member2.getId(),
        order.getId(), OrderStatusRequest.CANCEL)).isInstanceOf(CustomException.class)
        .hasMessage("해당 정보를 조회할 수 있는 권한이 없습니다");
  }

  @DisplayName("주문 취소는 배송 출발 전이 아니면 불가능하다.")
  @CsvSource({"SHIPPING","SHIPPED","COMPLETED","CANCELLED","REQUESTED_RETURN","RETURNED"})
  @ParameterizedTest
  void updateStatusCancelWithOrderCancelInvalidState(OrderStatus status){
    // given
    Product product1 = productRepository.save(createProduct("상품1"));

    Member member1 = memberRepository.save(createMember("email1@email.com"));

    ProductOption option1 = productOptionRepository.save(createProductOption("옵션1-1", product1));
    ProductOption option2 = productOptionRepository.save(createProductOption("옵션1-2", product1));

    Order order = orderRepository.save(
        createOrder(member1, status, option1.getPrice() + option2.getPrice()));
    OrderItem orderItem1 = createOrderItem(order, option1, 1);
    OrderItem orderItem2 = createOrderItem(order, option2, 2);
    orderItemRepository.saveAll(List.of(orderItem1, orderItem2));

    // when
    // then
    assertThatThrownBy(() ->orderService.updateStatus(member1.getId(),
        order.getId(), OrderStatusRequest.CANCEL)).isInstanceOf(CustomException.class)
        .hasMessage("취소가 가능한 상태가 아닙니다.");
  }

  @DisplayName("반품 신청은 배송완료 상태가 아니면 불가능하다.")
  @CsvSource({"CREATED", "SHIPPING","COMPLETED","CANCELLED","REQUESTED_RETURN","RETURNED"})
  @ParameterizedTest
  void updateStatusCancelWithOrderReturnInvalidState(OrderStatus status){
    // given
    Product product1 = productRepository.save(createProduct("상품1"));

    Member member1 = memberRepository.save(createMember("email1@email.com"));

    ProductOption option1 = productOptionRepository.save(createProductOption("옵션1-1", product1));
    ProductOption option2 = productOptionRepository.save(createProductOption("옵션1-2", product1));

    Order order = orderRepository.save(
        createOrder(member1, status, option1.getPrice() + option2.getPrice()));
    OrderItem orderItem1 = createOrderItem(order, option1, 1);
    OrderItem orderItem2 = createOrderItem(order, option2, 2);
    orderItemRepository.saveAll(List.of(orderItem1, orderItem2));

    // when
    // then
    assertThatThrownBy(() ->orderService.updateStatus(member1.getId(),
        order.getId(), OrderStatusRequest.REQUESTED_RETURN)).isInstanceOf(CustomException.class)
        .hasMessage("반품이 가능한 상태가 아닙니다.");
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

  private WishlistItem createWishlistItem(
      Wishlist wishlist, ProductOption productOption, int quantity) {
    return WishlistItem.builder()
        .wishlist(wishlist)
        .option(productOption)
        .quantity(quantity)
        .build();
  }



  private Wishlist createWishlist(Member member, Product product){
    return Wishlist.builder()
        .member(member)
        .product(product)
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