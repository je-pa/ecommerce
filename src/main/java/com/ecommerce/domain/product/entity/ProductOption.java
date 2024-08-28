package com.ecommerce.domain.product.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import com.ecommerce.domain.BaseEntity;
import com.ecommerce.domain.product.type.OptionType;
import com.ecommerce.global.exception.CustomException;
import com.ecommerce.global.exception.ExceptionCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_options")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOption extends BaseEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "product_option_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Column(name = "name", nullable = false, length = 30)
  private String name;

  @Column(name = "count", nullable = false)
  private int count;

  @Column(name = "price", nullable = false)
  private int price;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private OptionType optionType;

  @Builder
  public ProductOption(int count, String name, OptionType optionType, int price, Product product) {
    this.count = count;
    this.name = name;
    this.optionType = optionType;
    this.price = price;
    this.product = product;
  }

  public void addQuantity(int quantity){
    if(this.count + quantity < 0){
      throw CustomException.from(ExceptionCode.OPTION_QUANTITY_NEGATIVE_VALUE_NOT_ALLOWED);
    }
    this.count += quantity;
  }
}
