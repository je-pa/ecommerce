package com.ecommerce.domain.order.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import com.ecommerce.domain.BaseEntity;
import com.ecommerce.domain.member.entity.Member;
import com.ecommerce.domain.order.type.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "order_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @Column(name = "amount_payment", nullable = false)
  private int amountPayment;

  @Column(name = "confirmed_date_time", nullable = true)
  private LocalDateTime confirmedDateTime;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private OrderStatus status;

  @Builder
  public Order(int amountPayment, Member member, OrderStatus status) {
    this.amountPayment = amountPayment;
    this.member = member;
    this.status = status;
  }
}
