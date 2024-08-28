/**
 * @Date : 2024. 08. 24.
 * @author : jieun(je-pa)
 */
package com.ecommerce.api.service.product;

import com.ecommerce.api.controller.product.dto.request.ReadProductListRequest;
import com.ecommerce.api.controller.product.dto.response.ProductDetailResponse;
import com.ecommerce.api.controller.product.dto.response.ProductListItemResponse;
import com.ecommerce.api.service.product.event.UpdateQuantityByProductOptionsEvent;
import com.ecommerce.api.service.product.event.UpdateQuantityByProductOptionsEvent.ProductOptionInfo;
import com.ecommerce.domain.product.entity.Product;
import com.ecommerce.domain.product.entity.ProductOption;
import com.ecommerce.domain.product.repository.ProductOptionRepository;
import com.ecommerce.domain.product.repository.ProductRepository;
import com.ecommerce.global.exception.CustomException;
import com.ecommerce.global.exception.ExceptionCode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final ProductOptionRepository productOptionRepository;

  @Override
  public ProductDetailResponse get(Long productId) {
    return ProductDetailResponse.from(productRepository.findWithOptions(productId));
  }

  @Override
  public Slice<ProductListItemResponse> getProductSlice(ReadProductListRequest request) {
    return productRepository.findListBy(request);
  }

  @Override
  @Transactional
  public void adjustQuantity(UpdateQuantityByProductOptionsEvent event) {
    HashMap<Long, Integer> optionQuantity = new HashMap<>();
    for (ProductOptionInfo info : event.productOptionInfos()) {
      optionQuantity.put(info.getProductOptionId(), info.getQuantity());
    }
    if (optionQuantity.size() != event.productOptionInfos().size()) {
      throw CustomException.from(ExceptionCode.PRODUCT_OPTION_DUPLICATE);
    }

    List<ProductOption> options = productOptionRepository.findAllById(optionQuantity.keySet());
    Set<Product> products = new HashSet<>();

    for (ProductOption option : options) {
      int quantity = optionQuantity.get(option.getId());
      if (option.getCount() + quantity < 0) {
        throw CustomException.from(ExceptionCode.INSUFFICIENT_STOCK);
      }
      option.addQuantity(quantity);
      products.add(option.getProduct());
    }

    for (Product product : products) {
      int totalQuantity = productOptionRepository.findAllByProduct(product)
          .stream()
          .mapToInt(ProductOption::getCount)
          .sum();
      product.setQuantity(totalQuantity);
    }
  }
}
