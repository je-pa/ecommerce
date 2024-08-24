/**
 * @Date : 2024. 08. 24.
 * @author : jieun(je-pa)
 */
package com.ecommerce.api.controller.product.dto.response;

import com.ecommerce.domain.product.type.ProductCategory;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;

public record ProductListItemResponse(
    Long id,
    String name,
    StoreInfo store,
    int quantity,
    ProductCategory category,
    String thumbnailImgUrl,
    int basePrice,
    LocalDateTime createdDateTime
) {

  @QueryProjection
  public ProductListItemResponse {

  }

  public record StoreInfo(
      Long storeId,
      String name
  ) {

    @QueryProjection
    public StoreInfo {

    }
  }
}
