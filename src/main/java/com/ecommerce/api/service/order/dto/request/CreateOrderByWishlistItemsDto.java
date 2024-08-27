package com.ecommerce.api.service.order.dto.request;

import com.ecommerce.api.controller.order.dto.request.CreateOrderByWishlistItemsRequest;
import java.util.List;
import java.util.stream.Collectors;

public record CreateOrderByWishlistItemsDto(
    List<WishlistItemsDto> wishlistItems,

    Long currentMemberId
) {
  public record WishlistItemsDto(
      Long wishlistItemId
  ){
    public static List<WishlistItemsDto> toListFrom(List<CreateOrderByWishlistItemsRequest> requests) {
      return requests.stream()
          .map(WishlistItemsDto::from)
          .collect(Collectors.toList());
    }

    public static WishlistItemsDto from(CreateOrderByWishlistItemsRequest request){
      return new WishlistItemsDto(request.wishlistItemId());
    }
  }

  public static CreateOrderByWishlistItemsDto of(List<CreateOrderByWishlistItemsRequest> requests,
      Long currentMemberId){
    return new CreateOrderByWishlistItemsDto(
        WishlistItemsDto.toListFrom(requests),
        currentMemberId
    );
  }

}
