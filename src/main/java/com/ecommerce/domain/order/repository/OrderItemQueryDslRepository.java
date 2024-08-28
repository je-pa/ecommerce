/**
 * @Date : 2024. 08. 28.
 * @author : jieun(je-pa)
 */
package com.ecommerce.domain.order.repository;

import com.ecommerce.domain.order.entity.OrderItem;
import java.util.List;

public interface OrderItemQueryDslRepository {
  List<OrderItem> findAllByOrder(Long orderId);
}
