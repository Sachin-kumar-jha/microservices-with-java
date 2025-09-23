package com.sachin.inventory_service.servie;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sachin.inventory_service.repository.InventoryRepository;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService {
	
private final InventoryRepository inventoryRepository;
 
   @Transactional(readOnly=true)
	public boolean isInStock(String skuCode) {
       return inventoryRepository.findBySkuCode(skuCode).isPresent();
	}
}
