package com.ecommerce.domain.store.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import com.ecommerce.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "stores")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends BaseEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "store_id")
  private Long id;

  @Column(name = "name", nullable = false, length = 30)
  private String name;

  @Column(name = "info", nullable = false)
  private String info;

  @Column(name = "tell_number", nullable = false, length = 50)
  private String tellNumber;

  @Builder
  public Store(String info, String name, String tellNumber) {
    this.info = info;
    this.name = name;
    this.tellNumber = tellNumber;
  }
}
