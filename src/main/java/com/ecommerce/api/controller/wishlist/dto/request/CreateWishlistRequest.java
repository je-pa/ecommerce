package com.ecommerce.api.controller.wishlist.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreateWishlistRequest(
    @NotNull(message = "optionId는 필수 항목입니다.")
    Long optionId,

    int quantity
) {

}
