/**
 * @Date : 2024. 08. 27.
 * @author : jieun(je-pa)
 */
package com.ecommerce.api.controller.order;

import com.ecommerce.api.controller.ApiResponse;
import com.ecommerce.api.controller.order.dto.request.CreateOrderByWishlistItemsRequest;
import com.ecommerce.api.controller.order.dto.request.OrderStatusRequest;
import com.ecommerce.api.service.order.OrderService;
import com.ecommerce.api.service.order.dto.request.CreateOrderByWishlistItemsDto;
import com.ecommerce.global.security.annotation.CurrentMemberId;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
  private final OrderService orderService;

  /**
   * 주문을 생성합니다.
   * @param currentMemberId 로그인 유저 아이디
   * @param requests 위시리스트 항목 아이디의 리스트
   * @return 실행 결과 응답
   */
  @PostMapping
  public ResponseEntity<ApiResponse<String>> add(
      @CurrentMemberId Long currentMemberId,
      @Valid @RequestBody List<CreateOrderByWishlistItemsRequest> requests
  ){
    return ResponseEntity.ok(orderService.create(
      CreateOrderByWishlistItemsDto.of(requests, currentMemberId)
    ));
  }

  /**
   * 주문의 상태를 수정합니다.
   * @param currentMemberId 로그인 유저 아이디
   * @param orderId 주문 아이디
   * @param status 주문에 대해 하려는 행위(CANCEL, REQUESTED_RETURN)
   * @return 실행 결과 응답
   */
  @PatchMapping("/{orderId}/status")
  public ResponseEntity<ApiResponse<String>> setStatus(
      @CurrentMemberId Long currentMemberId, @PathVariable Long orderId, @RequestBody OrderStatusRequest status){
    return ResponseEntity.ok(
        orderService.updateStatus(currentMemberId, orderId, status)
    );
  }
}
