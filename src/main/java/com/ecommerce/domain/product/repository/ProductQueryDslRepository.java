/**
 * @Date : 2024. 08. 24.
 * @author : jieun(je-pa)
 */
package com.ecommerce.domain.product.repository;

import com.ecommerce.api.controller.product.dto.request.ReadProductListRequest;
import com.ecommerce.api.controller.product.dto.response.ProductListItemResponse;
import org.springframework.data.domain.Slice;

public interface ProductQueryDslRepository {
  Slice<ProductListItemResponse> findListBy(ReadProductListRequest request);
}
