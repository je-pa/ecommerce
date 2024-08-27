package com.ecommerce.api.service.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

import com.ecommerce.IntegrationTestSupport;
import com.ecommerce.api.controller.ApiResponse;
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