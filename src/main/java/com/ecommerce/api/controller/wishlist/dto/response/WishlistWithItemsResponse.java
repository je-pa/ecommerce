/**
 * @Date : 2024. 08. 26.
 * @author : jieun(je-pa)
 */
package com.ecommerce.api.controller.wishlist.dto.response;

import com.ecommerce.domain.wishlist.repository.dao.WishlistWithItemsDao;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record WishlistWithItemsResponse(
    Long wishlistId,

    Long productId,

    String productName,

    Long storeId,

    String storeName,

    Long memberId,

    List<Item> items

) {

  public record Item(
      Long itemId,

      Long optionId,

      String optionName,

      int quantity

  ) {

  }

  public static List<WishlistWithItemsResponse> toListFrom(List<WishlistWithItemsDao> daoList) {
    // wishlistId를 기준으로 WishlistWithItemsDao 항목들을 그룹화
    Map<Long, List<WishlistWithItemsDao>> groupedByWishlistId = daoList.stream()
        .collect(Collectors.groupingBy(WishlistWithItemsDao::wishlistId));

    // 그룹화된 데이터를 WishlistWithItemsResponse 객체로 변환
    return groupedByWishlistId.entrySet().stream()
        .map(entry -> {
          Long wishlistId = entry.getKey();
          List<WishlistWithItemsDao> groupedDaos = entry.getValue();

          // 각 그룹에서 첫 번째 항목을 사용해 WishlistWithItemsResponse를 생성하고
          WishlistWithItemsDao firstDao = groupedDaos.getFirst();

          // items는 그룹화된 목록에서 생성
          List<WishlistWithItemsResponse.Item> items = groupedDaos.stream()
              .map(dao -> new WishlistWithItemsResponse.Item(
                  dao.item().itemId(),
                  dao.item().optionId(),
                  dao.item().optionName(),
                  dao.item().quantity()
              ))
              .collect(Collectors.toList());

          return new WishlistWithItemsResponse(
              wishlistId,
              firstDao.productId(),
              firstDao.productName(),
              firstDao.storeId(),
              firstDao.storeName(),
              firstDao.memberId(),
              items
          );
        })
        .collect(Collectors.toList());
  }
}
