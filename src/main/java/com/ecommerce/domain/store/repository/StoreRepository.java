package com.ecommerce.domain.store.repository;

import com.ecommerce.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Integer> {

}
