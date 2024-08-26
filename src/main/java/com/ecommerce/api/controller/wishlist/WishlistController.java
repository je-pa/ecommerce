package com.ecommerce.api.controller.wishlist;

import com.ecommerce.api.controller.ApiResponse;
import com.ecommerce.api.controller.wishlist.dto.request.CreateWishlistRequest;
import com.ecommerce.api.service.wishlist.WishlistService;
import com.ecommerce.global.security.annotation.CurrentMemberId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wishlists")
@RequiredArgsConstructor
public class WishlistController {
  private final WishlistService wishlistService;

  @PostMapping
  public ResponseEntity<ApiResponse<String>> set(
      @CurrentMemberId Long memberId, @RequestBody List<CreateWishlistRequest> requests){
    return ResponseEntity.ok(wishlistService.modify(memberId, requests));
  }


}
