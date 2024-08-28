package com.ecommerce.api.service.order;

import com.ecommerce.api.controller.ApiResponse;
import com.ecommerce.api.controller.order.dto.request.OrderStatusRequest;
import com.ecommerce.api.service.order.dto.request.CreateOrderByWishlistItemsDto;

public interface OrderService {

  ApiResponse<String> create(CreateOrderByWishlistItemsDto of);

  ApiResponse<String> updateStatus(Long currentMemberId, Long orderId, OrderStatusRequest status);
}
