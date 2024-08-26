package com.ecommerce.api.service.wishlist.dto.request;

import com.ecommerce.api.controller.wishlist.dto.request.Action;
import lombok.Builder;

@Builder
public record UpdateWishlistItemQuantityRequestWithId(
    Action action,
    Long itemId,
    Long currentMemberId
) {


}