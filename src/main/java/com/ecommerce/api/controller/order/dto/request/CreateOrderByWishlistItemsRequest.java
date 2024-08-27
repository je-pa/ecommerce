package com.ecommerce.api.controller.order.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreateOrderByWishlistItemsRequest(
    @NotNull
    Long wishlistItemId
) {

}
