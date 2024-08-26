/**
 * @Date : 2024. 08. 26.
 * @author : jieun(je-pa)
 */
package com.ecommerce.api.service.wishlist;

import com.ecommerce.api.controller.ApiResponse;
import com.ecommerce.api.controller.wishlist.dto.request.CreateWishlistRequest;
import com.ecommerce.api.controller.wishlist.dto.response.WishlistWithItemsResponse;
import com.ecommerce.api.service.wishlist.dto.request.UpdateWishlistItemQuantityRequestWithId;
import java.util.List;

public interface WishlistService {

  /**
   * 상품 옵션을 선택하여 위시리스트를 등록합니다.
   * @param memberId 로그인 유저 id
   * @param requests 옵션 id 와 수량을 담은 dto
   * @return 결과
   */
  ApiResponse<String> modify(Long memberId, List<CreateWishlistRequest> requests);

  /**
   * 위시리스트를 조회합니다.
   * @param currentMemberId 현재 로그인 유저 id
   * @param memberId 조회하려는 멤버 id
   * @return 조회된 위시리스트
   */
  List<WishlistWithItemsResponse> readList(Long currentMemberId, Long memberId);

  /**
   * 위시리스트 항목을 수정합니다.
   * @param request 현재 로그인 유저 id, 수정하려는 위시리스트 항목 id, 수정하려는 행위를 담고 있는 dto
   * @return 결과
   */
  ApiResponse<String> updateItemQuantity(UpdateWishlistItemQuantityRequestWithId request);
}
