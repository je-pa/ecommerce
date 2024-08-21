package com.ecommerce.domain.product.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ProductCategory {

  TOP("상의"),
  BOTTOM("하의"),
  ACCESSORY("악세사리");

  private final String name;

  public String getName() {
    return name;
  }
}
