package com.ecommerce.api.controller.wishlist.dto.request;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Action {
  INCREASE("증가"),
  DECREASE("감소");

  private final String value;
}
