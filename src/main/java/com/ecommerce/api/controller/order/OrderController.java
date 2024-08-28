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

  @PostMapping
  public ResponseEntity<ApiResponse<String>> add(
      @CurrentMemberId Long currentMemberId,
      @Valid @RequestBody List<CreateOrderByWishlistItemsRequest> requests
  ){
    return ResponseEntity.ok(orderService.create(
      CreateOrderByWishlistItemsDto.of(requests, currentMemberId)
    ));
  }

  @PatchMapping("/{orderId}/status")
  public ResponseEntity<ApiResponse<String>> setStatus(
      @CurrentMemberId Long currentMemberId, @PathVariable Long orderId, @RequestBody OrderStatusRequest status){
    return ResponseEntity.ok(
        orderService.updateStatus(currentMemberId, orderId, status)
    );
  }
}
