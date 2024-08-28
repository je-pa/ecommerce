/**
 * @Date : 2024. 08. 28.
 * @author : jieun(je-pa)
 */
package com.ecommerce.domain.product.event;

import java.util.Collection;

public record UpdateQuantityByProductOptionsEvent(
    Collection<? extends ProductOptionInfo> productOptionInfos
) {

  public interface ProductOptionInfo {
    Long getProductOptionId();
    int getQuantity();
  }

}
