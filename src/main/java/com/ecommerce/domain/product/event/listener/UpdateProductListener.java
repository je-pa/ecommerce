/**
 * @Date : 2024. 08. 28.
 * @author : jieun(je-pa)
 */
package com.ecommerce.domain.product.event.listener;

import com.ecommerce.api.service.product.ProductService;
import com.ecommerce.domain.product.event.UpdateQuantityByProductOptionsEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UpdateProductListener {
  private final ProductService productService;

  @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
  public void handleUpdateQuantity(final UpdateQuantityByProductOptionsEvent event) {
    productService.adjustQuantity(event);
  }

}
