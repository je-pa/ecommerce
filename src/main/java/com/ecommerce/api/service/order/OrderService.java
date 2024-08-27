package com.ecommerce.api.service.order;

import com.ecommerce.api.controller.ApiResponse;
import com.ecommerce.api.service.order.dto.request.CreateOrderByWishlistItemsDto;

public interface OrderService {

  ApiResponse<String> create(CreateOrderByWishlistItemsDto of);
}
