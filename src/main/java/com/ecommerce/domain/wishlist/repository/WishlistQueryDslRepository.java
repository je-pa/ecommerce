package com.ecommerce.domain.wishlist.repository;

import com.ecommerce.domain.wishlist.entity.Wishlist;
import java.util.Optional;

public interface WishlistQueryDslRepository {
  Optional<Wishlist> findByMemberIdAndProductId(Long memberId, Long productId);
}
