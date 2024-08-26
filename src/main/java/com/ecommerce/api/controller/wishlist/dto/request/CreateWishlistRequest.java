package com.ecommerce.api.controller.wishlist.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateWishlistRequest(
    @NotNull(message = "optionId는 필수 항목입니다.")
    Long optionId,

    @Positive(message = "양수만 입력 가능합니다.")
    int quantity
) {

}
