/**
 * @Date : 2024. 08. 24.
 * @author : jieun(je-pa)
 */
package com.ecommerce.api.controller.product;

import com.ecommerce.api.controller.product.dto.request.ReadProductListRequest;
import com.ecommerce.api.controller.product.dto.response.ProductDetailResponse;
import com.ecommerce.api.controller.product.dto.response.ProductListItemResponse;
import com.ecommerce.api.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
  private final ProductService productService;

  /**
   * 상품의 상세 정보를 조회합니다.
   * @param productId 조회할 상품의 id
   * @return 상품 상세 정보
   */
  @GetMapping("/{productId}")
  public ResponseEntity<ProductDetailResponse> getProduct(@PathVariable Long productId) {
    return ResponseEntity.ok(
        productService.get(productId)
    );
  }

  /**
   * 상품의 리스트를 필터링하여 조회합니다.
   * @param request 상품 필터링 정보
   * @return 페이징 기반 상품 리스트
   */
  @GetMapping
  public ResponseEntity<Slice<ProductListItemResponse>> getProductSlice(
    ReadProductListRequest request
  ){
    return ResponseEntity.ok(
        productService.getProductSlice(request)
    );
  }
}
