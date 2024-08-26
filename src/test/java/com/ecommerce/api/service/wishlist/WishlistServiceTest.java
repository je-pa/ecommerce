package com.ecommerce.api.service.wishlist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

import com.ecommerce.IntegrationTestSupport;
import com.ecommerce.api.controller.ApiResponse;
import com.ecommerce.api.controller.wishlist.dto.request.Action;
import com.ecommerce.api.controller.wishlist.dto.request.CreateWishlistRequest;
import com.ecommerce.api.controller.wishlist.dto.response.WishlistWithItemsResponse;
import com.ecommerce.api.service.wishlist.dto.request.UpdateWishlistItemQuantityRequestWithId;
import com.ecommerce.domain.member.entity.Member;
import com.ecommerce.domain.member.repository.MemberRepository;
import com.ecommerce.domain.member.type.Role;
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
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class WishlistServiceTest extends IntegrationTestSupport {

  @Autowired
  private WishlistService wishlistService;

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

  @AfterEach
  void tearDown() {
    wishlistItemRepository.deleteAllInBatch();
    wishlistRepository.deleteAllInBatch();
    productOptionRepository.deleteAllInBatch();
    productRepository.deleteAllInBatch();
    storeRepository.deleteAllInBatch();
    memberRepository.deleteAllInBatch();
  }

  @DisplayName("상품 옵션을 선택해서 위시리스트에 등록한다.")
  @Test
  void modify(){
    // given
    Product product1 = productRepository.save(createProduct("상품1"));
    Product product2 = productRepository.save(createProduct("상품2"));
    Product product3 = productRepository.save(createProduct("상품3"));

    Member member1 = memberRepository.save(createMember("email1@email.com"));
    Member member2 = memberRepository.save(createMember("email2@email.com"));

    Wishlist wishlist1 = wishlistRepository.save(createWishlist(member1, product1));
    Wishlist wishlist2 = wishlistRepository.save(createWishlist(member1, product2));
    Wishlist wishlist3 = wishlistRepository.save(createWishlist(member2, product3));

    ProductOption option1 = productOptionRepository.save(createProductOption("옵션1-1", product1));
    ProductOption option2 = productOptionRepository.save(createProductOption("옵션1-2", product1));
    ProductOption option3 = productOptionRepository.save(createProductOption("옵션1-3", product1));
    ProductOption option4 = productOptionRepository.save(createProductOption("옵션2-1", product2));
    ProductOption option5 = productOptionRepository.save(createProductOption("옵션3-1", product3));

    List<WishlistItem> items = new ArrayList<>();
    items.add(createWishlistItem(wishlist1, option1, 1)); // member1, product1, option1
    items.add(createWishlistItem(wishlist1, option2, 2)); // member1, product1, option2
    items.add(createWishlistItem(wishlist1, option3, 3)); // member1, product1, option3
    items.add(createWishlistItem(wishlist2, option4, 4)); // member1, product2, option4
    items.add(createWishlistItem(wishlist3, option5, 5)); // member2, product3, option5
    wishlistItemRepository.saveAll(items);

    // when
    ApiResponse<String> result = wishlistService.modify(member1.getId(), List.of(
        new CreateWishlistRequest(option1.getId(), 3),
        new CreateWishlistRequest(option2.getId(), 3),
        new CreateWishlistRequest(option3.getId(), 3)
    ));

    // then
    assertThat(result)
        .extracting("code", "status", "message", "data")
        .contains(200, HttpStatus.OK, "OK", "Wishlist updated successfully");
  }

  @DisplayName("위시리스트에 등록 시, 옵션 리스트(CreateWishlistRequest)는 필수 항목이고, 항목이 1개 이상이어야 한다.")
  @Test
  void modifyWithRequestsEmpty(){
    // given
    Product product1 = productRepository.save(createProduct("상품1"));
    Product product2 = productRepository.save(createProduct("상품2"));
    Product product3 = productRepository.save(createProduct("상품3"));

    Member member1 = memberRepository.save(createMember("email1@email.com"));
    Member member2 = memberRepository.save(createMember("email2@email.com"));

    Wishlist wishlist1 = wishlistRepository.save(createWishlist(member1, product1));
    Wishlist wishlist2 = wishlistRepository.save(createWishlist(member1, product2));
    Wishlist wishlist3 = wishlistRepository.save(createWishlist(member2, product3));

    ProductOption option1 = productOptionRepository.save(createProductOption("옵션1-1", product1));
    ProductOption option2 = productOptionRepository.save(createProductOption("옵션1-2", product1));
    ProductOption option3 = productOptionRepository.save(createProductOption("옵션1-3", product1));
    ProductOption option4 = productOptionRepository.save(createProductOption("옵션2-1", product2));
    ProductOption option5 = productOptionRepository.save(createProductOption("옵션3-1", product3));

    List<WishlistItem> items = new ArrayList<>();
    items.add(createWishlistItem(wishlist1, option1, 1)); // member1, product1, option1
    items.add(createWishlistItem(wishlist1, option2, 2)); // member1, product1, option2
    items.add(createWishlistItem(wishlist1, option3, 3)); // member1, product1, option3
    items.add(createWishlistItem(wishlist2, option4, 4)); // member1, product2, option4
    items.add(createWishlistItem(wishlist3, option5, 5)); // member2, product3, option5
    wishlistItemRepository.saveAll(items);

    // when
    // then
    assertThatThrownBy(() -> wishlistService.modify(member1.getId(), List.of())).isInstanceOf(CustomException.class)
        .hasMessage("필수 요청 파라미터가 비었습니다.");
  }

  @DisplayName("위시리스트에 등록 시, 옵션 리스트(CreateWishlistRequest)는 중복된 상품 옵션은 불가능하다.")
  @Test
  void modifyWithProductOptionDuplicate(){
    // given
    Product product1 = productRepository.save(createProduct("상품1"));
    Product product2 = productRepository.save(createProduct("상품2"));
    Product product3 = productRepository.save(createProduct("상품3"));

    Member member1 = memberRepository.save(createMember("email1@email.com"));
    Member member2 = memberRepository.save(createMember("email2@email.com"));

    Wishlist wishlist1 = wishlistRepository.save(createWishlist(member1, product1));
    Wishlist wishlist2 = wishlistRepository.save(createWishlist(member1, product2));
    Wishlist wishlist3 = wishlistRepository.save(createWishlist(member2, product3));

    ProductOption option1 = productOptionRepository.save(createProductOption("옵션1-1", product1));
    ProductOption option2 = productOptionRepository.save(createProductOption("옵션1-2", product1));
    ProductOption option3 = productOptionRepository.save(createProductOption("옵션1-3", product1));
    ProductOption option4 = productOptionRepository.save(createProductOption("옵션2-1", product2));
    ProductOption option5 = productOptionRepository.save(createProductOption("옵션3-1", product3));

    List<WishlistItem> items = new ArrayList<>();
    items.add(createWishlistItem(wishlist1, option1, 1)); // member1, product1, option1
    items.add(createWishlistItem(wishlist1, option2, 2)); // member1, product1, option2
    items.add(createWishlistItem(wishlist1, option3, 3)); // member1, product1, option3
    items.add(createWishlistItem(wishlist2, option4, 4)); // member1, product2, option4
    items.add(createWishlistItem(wishlist3, option5, 5)); // member2, product3, option5
    wishlistItemRepository.saveAll(items);

    // when
    // then
    assertThatThrownBy(() -> wishlistService.modify(member1.getId(), List.of(
        new CreateWishlistRequest(option1.getId(), 3),
        new CreateWishlistRequest(option2.getId(), 3),
        new CreateWishlistRequest(option2.getId(), 3)
    ))).isInstanceOf(CustomException.class)
        .hasMessage("중복된 상품 옵션이 있습니다.");
  }

  @DisplayName("위시리스트에 등록 시, 없는 상품 옵션 id로 요청할 수 없다.")
  @Test
  void modifyWithProductOptionsNotFound(){
    // given
    Product product1 = productRepository.save(createProduct("상품1"));
    Product product2 = productRepository.save(createProduct("상품2"));
    Product product3 = productRepository.save(createProduct("상품3"));

    Member member1 = memberRepository.save(createMember("email1@email.com"));
    Member member2 = memberRepository.save(createMember("email2@email.com"));

    Wishlist wishlist1 = wishlistRepository.save(createWishlist(member1, product1));
    Wishlist wishlist2 = wishlistRepository.save(createWishlist(member1, product2));
    Wishlist wishlist3 = wishlistRepository.save(createWishlist(member2, product3));

    ProductOption option1 = productOptionRepository.save(createProductOption("옵션1-1", product1));
    ProductOption option2 = productOptionRepository.save(createProductOption("옵션1-2", product1));
    ProductOption option3 = productOptionRepository.save(createProductOption("옵션1-3", product1));
    ProductOption option4 = productOptionRepository.save(createProductOption("옵션2-1", product2));
    ProductOption option5 = productOptionRepository.save(createProductOption("옵션3-1", product3));

    List<WishlistItem> items = new ArrayList<>();
    items.add(createWishlistItem(wishlist1, option1, 1)); // member1, product1, option1
    items.add(createWishlistItem(wishlist1, option2, 2)); // member1, product1, option2
    items.add(createWishlistItem(wishlist1, option3, 3)); // member1, product1, option3
    items.add(createWishlistItem(wishlist2, option4, 4)); // member1, product2, option4
    items.add(createWishlistItem(wishlist3, option5, 5)); // member2, product3, option5
    wishlistItemRepository.saveAll(items);

    // when
    // then
    assertThatThrownBy(() -> wishlistService.modify(member1.getId(), List.of(
        new CreateWishlistRequest(0L, 3),
        new CreateWishlistRequest(option2.getId(), 3)
    ))).isInstanceOf(CustomException.class)
        .hasMessage("없는 상품 옵션이 있습니다.");
  }

  @DisplayName("위시리스트에 등록 시, 모든 옵션의 상품이 같아야 한다.")
  @Test
  void modifyWithWishlistOptionsSameProductOnly(){
    // given
    Product product1 = productRepository.save(createProduct("상품1"));
    Product product2 = productRepository.save(createProduct("상품2"));
    Product product3 = productRepository.save(createProduct("상품3"));

    Member member1 = memberRepository.save(createMember("email1@email.com"));
    Member member2 = memberRepository.save(createMember("email2@email.com"));

    Wishlist wishlist1 = wishlistRepository.save(createWishlist(member1, product1));
    Wishlist wishlist2 = wishlistRepository.save(createWishlist(member1, product2));
    Wishlist wishlist3 = wishlistRepository.save(createWishlist(member2, product3));

    ProductOption option1 = productOptionRepository.save(createProductOption("옵션1-1", product1));
    ProductOption option2 = productOptionRepository.save(createProductOption("옵션1-2", product1));
    ProductOption option3 = productOptionRepository.save(createProductOption("옵션1-3", product1));
    ProductOption option4 = productOptionRepository.save(createProductOption("옵션2-1", product2));
    ProductOption option5 = productOptionRepository.save(createProductOption("옵션3-1", product3));

    List<WishlistItem> items = new ArrayList<>();
    items.add(createWishlistItem(wishlist1, option1, 1)); // member1, product1, option1
    items.add(createWishlistItem(wishlist1, option2, 2)); // member1, product1, option2
    items.add(createWishlistItem(wishlist1, option3, 3)); // member1, product1, option3
    items.add(createWishlistItem(wishlist2, option4, 4)); // member1, product2, option4
    items.add(createWishlistItem(wishlist3, option5, 5)); // member2, product3, option5
    wishlistItemRepository.saveAll(items);

    // when
    // then
    assertThatThrownBy(() -> wishlistService.modify(member1.getId(), List.of(
        new CreateWishlistRequest(option1.getId(), 3),
        new CreateWishlistRequest(option2.getId(), 3),
        new CreateWishlistRequest(option5.getId(), 3)
    ))).isInstanceOf(CustomException.class)
        .hasMessage("모든 옵션의 상품이 같아야 합니다.");
  }

  @DisplayName("위시리스트에 등록 시, 멤버 정보가 없는 멤버id로는 진행할 수 없다.")
  @Test
  void modifyWithUserNotFound(){
    // given
    Product product1 = productRepository.save(createProduct("상품1"));
    Product product2 = productRepository.save(createProduct("상품2"));
    Product product3 = productRepository.save(createProduct("상품3"));

    Member member1 = memberRepository.save(createMember("email1@email.com"));
    Member member2 = memberRepository.save(createMember("email2@email.com"));

    Wishlist wishlist1 = wishlistRepository.save(createWishlist(member1, product1));
    Wishlist wishlist2 = wishlistRepository.save(createWishlist(member1, product2));
    Wishlist wishlist3 = wishlistRepository.save(createWishlist(member2, product3));

    ProductOption option1 = productOptionRepository.save(createProductOption("옵션1-1", product1));
    ProductOption option2 = productOptionRepository.save(createProductOption("옵션1-2", product1));
    ProductOption option3 = productOptionRepository.save(createProductOption("옵션1-3", product1));
    ProductOption option4 = productOptionRepository.save(createProductOption("옵션2-1", product2));
    ProductOption option5 = productOptionRepository.save(createProductOption("옵션3-1", product3));

    List<WishlistItem> items = new ArrayList<>();
    items.add(createWishlistItem(wishlist1, option1, 1)); // member1, product1, option1
    items.add(createWishlistItem(wishlist1, option2, 2)); // member1, product1, option2
    items.add(createWishlistItem(wishlist1, option3, 3)); // member1, product1, option3
    items.add(createWishlistItem(wishlist2, option4, 4)); // member1, product2, option4
    items.add(createWishlistItem(wishlist3, option5, 5)); // member2, product3, option5
    wishlistItemRepository.saveAll(items);

    // when
    // then
    assertThatThrownBy(() -> wishlistService.modify(0L, List.of(
        new CreateWishlistRequest(option1.getId(), 3),
        new CreateWishlistRequest(option2.getId(), 3),
        new CreateWishlistRequest(option3.getId(), 3)
    ))).isInstanceOf(CustomException.class)
        .hasMessage("유저 개체를 찾지 못했습니다.");
  }

  @DisplayName("위시리스트를 조회한다.")
  @Test
  void readList(){
    // given
    Product product1 = productRepository.save(createProduct("상품1"));
    Product product2 = productRepository.save(createProduct("상품2"));
    Product product3 = productRepository.save(createProduct("상품3"));

    Member member1 = memberRepository.save(createMember("email1@email.com"));
    Member member2 = memberRepository.save(createMember("email2@email.com"));

    Wishlist wishlist1 = wishlistRepository.save(createWishlist(member1, product1));
    Wishlist wishlist2 = wishlistRepository.save(createWishlist(member1, product2));
    Wishlist wishlist3 = wishlistRepository.save(createWishlist(member2, product3));

    ProductOption option1 = productOptionRepository.save(createProductOption("옵션1-1", product1));
    ProductOption option2 = productOptionRepository.save(createProductOption("옵션1-2", product1));
    ProductOption option3 = productOptionRepository.save(createProductOption("옵션1-3", product1));
    ProductOption option4 = productOptionRepository.save(createProductOption("옵션2-1", product2));
    ProductOption option5 = productOptionRepository.save(createProductOption("옵션3-1", product3));

    List<WishlistItem> items = new ArrayList<>();
    items.add(createWishlistItem(wishlist1, option1, 1)); // member1, product1, option1
    items.add(createWishlistItem(wishlist1, option2, 2)); // member1, product1, option2
    items.add(createWishlistItem(wishlist1, option3, 3)); // member1, product1, option3
    items.add(createWishlistItem(wishlist2, option4, 4)); // member1, product2, option4
    items.add(createWishlistItem(wishlist3, option5, 5)); // member2, product3, option5
    wishlistItemRepository.saveAll(items);

    // when
    List<WishlistWithItemsResponse> wishlistWithItemsResponses = wishlistService.readList(
        member1.getId(), member1.getId());

    // then
    assertThat(wishlistWithItemsResponses).hasSize(2)
        .extracting("productName", "storeName")
        .containsExactly(
            tuple("상품1", "storeName1"),
            tuple("상품2", "storeName1")
        );
    assertThat(wishlistWithItemsResponses.get(0).items()).hasSize(3)
        .extracting("optionName", "quantity")
        .containsExactly(
            tuple("옵션1-1", 1),
            tuple("옵션1-2", 2),
            tuple("옵션1-3", 3)
        );
    assertThat(wishlistWithItemsResponses.get(1).items()).hasSize(1)
        .extracting("optionName", "quantity")
        .containsExactly(
            tuple("옵션2-1", 4)
        );
  }

  @DisplayName("본인의 위시리스트만 조회할 수 있다.")
  @Test
  void readListWithUnauthorizedAccess(){
    // given
    // when
    // then
    assertThatThrownBy(() -> wishlistService.readList(
        1L, 2L)).isInstanceOf(CustomException.class)
        .hasMessage("해당 정보를 조회할 수 있는 권한이 없습니다");
  }

  @DisplayName("위시리스트 항목을 수정한다.")
  @Test
  void updateItemQuantity(){
    // given
    Product product1 = productRepository.save(createProduct("상품1"));
    Member member1 = memberRepository.save(createMember("email1@email.com"));
    Wishlist wishlist1 = wishlistRepository.save(createWishlist(member1, product1));
    ProductOption option1 = productOptionRepository.save(createProductOption("옵션1-1", product1));
    WishlistItem item = wishlistItemRepository.save(
        createWishlistItem(wishlist1, option1, 1));// member1, product1, option1
    // when
    ApiResponse<String> result = wishlistService.updateItemQuantity(
        UpdateWishlistItemQuantityRequestWithId.builder()
            .action(Action.DECREASE)
            .itemId(item.getId())
            .currentMemberId(member1.getId())
            .build()
    );

    // then
    assertThat(result)
        .extracting("code", "status", "message", "data")
        .contains(200, HttpStatus.OK, "OK", "Wishlist updated successfully");

  }

  @DisplayName("위시리스트 항목 수정 시, 없는 위시리스트 항목은 불가능하다.")
  @Test
  void updateItemQuantityWithWishlistItemNotFound(){
    // given
    // when
    // then
    assertThatThrownBy(() -> wishlistService.updateItemQuantity(
        UpdateWishlistItemQuantityRequestWithId.builder()
            .action(Action.DECREASE)
            .itemId(1L)
            .currentMemberId(0L)
            .build()
    )).isInstanceOf(CustomException.class)
        .hasMessage("위시리스트 항목 개체를 찾지 못했습니다.");
  }

  @DisplayName("위시리스트 항목 수정 시, 본인만 가능하다.")
  @Test
  void updateItemQuantityWithUnauthorizedAccess(){
    // given
    Product product1 = productRepository.save(createProduct("상품1"));
    Member member1 = memberRepository.save(createMember("email1@email.com"));
    Wishlist wishlist1 = wishlistRepository.save(createWishlist(member1, product1));
    ProductOption option1 = productOptionRepository.save(createProductOption("옵션1-1", product1));
    WishlistItem item = wishlistItemRepository.save(
        createWishlistItem(wishlist1, option1, 1));// member1, product1, option1
    // when
    // then
    assertThatThrownBy(() -> wishlistService.updateItemQuantity(
        UpdateWishlistItemQuantityRequestWithId.builder()
            .action(Action.DECREASE)
            .itemId(item.getId())
            .currentMemberId(0L)
            .build()
    )).isInstanceOf(CustomException.class)
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