package com.ecommerce.domain.wishlist.repository;

import com.ecommerce.domain.wishlist.entity.Wishlist;
import com.ecommerce.domain.wishlist.entity.WishlistItem;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {

  @EntityGraph(attributePaths = "option")
  List<WishlistItem> findByWishlist(Wishlist wishlist);
}
