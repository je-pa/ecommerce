package com.ecommerce.api.service.wishlist;

import com.ecommerce.api.controller.ApiResponse;
import com.ecommerce.api.controller.wishlist.dto.request.CreateWishlistRequest;
import java.util.List;

public interface WishlistService {

  ApiResponse<String> modify(Long memberId, List<CreateWishlistRequest> requests);


}
