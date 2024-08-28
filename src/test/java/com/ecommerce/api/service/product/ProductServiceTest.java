/**
 * @Date : 2024. 08. 25.
 * @author : jieun(je-pa)
 */
package com.ecommerce.api.service.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.ecommerce.IntegrationTestSupport;
import com.ecommerce.api.controller.product.dto.request.ReadProductListRequest;
import com.ecommerce.api.controller.product.dto.request.ReadProductListRequest.ReadProductListSort;
import com.ecommerce.api.controller.product.dto.response.ProductDetailResponse;
import com.ecommerce.api.controller.product.dto.response.ProductListItemResponse;
import com.ecommerce.api.service.product.event.UpdateQuantityByProductOptionsEvent;
import com.ecommerce.api.service.product.event.UpdateQuantityByProductOptionsEvent.ProductOptionInfo;
import com.ecommerce.domain.product.entity.Product;
import com.ecommerce.domain.product.entity.ProductOption;
import com.ecommerce.domain.product.repository.ProductOptionRepository;
import com.ecommerce.domain.product.repository.ProductRepository;
import com.ecommerce.domain.product.type.OptionType;
import com.ecommerce.domain.product.type.ProductCategory;
import com.ecommerce.domain.store.entity.Store;
import com.ecommerce.domain.store.repository.StoreRepository;
import com.ecommerce.global.exception.CustomException;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;

class ProductServiceTest extends IntegrationTestSupport {
  @Autowired
  private ProductService productService;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private StoreRepository storeRepository;

  @Autowired
  private ProductOptionRepository productOptionRepository;

  @AfterEach
  void tearDown() {
    productOptionRepository.deleteAllInBatch();
    productRepository.deleteAllInBatch();
    storeRepository.deleteAllInBatch();
  }

  @DisplayName("상품 id 에 대한 상품의 상세정보를 조회한다.")
  @Test
  void get(){
    // given
    String storeName1 = "가게명 1";
    Store store1 = storeRepository.save(createStore(storeName1));

    String productName1 = "상품1";
    int product1Option1Quantity = 3;
    int product1Option2Quantity = 2;
    int productQuantity1 = product1Option1Quantity + product1Option2Quantity;
    ProductCategory product1Category = ProductCategory.TOP;
    Product product1 = productRepository.save(createProduct(productName1, productQuantity1, product1Category, store1, 1000));
    String product1Options1Name = "화이트";
    String product1Options2Name = "블랙";


    String productName2 = "상품2";
    int product2OptionQuantity1 = 3;
    int product2OptionQuantity2 = 2;
    int productQuantity2 = product2OptionQuantity1 + product2OptionQuantity2;
    ProductCategory product2Category = ProductCategory.ACCESSORY;
    Product product2 = productRepository.save(createProduct(productName2, productQuantity2, product2Category, store1, 1000));
    String product2Options1Name = "골드";
    String product2Options2Name = "실버";

    productOptionRepository.saveAll(
        List.of(
            createProductOption(product1Options1Name, product1Option1Quantity, product1),
            createProductOption(product1Options2Name, product1Option2Quantity, product1),
            createProductOption(product2Options1Name, product2OptionQuantity1, product2),
            createProductOption(product2Options2Name, product2OptionQuantity2, product2)
        )
    );

    // when
    ProductDetailResponse result = productService.get(product1.getId());

    // then
    assertThat(result.options()).hasSize(2)
        .extracting("name", "count")
        .containsExactlyInAnyOrder(
            tuple(product1Options1Name, product1Option1Quantity),
            tuple(product1Options2Name, product1Option2Quantity)
        );
    assertThat(result)
        .extracting("name", "quantity", "category", "store.name")
        .contains(productName1, productQuantity1, product1Category, storeName1);
  }

  @DisplayName("상품 리스트를 조회한다.")
  @Test
  void getProductSlice(){
    // given
    String storeName1 = "가게명 1";
    Store store1 = storeRepository.save(createStore(storeName1));
    String storeName2 = "가게명 2";
    Store store2 = storeRepository.save(createStore(storeName2));

    List<Product> list = new ArrayList<>();
    list.add(createProduct("상품1", 8, ProductCategory.TOP, store1, 1000));
    list.add(createProduct("상품2", 13, ProductCategory.TOP, store1, 2000));
    list.add(createProduct("상품3", 8, ProductCategory.TOP, store1, 3000));
    list.add(createProduct("상품4", 13, ProductCategory.ACCESSORY, store1, 4000));
    list.add(createProduct("상", 13, ProductCategory.ACCESSORY, store1, 5000));
    list.add(createProduct("상품5", 5, ProductCategory.ACCESSORY, store1, 6000));
    list.add(createProduct("상품6", 15, ProductCategory.ACCESSORY, store1, 7000));
    list.add(createProduct("상품7", 5, ProductCategory.ACCESSORY, store1, 8000));
    list.add(createProduct("상품8", 15, ProductCategory.ACCESSORY, store1, 9000));
    list.add(createProduct("상품9", 10, ProductCategory.TOP, store2, 10000));
    list.add(createProduct("상품10", 16, ProductCategory.TOP, store2, 11000));
    list.add(createProduct("상품11", 11, ProductCategory.ACCESSORY, store2, 12000));
    list.add(createProduct("상품12", 17, ProductCategory.ACCESSORY, store2, 13000));

    productRepository.saveAll(list);

    // when
    Slice<ProductListItemResponse> result = productService.getProductSlice(
        ReadProductListRequest.builder()
            .storeId(store1.getId())
            .searchKeyword("상품")
            .productCategory(ProductCategory.ACCESSORY)
            .pageSize(3)
            .listSort(ReadProductListSort.PRICE_DESC)
            .pageNumber(2)
            .build());

    // then
    assertThat(result).hasSize(2)
        .extracting("name", "store.name", "quantity", "category")
        .containsExactly(
            tuple("상품5", storeName1, 5, ProductCategory.ACCESSORY),
            tuple("상품4", storeName1, 13, ProductCategory.ACCESSORY)
        );

  }

