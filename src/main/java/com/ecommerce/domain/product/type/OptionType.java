package com.ecommerce.domain.product.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OptionType {
  MANDATORY("필수"),
  OPTIONAL("선택");

  private final String name;
}
