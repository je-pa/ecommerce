/**
 * @Date : 2024. 08. 27.
 * @author : jieun(je-pa)
 */
package com.ecommerce.domain.order.repository;

import com.ecommerce.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>, OrderItemQueryDslRepository {

}
