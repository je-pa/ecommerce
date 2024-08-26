package com.ecommerce.domain.wishlist.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.ecommerce.IntegrationTestSupport;
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
import com.ecommerce.domain.wishlist.repository.dao.WishlistWithItemsDao;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class WishlistRepositoryTest extends IntegrationTestSupport {

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

  @DisplayName("memberId, productId로 위시리스트를 조회할 수 있다.")
  @Test
  void findByMemberIdAndProductId(){
    // given
    Product product1 = productRepository.save(createProduct("상품1"));
    Product product2 = productRepository.save(createProduct("상품2"));
    Product product3 = productRepository.save(createProduct("상품3"));
    Product product4 = productRepository.save(createProduct("상품4"));

    Member member1 = memberRepository.save(createMember("email1@email.com"));
    Member member2 = memberRepository.save(createMember("email2@email.com"));

    List<Wishlist> wishlists = new ArrayList<>();
    wishlists.add(createWishlist(member1, product1));
    wishlists.add(createWishlist(member1, product2));
    wishlists.add(createWishlist(member1, product3));
    wishlists.add(createWishlist(member2, product3));
    wishlists.add(createWishlist(member2, product4));
    wishlistRepository.saveAll(wishlists);

    // when
    Wishlist wishlist = wishlistRepository.findByMemberIdAndProductId(member1.getId(),
        product1.getId()).orElseGet(null);

    // then
    assertThat(wishlist)
        .extracting("member.email", "product.name")
        .contains("email1@email.com", "상품1");
  }

  @DisplayName("memberId로 위시리스트와 위시리스트 항목을 조회할 수 있다.")
  @Test
  void findWishlistWithItemsDaoListBy(){
    // given
    Product product1 = productRepository.save(createProduct("상품1"));
    Product product2 = productRepository.save(createProduct("상품2"));
    Product product3 = productRepository.save(createProduct("상품3"));

    Member member1 = memberRepository.save(createMember("email1@email.com"));
    Member member2 = memberRepository.save(createMember("email2@email.com"));

    Wishlist wishlist1 = wishlistRepository.save(createWishlist(member1, product1));
    Wishlist wishlist2 = wishlistRepository.save(createWishlist(member1, product2));
    Wishlist wishlist3 = wishlistRepository.save(createWishlist(member2, product3));
    Wishlist wishlist4 = wishlistRepository.save(createWishlist(member2, product1));

    ProductOption option1 = productOptionRepository.save(createProductOption("옵션1-1", product1));
    ProductOption option2 = productOptionRepository.save(createProductOption("옵션1-2", product1));
    ProductOption option3 = productOptionRepository.save(createProductOption("옵션1-3", product1));
    ProductOption option4 = productOptionRepository.save(createProductOption("옵션2-1", product2));
    ProductOption option5 = productOptionRepository.save(createProductOption("옵션3-1", product3));

    List<WishlistItem> items = new ArrayList<>();
    items.add(createWishlistItem(wishlist1, option1));
    items.add(createWishlistItem(wishlist1, option2));
    items.add(createWishlistItem(wishlist1, option3));
    items.add(createWishlistItem(wishlist2, option4));
    items.add(createWishlistItem(wishlist3, option5));
    items.add(createWishlistItem(wishlist4, option1));
    wishlistItemRepository.saveAll(items);

    // when
    List<WishlistWithItemsDao> wishlistWithItemsDaoListBy = wishlistRepository
        .findWishlistWithItemsDaoListBy(member1.getId());

    // then
    assertThat(wishlistWithItemsDaoListBy).hasSize(4)
        .extracting("productName", "item.optionName")
        .containsExactly(
            tuple("상품1" , "옵션1-1"),
            tuple("상품1" , "옵션1-2"),
            tuple("상품1" , "옵션1-3"),
            tuple("상품2" , "옵션2-1")
        );
  }
  private WishlistItem createWishlistItem(Wishlist wishlist, ProductOption productOption) {
    return WishlistItem.builder()
        .wishlist(wishlist)
        .option(productOption)
        .quantity(5)
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