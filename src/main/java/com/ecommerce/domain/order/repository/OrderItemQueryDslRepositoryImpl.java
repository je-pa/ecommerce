package com.ecommerce.domain.order.repository;

import static com.ecommerce.domain.order.entity.QOrderItem.orderItem;
import static com.ecommerce.domain.product.entity.QProduct.product;
import static com.ecommerce.domain.product.entity.QProductOption.productOption;

import com.ecommerce.domain.order.entity.OrderItem;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderItemQueryDslRepositoryImpl implements OrderItemQueryDslRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<OrderItem> findAllByOrder(Long orderId) {
    return queryFactory
        .selectFrom(orderItem)
        .innerJoin(orderItem.option, productOption)
        .innerJoin(productOption.product, product)
        .where(orderItem.order.id.eq(orderId))
        .fetch();
  }
}
