package com.ecommerce.domain.wishlist.repository;

import static com.ecommerce.domain.wishlist.entity.QWishlist.wishlist;

import com.ecommerce.domain.wishlist.entity.Wishlist;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class WishlistQueryDslRepositoryImpl implements WishlistQueryDslRepository{

  private final JPAQueryFactory queryFactory;

  @Override
  public Optional<Wishlist> findByMemberIdAndProductId(Long memberId, Long productId) {
    Wishlist result = queryFactory
        .selectFrom(wishlist)
        .where(
            wishlist.member.id.eq(memberId),
            wishlist.product.id.eq(productId)
        )
        .fetchOne();

    return Optional.ofNullable(result);
  }

}
