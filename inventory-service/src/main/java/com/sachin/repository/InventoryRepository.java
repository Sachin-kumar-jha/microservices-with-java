package com.sachin.repository;

import java.util.Collection;
import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;

import com.sachin.model.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory,Long> {

    List<Inventory> findBySkuCodeIn(Collection<String> skuCodes);

}
