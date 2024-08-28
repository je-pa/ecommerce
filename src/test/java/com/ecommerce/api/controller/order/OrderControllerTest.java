/**
 * @Date : 2024. 08. 27.
 * @author : jieun(je-pa)
 */
package com.ecommerce.api.controller.order;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ecommerce.api.ControllerTestSupport;
import com.ecommerce.api.controller.annotation.WithMockCustomUser;
import com.ecommerce.api.controller.order.dto.request.CreateOrderByWishlistItemsRequest;
import com.ecommerce.api.controller.order.dto.request.OrderStatusRequest;
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

  @DisplayName("주문 상태를 업데이트 한다.")
  @Test
  @WithMockCustomUser(memberId = 1L)
  void setStatus() throws Exception {
    mockMvc.perform(
            patch("/api/orders/1/status")
                .content(objectMapper.writeValueAsString(OrderStatusRequest.CANCEL))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        ).andDo(print())
        .andExpect(status().isOk());
  }
}