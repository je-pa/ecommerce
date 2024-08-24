/**
 * @Date : 2024. 08. 24.
 * @author : jieun(je-pa)
 */
package com.ecommerce.domain.product.repository;

import static com.ecommerce.domain.product.entity.QProduct.product;
import static com.ecommerce.domain.store.entity.QStore.store;

import com.ecommerce.api.controller.product.dto.request.ReadProductListRequest;
import com.ecommerce.api.controller.product.dto.request.ReadProductListRequest.ReadProductListSort;
import com.ecommerce.api.controller.product.dto.response.ProductListItemResponse;
import com.ecommerce.api.controller.product.dto.response.QProductListItemResponse;
import com.ecommerce.api.controller.product.dto.response.QProductListItemResponse_StoreInfo;
import com.ecommerce.domain.product.type.ProductCategory;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductQueryDslRepositoryImpl implements ProductQueryDslRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public Slice<ProductListItemResponse> findListBy(ReadProductListRequest request) {
    List<ProductListItemResponse> list = queryFactory
        .select(new QProductListItemResponse(
            product.id,
            product.name,
            new QProductListItemResponse_StoreInfo(
                store.id,
                store.name
            ),
            product.stockQuantity,
            product.category,
            product.thumbnailImgUrl,
            product.price,
            product.createdDateTime
        ))
        .from(product)
        .innerJoin(product.store, store)
        .where(
            equalsStoreId(request.storeId()),
            likeSearchKeyword(request.searchKeyword()),
            equalsCategory(request.productCategory())
        )
        .orderBy(sort(request.listSort()))
        .offset(request.pageNumber() * request.pageSize())
        .limit(request.pageSize() + 1)
        .fetch();

    boolean hasNext = list.size() > request.pageSize();
    if (hasNext) {
      list.remove(list.size() - 1);
    }

    // 전체 항목 수를 계산
    long total = queryFactory
        .selectFrom(product)
        .innerJoin(product.store, store)
        .where(
            equalsStoreId(request.storeId()),
            likeSearchKeyword(request.searchKeyword()),
            equalsCategory(request.productCategory())
        )
        .fetchCount();

    return new PageImpl<>(list, request.getPageable(), total);
  }

  private OrderSpecifier<?>[] sort(ReadProductListSort sort) {
    switch (sort) {
      case PRICE_DESC:
        return new OrderSpecifier[]{
            product.price.desc(),
            product.id.desc()
        };
      case PRICE_ASC:
        return new OrderSpecifier[]{
            product.price.asc(),
            product.id.desc()
        };
      case CREATED_DATE_DESC:
        return new OrderSpecifier[]{
            product.id.desc()
        };
      case CREATED_DATE_ASC:
        return new OrderSpecifier[]{
            product.id.asc()
        };
      default:
        return null;
    }
  }

  private BooleanExpression equalsStoreId(Long storeId){
    if(storeId == null) return null;
    return product.store.id.eq(storeId);
  }

  private BooleanExpression likeSearchKeyword(String keyword) {
    if (keyword == null || keyword.trim().isEmpty()) {
      return null;
    }

    keyword = keyword.trim();
    String[] split = keyword.split(" ");
    BooleanExpression b = null;

    for (String s : split) {
      BooleanExpression currentExpression = product.name.like("%" + s + "%"); // Replace 'product.name' with the correct path
      if (b == null) {
        b = currentExpression;
      } else {
        b = b.or(currentExpression);
      }
    }

    return b;
  }


  private BooleanExpression equalsCategory(ProductCategory category) {
    if(category == null) return null;
    return product.category.eq(category);
  }
}
