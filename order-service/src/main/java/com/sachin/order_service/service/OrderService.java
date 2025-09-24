package com.sachin.order_service.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.sachin.order_service.dto.InventoryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sachin.order_service.dto.OrderLineItemsDto;
import com.sachin.order_service.dto.OrderRequest;
import com.sachin.order_service.model.Order;
import com.sachin.order_service.model.OrderLineItems;
import com.sachin.order_service.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;


@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

	private final OrderRepository orderRepository;
    private final WebClient webClient;
	
	public void placeOrder(OrderRequest orderRequest) {
		Order order = new Order();
		order.setOrderNumber(UUID.randomUUID().toString());
		
		List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
		                                .stream()
		                                 .map(this::mapToDto)
		                                 .toList();
		
		order.setOrderLineItems(orderLineItems);

         List<String> skuCode=order.getOrderLineItems().stream()
                .map(OrderLineItems::getSkuCode)
                .toList();
        //Call Inventory Service , and place order if product is in
        //Stock



InventoryResponse[] inventoryResponses=webClient.get()
        .uri("http://localhost:8082/api/inventory",uriBuilder -> uriBuilder.queryParam(
                "skuCode",skuCode).build())
        .retrieve()
        .bodyToMono(InventoryResponse[].class)
        .block();


        assert inventoryResponses != null;
        boolean allProductsInstock =  Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);


if(allProductsInstock){
    orderRepository.save(order);
}else{
    throw new IllegalArgumentException("Product is not in stock ,please try again later");
}


	}

	private OrderLineItems  mapToDto(OrderLineItemsDto orderLineItemsDto) {
		OrderLineItems orderLineItems = new OrderLineItems();
		orderLineItems.setPrice(orderLineItemsDto.getPrice());
		orderLineItems.setQuantity(orderLineItems.getQuantity());
		orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
		
		return orderLineItems;
	}

	
}
