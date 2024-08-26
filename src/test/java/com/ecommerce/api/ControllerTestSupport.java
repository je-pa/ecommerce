package com.ecommerce.api;

import com.ecommerce.api.controller.auth.AuthController;
import com.ecommerce.api.controller.product.ProductController;
import com.ecommerce.api.controller.wishlist.WishlistController;
import com.ecommerce.api.service.auth.AuthService;
import com.ecommerce.api.service.product.ProductService;
import com.ecommerce.api.service.wishlist.WishlistService;
import com.ecommerce.global.security.jwt.util.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
    AuthController.class,
    ProductController.class,
    WishlistController.class
})
@ActiveProfiles("test")
public abstract class ControllerTestSupport {
  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @MockBean
  protected TokenProvider tokenProvider;

  @MockBean
  protected AuthService authService;

  @MockBean
  protected ProductService productService;

  @MockBean
  protected WishlistService wishlistService;

}
