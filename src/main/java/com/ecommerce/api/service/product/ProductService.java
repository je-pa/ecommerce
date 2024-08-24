/**
 * @Date : 2024. 08. 24.
 * @author : jieun(je-pa)
 */
package com.ecommerce.api.service.product;

import com.ecommerce.api.controller.product.dto.request.ReadProductListRequest;
import com.ecommerce.api.controller.product.dto.response.ProductListItemResponse;
import org.springframework.data.domain.Slice;

public interface ProductService {

  Slice<ProductListItemResponse> getProductSlice(ReadProductListRequest request);
}
