package com.ecommerce.domain.order.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OrderStatus {
  CREATED("생성된"),
  SHIPPING("배송중"),
  SHIPPED("배송완료된"),
  COMPLETED("완료된"),
  CANCELLED("취소완료"),
  REQUESTED_RETURN("반품신청된"),
  RETURNED("반품완료");

  private final String name;

  public String getName() {
    return name;
  }
}