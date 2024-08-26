/**
 * @Date : 2024. 08. 26.
 * @author : jieun(je-pa)
 */
package com.ecommerce.api.service.wishlist;

import com.ecommerce.api.controller.ApiResponse;
import com.ecommerce.api.controller.wishlist.dto.request.CreateWishlistRequest;
import com.ecommerce.api.controller.wishlist.dto.response.WishlistWithItemsResponse;
import java.util.List;

public interface WishlistService {

  ApiResponse<String> modify(Long memberId, List<CreateWishlistRequest> requests);


  List<WishlistWithItemsResponse> readList(Long currentMemberId, Long memberId);
}
