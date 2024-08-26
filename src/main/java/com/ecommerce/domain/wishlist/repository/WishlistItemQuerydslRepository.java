package com.ecommerce.domain.wishlist.repository;

import com.ecommerce.domain.wishlist.entity.WishlistItem;
import java.util.Optional;

public interface WishlistItemQuerydslRepository {
  Optional<WishlistItem> findWithWishlistById(Long itemId);
}
