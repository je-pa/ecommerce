/**
 * @Date : 2024. 08. 27.
 * @author : jieun(je-pa)
 */
package com.ecommerce.api.service.order;

import com.ecommerce.api.controller.ApiResponse;
import com.ecommerce.api.controller.order.dto.request.OrderStatusRequest;
import com.ecommerce.api.service.order.dto.request.CreateOrderByWishlistItemsDto;

public interface OrderService {

  /**
   * 주문을 생성합니다.
   * @param dto 로그인 유저 아이디와 위시리스트 항목 아이디의 리스트를 담은 dto
   * @return 실행 결과 응답
   */
  ApiResponse<String> create(CreateOrderByWishlistItemsDto dto);

  /**
   * 주문의 상태를 수정합니다.
   * @param currentMemberId 로그인 유저 아이디
   * @param orderId 주문 아이디
   * @param status 주문에 대해 하려는 행위(CANCEL, REQUESTED_RETURN)
   * @return 실행 결과 응답
   */
  ApiResponse<String> updateStatus(Long currentMemberId, Long orderId, OrderStatusRequest status);
}