  @DisplayName("상품 옵션 재고를 조정한다.")
  @Test
  void adjustQuantity(){
    // given
    Store store1 = storeRepository.save(createStore("storeName1"));

    int option1Quantity = 5;
    int option2Quantity = 7;
    int option3Quantity = 9;
    int totalCount = option1Quantity + option2Quantity + option3Quantity;
    Product product1 = productRepository.save(
        createProduct("상품1", store1, totalCount));

    ProductOption option1 = productOptionRepository.save(
        createProductOption("옵션1-1", option1Quantity, product1));
    ProductOption option2 = productOptionRepository.save(
        createProductOption("옵션1-2", option2Quantity, product1));
    ProductOption option3 = productOptionRepository.save(
        createProductOption("옵션1-3", option3Quantity, product1));

    // when
    productService.adjustQuantity(new UpdateQuantityByProductOptionsEvent(List.of(
        new Info(option1.getId(), 2),
        new Info(option2.getId(), 1)
    )));

    // then
    assertThat(productOptionRepository.findAll())
        .extracting("name", "count")
        .containsExactlyInAnyOrder(
            AssertionsForClassTypes.tuple("옵션1-1", 7),
            AssertionsForClassTypes.tuple("옵션1-2", 8),
            AssertionsForClassTypes.tuple("옵션1-3", option3Quantity)
        );
    assertThat(productRepository.findById(product1.getId()).get())
        .extracting("name", "stockQuantity")
        .contains("상품1", option1Quantity + 2 + option2Quantity + 1 + option3.getCount());

  }

  @DisplayName("상품 옵션id가 중복이면 안된다.")
  @Test
  void adjustQuantityWithProductOptionDuplicate(){
    // given
    Store store1 = storeRepository.save(createStore("storeName1"));

    int option1Quantity = 5;
    int option2Quantity = 7;
    int option3Quantity = 9;
    int totalCount = option1Quantity + option2Quantity + option3Quantity;
    Product product1 = productRepository.save(
        createProduct("상품1", store1, totalCount));

    ProductOption option1 = productOptionRepository.save(
        createProductOption("옵션1-1", option1Quantity, product1));
    ProductOption option2 = productOptionRepository.save(
        createProductOption("옵션1-2", option2Quantity, product1));
    ProductOption option3 = productOptionRepository.save(
        createProductOption("옵션1-3", option3Quantity, product1));

    // when
    // then
    assertThatThrownBy(() ->productService.adjustQuantity(new UpdateQuantityByProductOptionsEvent(List.of(
        new Info(option1.getId(), 2),
        new Info(option1.getId(), 1)
    )))).isInstanceOf(CustomException.class)
        .hasMessage("중복된 상품 옵션이 있습니다.");

  }

  @DisplayName("재고가 모자르면 안된다.")
  @Test
  void adjustQuantityWithInsufficientStock(){
    // given
    Store store1 = storeRepository.save(createStore("storeName1"));

    int option1Quantity = 0;
    int option2Quantity = 7;
    int option3Quantity = 9;
    int totalCount = option1Quantity + option2Quantity + option3Quantity;
    Product product1 = productRepository.save(
        createProduct("상품1", store1, totalCount));

    ProductOption option1 = productOptionRepository.save(
        createProductOption("옵션1-1", option1Quantity, product1));
    ProductOption option2 = productOptionRepository.save(
        createProductOption("옵션1-2", option2Quantity, product1));
    ProductOption option3 = productOptionRepository.save(
        createProductOption("옵션1-3", option3Quantity, product1));

    // when
    // then
    assertThatThrownBy(() ->productService.adjustQuantity(new UpdateQuantityByProductOptionsEvent(List.of(
        new Info(option1.getId(), -2),
        new Info(option2.getId(), -1)
    )))).isInstanceOf(CustomException.class)
        .hasMessage("재고가 충분하지 않습니다.");

  }

  record Info(Long productOptionId, int quantity)implements ProductOptionInfo {

    @Override
    public Long getProductOptionId() {
      return this.productOptionId;
    }

    @Override
    public int getQuantity() {
      return this.quantity;
    }
  }

  private ProductOption createProductOption(String name, int quantity, Product product) {
    return ProductOption.builder()
        .product(product)
        .count(quantity)
        .name(name)
        .optionType(OptionType.MANDATORY)
        .price(10000)
        .build();
  }

  private Product createProduct(String name, Store store, int quantity){
    return createProduct(name, quantity, ProductCategory.TOP, store, 10000);
  }

  private Product createProduct(String name, int quantity, ProductCategory category, Store store, int price) {
    return Product.builder()
        .name(name)
        .stockQuantity(quantity)
        .store(store)
        .category(category)
        .info("상품정보")
        .thumbnailImgUrl("url")
        .price(price)
        .build();
  }

  private Store createStore(String name){
    return Store.builder()
        .name(name)
        .tellNumber("encrypted")
        .info("정보")
        .build();
  }
}