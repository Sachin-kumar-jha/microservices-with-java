package com.sachin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.sachin.dto.InventoryResponse;
import com.sachin.service.InventoryService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
 
	private final InventoryService inventoryService;
	
	@GetMapping()
	@ResponseStatus(HttpStatus.OK)
	public List<InventoryResponse> isInStock(@RequestParam List<String> skuCode) {
		return inventoryService.isInStock(skuCode);
	}
	
}
