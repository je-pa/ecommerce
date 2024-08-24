/**
 * @Date : 2024. 08. 24.
 * @author : jieun(je-pa)
 */
package com.ecommerce.api.controller.product.dto.request;

import com.ecommerce.domain.product.type.ProductCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

public record ReadProductListRequest(

    Long storeId,

    String searchKeyword,

    ProductCategory productCategory,

    Integer pageSize,

    ReadProductListSort listSort,

    Integer pageNumber

) {
  public Pageable getPageable() {
    return PageRequest.of(this.pageNumber, this.pageSize, this.listSort.sort);
  }

  public ReadProductListRequest {
    if(pageSize == null){
      pageSize = 15;
    }
    if(listSort == null){
      listSort = ReadProductListSort.CREATED_DATE_DESC;
    }
    if(pageNumber == null){
      pageNumber = 0;
    }else {
      pageNumber = Math.max(pageNumber - 1, 0);
    }
  }

  @RequiredArgsConstructor
  public enum ReadProductListSort {
    PRICE_DESC("가격 내림차순", Sort.by(new Order(Direction.DESC, "price"),
        new Order(Sort.Direction.DESC, "id"))),
    PRICE_ASC("가격 오름차순", Sort.by(new Order(Direction.ASC, "price"),
        new Order(Sort.Direction.DESC, "id"))),
    CREATED_DATE_DESC("등록 날짜 내림차순", Sort.by(Direction.DESC, "id")),
    CREATED_DATE_ASC("등록 날짜 오름차순", Sort.by(Direction.ASC, "id")),
    ;

    private final String name;
    private final Sort sort;
  }
}
