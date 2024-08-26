/**
 * @Date : 2024. 08. 26.
 * @author : jieun(je-pa)
 */
package com.ecommerce.domain.wishlist.repository.dao;


import com.querydsl.core.annotations.QueryProjection;

public record WishlistWithItemsDao(
    Long wishlistId,

    Long productId,

    String productName,

    Long storeId,

    String storeName,

    Long memberId,

    Item item

) {

  @QueryProjection
  public WishlistWithItemsDao {
  }

  public record Item(
      Long itemId,

      Long optionId,

      String optionName,

      int quantity

  ) {

    @QueryProjection
    public Item {
    }
  }
}
