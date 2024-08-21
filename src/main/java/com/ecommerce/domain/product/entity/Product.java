package com.ecommerce.domain.product.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import com.ecommerce.domain.BaseEntity;
import com.ecommerce.domain.product.type.ProductCategory;
import com.ecommerce.domain.store.entity.Store;
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
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "product_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "store_id", nullable = false)
  private Store store;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "info", nullable = true)
  private String info;

  @Column(name = "stock_quantity", nullable = false)
  private int stockQuantity;

  @Enumerated(EnumType.STRING)
  @Column(name = "category", nullable = false)
  private ProductCategory category;

  @Column(name = "thumbnail_img_url")
  private String thumbnailImgUrl;

  @Builder
  public Product(ProductCategory category, String info, String name, int stockQuantity, Store store,
      String thumbnailImgUrl) {
    this.category = category;
    this.info = info;
    this.name = name;
    this.stockQuantity = stockQuantity;
    this.store = store;
    this.thumbnailImgUrl = thumbnailImgUrl;
  }
}
