/**
 * @Date : 2024. 08. 26.
 * @author : jieun(je-pa)
 */
package com.ecommerce.api.controller.wishlist;

import com.ecommerce.api.controller.ApiResponse;
import com.ecommerce.api.controller.wishlist.dto.request.CreateWishlistRequest;
import com.ecommerce.api.controller.wishlist.dto.request.UpdateWishlistItemQuantityRequest;
import com.ecommerce.api.controller.wishlist.dto.response.WishlistWithItemsResponse;
import com.ecommerce.api.service.wishlist.WishlistService;
import com.ecommerce.api.service.wishlist.dto.request.UpdateWishlistItemQuantityRequestWithId;
import com.ecommerce.global.security.annotation.CurrentMemberId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wishlists")
@RequiredArgsConstructor
public class WishlistController {
  private final WishlistService wishlistService;

  /**
   * 상품 옵션을 선택하여 위시리스트를 등록합니다.
   * @param memberId 로그인 유저 id
   * @param requests 옵션 id 와 수량을 담은 dto
   * @return 결과
   */
  @PostMapping
  public ResponseEntity<ApiResponse<String>> set(
      @CurrentMemberId Long memberId, @RequestBody List<CreateWishlistRequest> requests){
    return ResponseEntity.ok(wishlistService.modify(memberId, requests));
  }

  /**
   * 위시리스트를 조회합니다.
   * @param currentMemberId 현재 로그인 유저 id
   * @param memberId 조회하려는 멤버 id
   * @return 조회된 위시리스트
   */
  @GetMapping
  public ResponseEntity<List<WishlistWithItemsResponse>> getList(
      @CurrentMemberId Long currentMemberId, @RequestParam Long memberId
  ){
    return ResponseEntity.ok(wishlistService.readList(currentMemberId, memberId));
  }

  /**
   * 위시리스트 항목을 수정합니다.
   * @param currentMemberId 현재 로그인 유저 id
   * @param itemId 수정하려는 위시리스트 항목 id
   * @param request 수정하려는 행위
   * @return 결과
   */
  @PatchMapping("/items/{itemId}/quantity")
  public ResponseEntity<ApiResponse<String>> updateItemQuantity(
      @CurrentMemberId Long currentMemberId,
      @PathVariable Long itemId,
      @RequestBody UpdateWishlistItemQuantityRequest request){
    return ResponseEntity.ok(wishlistService.updateItemQuantity(
        UpdateWishlistItemQuantityRequestWithId.builder()
            .itemId(itemId)
            .action(request.action())
            .currentMemberId(currentMemberId)
            .build()
    ));
  }
}
