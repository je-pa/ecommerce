package com.ecommerce.domain.order.entity;

import com.ecommerce.domain.product.entity.ProductOption;
import com.ecommerce.domain.product.event.UpdateQuantityByProductOptionsEvent.ProductOptionInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem implements ProductOptionInfo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "order_item_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "option_id", nullable = false)
  private ProductOption option;

  @Column(name = "quantity", nullable = false)
  private int quantity;

  @Column(name = "price", nullable = false)
  private int price;

  @Builder
  public OrderItem(ProductOption option, Order order, int quantity, int price) {
    this.option = option;
    this.order = order;
    this.quantity = quantity;
    this.price = price;
  }

  @Override
  public Long getProductOptionId() {
    return this.option.getId();
  }
}
