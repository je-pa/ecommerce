/**
 * @Date : 2024. 08. 26.
 * @author : jieun(je-pa)
 */
package com.ecommerce.api.service.wishlist;

import com.ecommerce.api.controller.ApiResponse;
import com.ecommerce.api.controller.wishlist.dto.request.CreateWishlistRequest;
import com.ecommerce.api.controller.wishlist.dto.response.WishlistWithItemsResponse;
import com.ecommerce.api.service.wishlist.dto.request.UpdateWishlistItemQuantityRequestWithId;
import com.ecommerce.domain.member.entity.Member;
import com.ecommerce.domain.member.repository.MemberRepository;
import com.ecommerce.domain.product.entity.Product;
import com.ecommerce.domain.product.entity.ProductOption;
import com.ecommerce.domain.product.repository.ProductOptionRepository;
import com.ecommerce.domain.wishlist.entity.Wishlist;
import com.ecommerce.domain.wishlist.entity.WishlistItem;
import com.ecommerce.domain.wishlist.repository.WishlistItemRepository;
import com.ecommerce.domain.wishlist.repository.WishlistRepository;
import com.ecommerce.global.exception.CustomException;
import com.ecommerce.global.exception.ExceptionCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService{
  private final WishlistRepository wishlistRepository;
  private final WishlistItemRepository wishlistItemRepository;
  private final ProductOptionRepository productOptionRepository;
  private final MemberRepository memberRepository;


  @Override
  @Transactional
  public ApiResponse<String> modify(Long memberId, List<CreateWishlistRequest> requests) {
    if(requests == null || requests.isEmpty()){
      throw CustomException.from(ExceptionCode.REQUESTS_EMPTY);
    }

    // 1. requests에서 optionId 리스트 추출
    Set<Long> optionIds = requests.stream()
        .map(CreateWishlistRequest::optionId)
        .collect(Collectors.toSet());

    if(optionIds.size() != requests.size()){
      throw CustomException.from(ExceptionCode.PRODUCT_OPTION_DUPLICATE);
    }

    // 2. ProductOptionRepository에서 해당 optionId들로 ProductOption 조회
    List<ProductOption> productOptions = productOptionRepository.findAllById(optionIds);

    if(productOptions.size() != requests.size()){
      throw CustomException.from(ExceptionCode.PRODUCT_OPTIONS_NOT_FOUND);
    }

    // 3. 모든 ProductOption의 Product ID가 동일한지 확인
    Set<Long> productIds = productOptions.stream()
        .map(productOption -> productOption.getProduct().getId())
        .collect(Collectors.toSet());

    if (productIds.size() > 1) {
      throw CustomException.from(ExceptionCode.WISHLIST_OPTIONS_SAME_PRODUCT_ONLY);
    }

    // 4. Member 및 Product에 해당하는 wishlist가 있으면 조회 없으면 생성
    Wishlist wishlist = wishlistRepository.findByMemberIdAndProductId(
            memberId, productOptions.get(0).getProduct().getId())
        .orElseGet(() -> {
          // Member 및 Product 조회
          Member member = memberRepository.findById(memberId)
              .orElseThrow(() -> CustomException.from(ExceptionCode.USER_NOT_FOUND));

          Product product = productOptions.get(0).getProduct();

          // 새로운 Wishlist 생성
          Wishlist newWishlist = Wishlist.builder()
              .member(member)
              .product(product)
              .build();

          // Wishlist 저장
          return wishlistRepository.save(newWishlist);
        });
    List<WishlistItem> items = wishlistItemRepository.findByWishlist(wishlist);

    // 5. requests와 매칭되는 WishlistItem 처리
    List<WishlistItem> newItems = new ArrayList<>();
    for (CreateWishlistRequest request : requests) {
      ProductOption option = productOptions.stream()
          .filter(po -> po.getId().equals(request.optionId()))
          .findFirst()
          .orElseThrow(() -> CustomException.from(ExceptionCode.PRODUCT_OPTION_NOT_FOUND));

      WishlistItem existingItem = items.stream()
          .filter(item -> item.getOption().equals(option))
          .findFirst()
          .orElse(null);

      if (existingItem != null) {
        // 5-1. 매칭되는 WishlistItem이 있으면 quantity 추가
        existingItem.addQuantity(request.quantity());
      } else {
        // 5-2. 매칭되는 WishlistItem이 없으면 새로 생성
        WishlistItem newItem = WishlistItem.builder()
            .wishlist(wishlist)
            .option(option)
            .quantity(request.quantity())
            .build();
        newItems.add(newItem);
      }
    }
    if(!newItems.isEmpty()){
      wishlistItemRepository.saveAll(newItems);
    }

    return ApiResponse.ok("Wishlist updated successfully");
  }

  @Override
  public List<WishlistWithItemsResponse> readList(Long currentMemberId, Long memberId) {
    if(currentMemberId != memberId){
      throw CustomException.from(ExceptionCode.UNAUTHORIZED_ACCESS);
    }
    return WishlistWithItemsResponse.toListFrom(
        wishlistRepository.findWishlistWithItemsDaoListBy(memberId));
  }

  @Override
  @Transactional
  public ApiResponse<String> updateItemQuantity(UpdateWishlistItemQuantityRequestWithId request) {
    WishlistItem wishlistItem = wishlistItemRepository.findWithWishlistById(request.itemId())
        .orElseThrow(() -> CustomException.from(ExceptionCode.WISHLIST_ITEM_NOT_FOUND));

    if(request.currentMemberId() != wishlistItem.getWishlist().getMember().getId()){
      throw CustomException.from(ExceptionCode.UNAUTHORIZED_ACCESS);
    }

    switch (request.action()){
      case DECREASE -> {
        int quantity = wishlistItem.getQuantity();
        if(quantity < 2){
          Wishlist wishlist = wishlistItem.getWishlist();
          if(wishlistItemRepository.countByWishlist(wishlist) == 1){
            wishlistRepository.delete(wishlist);
          }
          wishlistItemRepository.delete(wishlistItem);
        }else{
          wishlistItem.addQuantity(-1);
        }
      }
      case INCREASE -> wishlistItem.addQuantity(1);
    }

    return ApiResponse.ok("Wishlist updated successfully");
  }

}
