package com.ecommerce.domain.order.repository;

import com.ecommerce.domain.order.entity.OrderItem;
import java.util.List;

public interface OrderItemQueryDslRepository {
  List<OrderItem> findAllByOrder(Long orderId);
}
