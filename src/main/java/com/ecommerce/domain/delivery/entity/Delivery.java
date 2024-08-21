package com.ecommerce.domain.delivery.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import com.ecommerce.domain.BaseEntity;
import com.ecommerce.domain.order.entity.Order;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "deliveries")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery extends BaseEntity {
  @Id
  private Long id;

  @MapsId
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "delivery_id")
  private Order order;

  @Column(name = "message", nullable = true, length = 50)
  private String message;

  @Column(name = "receiver_name", nullable = false, length = 50)
  private String receiverName;

  @Column(name = "receiver_address", nullable = false)
  private String receiverAddress;

  @Column(name = "tell_number", nullable = false, length = 50)
  private String tellNumber;

  @Column(name = "completed_date_time")
  private LocalDateTime completedDateTime;

  @Builder
  public Delivery(Order order, String receiverAddress, String receiverName, String tellNumber) {
    this.order = order;
    this.receiverAddress = receiverAddress;
    this.receiverName = receiverName;
    this.tellNumber = tellNumber;
  }
}
