/**
 * @Date : 2024. 08. 27.
 * @author : jieun(je-pa)
 */
package com.ecommerce.api.controller.order.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreateOrderByWishlistItemsRequest(
    @NotNull
    Long wishlistItemId
) {

}
