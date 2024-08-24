/**
 * @Date : 2024. 08. 25.
 * @author : jieun(je-pa)
 */
package com.ecommerce.api.controller.product.dto.response;

import com.ecommerce.domain.product.repository.dao.ProductDetailDao;
import com.ecommerce.domain.product.type.OptionType;
import com.ecommerce.domain.product.type.ProductCategory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;

@Builder
public record ProductDetailResponse(
    Long id,
    String name,
    StoreInfo store,
    int quantity,
    ProductCategory category,
    String thumbnailImgUrl,
    int basePrice,
    LocalDateTime createdDateTime,
    List<ProductOptionInfo> options
) {

  public static ProductDetailResponse from(List<ProductDetailDao> daos){
    List<ProductOptionInfo> options = new ArrayList<>();
    daos.forEach(dao -> options.add(ProductOptionInfo.from(dao.option())));
    ProductDetailDao dao = daos.get(0);
    return ProductDetailResponse.builder()
        .id(dao.id())
        .name(dao.name())
        .store(StoreInfo.from(dao.store()))
        .quantity(dao.quantity())
        .category(dao.category())
        .thumbnailImgUrl(dao.thumbnailImgUrl())
        .basePrice(dao.basePrice())
        .createdDateTime(dao.createdDateTime())
        .options(options)
        .build();
  }

  public record StoreInfo(
      Long storeId,
      String name
  ) {
    public static StoreInfo from(ProductDetailDao.StoreInfo daoStore){
      return new StoreInfo(daoStore.storeId(), daoStore.name());
    }
  }

  public record ProductOptionInfo(
      Long optionId,
      String name,
      int count,
      int price,
      OptionType optionType
  ){
    public static ProductOptionInfo from(ProductDetailDao.ProductOptionInfo option){
      return new ProductOptionInfo(
          option.optionId(),
          option.name(),
          option.count(),
          option.price(),
          option.optionType()
      );
    }
  }
}
