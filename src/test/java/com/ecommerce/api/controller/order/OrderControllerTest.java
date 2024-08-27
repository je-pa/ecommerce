package com.ecommerce.api.controller.order;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ecommerce.api.ControllerTestSupport;
import com.ecommerce.api.controller.annotation.WithMockCustomUser;
import com.ecommerce.api.controller.order.dto.request.CreateOrderByWishlistItemsRequest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

class OrderControllerTest extends ControllerTestSupport {

  @DisplayName("주문을 진행한다.")
  @Test
  @WithMockCustomUser(memberId = 1L)
  void sendAuthCode() throws Exception {
    // given
    List<CreateOrderByWishlistItemsRequest> requests =
        List.of(new CreateOrderByWishlistItemsRequest(1L));

    mockMvc.perform(
            post("/api/orders")
                .content(objectMapper.writeValueAsString(requests))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        ).andDo(print())
        .andExpect(status().isOk());
  }
}