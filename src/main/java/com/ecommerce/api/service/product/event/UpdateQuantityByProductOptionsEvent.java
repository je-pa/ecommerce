package com.ecommerce.api.service.product.event;

import java.util.Collection;

public record UpdateQuantityByProductOptionsEvent(
    Collection<? extends ProductOptionInfo> productOptionInfos
) {

  public interface ProductOptionInfo {
    Long getProductOptionId();
    int getQuantity();
  }

}
