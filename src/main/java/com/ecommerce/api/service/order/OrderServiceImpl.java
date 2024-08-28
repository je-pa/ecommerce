package com.ecommerce.api.service.order;

import com.ecommerce.api.controller.ApiResponse;
import com.ecommerce.api.controller.order.dto.request.OrderStatusRequest;
import com.ecommerce.api.service.order.dto.request.CreateOrderByWishlistItemsDto;
import com.ecommerce.api.service.order.dto.request.CreateOrderByWishlistItemsDto.WishlistItemsDto;
import com.ecommerce.domain.member.entity.Member;
import com.ecommerce.domain.member.repository.MemberRepository;
import com.ecommerce.domain.order.entity.Order;
import com.ecommerce.domain.order.entity.OrderItem;
import com.ecommerce.domain.order.repository.OrderItemRepository;
import com.ecommerce.domain.order.repository.OrderRepository;
import com.ecommerce.domain.order.type.OrderStatus;
import com.ecommerce.domain.product.event.UpdateQuantityByProductOptionsEvent;
import com.ecommerce.domain.wishlist.entity.Wishlist;
import com.ecommerce.domain.wishlist.entity.WishlistItem;
import com.ecommerce.domain.wishlist.repository.WishlistItemRepository;
import com.ecommerce.domain.wishlist.repository.WishlistRepository;
import com.ecommerce.global.exception.CustomException;
import com.ecommerce.global.exception.ExceptionCode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private final WishlistItemRepository wishlistItemRepository;
  private final WishlistRepository wishlistRepository;
  private final MemberRepository memberRepository;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  @Transactional
  public ApiResponse<String> create(CreateOrderByWishlistItemsDto dto) {
    /* 중복된 아이템 아이디가 있는지 확인 */
    Set<Long> wishlistIds = dto.wishlistItems().stream().map(WishlistItemsDto::wishlistItemId)
        .collect(Collectors.toSet());
    if(wishlistIds.size() != dto.wishlistItems().size()){
      throw CustomException.from(ExceptionCode.WISHLIST_ITEM_DUPLICATE);
    }

    /* db에 있는 wishlistItemId 인지 확인 */
    List<WishlistItem> items = wishlistItemRepository.findAllByIdIn(wishlistIds);
    if(items.size() != dto.wishlistItems().size()){
      throw CustomException.from(ExceptionCode.WISHLIST_ITEM_NOT_FOUND);
    }

    /* 본인의 위시리스트 아이템 인지 확인 */
    Set<Long> itemsMemberIds = items.stream().map(item -> item.getWishlist().getMember().getId())
        .collect(Collectors.toSet());
    if(itemsMemberIds.size() != 1 || !itemsMemberIds.contains(dto.currentMemberId())){
      throw CustomException.from(ExceptionCode.UNAUTHORIZED_ACCESS);
    }

    saveOrderAndDeleteWishlist(dto.currentMemberId(), items);

    return ApiResponse.ok("주문 생성이 완료되었습니다.");
  }

  @Override
  @Transactional
  public ApiResponse<String> updateStatus(Long currentMemberId, Long orderId, OrderStatusRequest status) {
    Order order = orderRepository.findById(orderId).orElseThrow(
        () -> CustomException.from(ExceptionCode.ORDER_NOT_FOUND)
    );

    if(currentMemberId != order.getMember().getId()){
      throw CustomException.from(ExceptionCode.UNAUTHORIZED_ACCESS);
    }

    switch (status){
      case OrderStatusRequest.CANCEL -> cancel(order);
      case OrderStatusRequest.REQUESTED_RETURN -> requestReturn(order);
    }
    return ApiResponse.ok("상태가 업데이트 되었습니다.");
  }

  private void cancel(Order order) {
    if(order.getStatus() != OrderStatus.CREATED){
      throw CustomException.from(ExceptionCode.ORDER_CANCEL_INVALID_STATE);
    }
    order.requestCancel();
    // 상품 옵션 및 상품 수량 되돌리기
    List<OrderItem> orderItems = orderItemRepository.findAllByOrder(order.getId());
    eventPublisher.publishEvent(new UpdateQuantityByProductOptionsEvent(orderItems));
  }

  private void requestReturn(Order order) {
    if(order.getStatus() != OrderStatus.SHIPPED){
      throw CustomException.from(ExceptionCode.ORDER_RETURN_INVALID_STATE);
    }
    order.requestReturn();
  }

  private void saveOrderAndDeleteWishlist(Long currentMemberId, List<WishlistItem> items) {
    /* order, orderItem 저장 */
    // orderTotalPayment 계산
    int totalPayment = 0;
    for(WishlistItem item : items){
          int price = item.getOption().getPrice();
          int quantity = item.getQuantity();
          totalPayment += price*quantity;
    }

    List<OrderItem> orderItemsToSave = new ArrayList<>();
    Member member = memberRepository.findById(currentMemberId)
        .orElseThrow(()-> CustomException.from(ExceptionCode.USER_NOT_FOUND));
    Order order = orderRepository.save(new Order(totalPayment, member, OrderStatus.CREATED));
    Set<Wishlist> wishlists = new HashSet<>();
    for(WishlistItem item : items){
      int price = item.getOption().getPrice();
      int quantity = item.getQuantity();
      orderItemsToSave.add(new OrderItem(item.getOption(), order, quantity, price));
      wishlists.add(item.getWishlist());
    }
    orderItemRepository.saveAll(orderItemsToSave);

    /* wishlist, wishlistItem 삭제 */
    wishlistItemRepository.deleteAllInBatch(items);
    wishlists.removeIf(wishlist -> wishlistItemRepository.countByWishlist(wishlist) != 0);
    wishlistRepository.deleteAllInBatch(wishlists);
  }

}
