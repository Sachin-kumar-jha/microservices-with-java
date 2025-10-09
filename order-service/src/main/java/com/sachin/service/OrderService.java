package com.sachin.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.hc.core5.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;

import com.sachin.dto.InventoryResponse;
import com.sachin.dto.OrderLineItemsDto;
import com.sachin.dto.OrderRequest;
import com.sachin.model.Order;
import com.sachin.model.OrderLineItems;
import com.sachin.repository.OrderRepository;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;


@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

	private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
	private final Tracer tracer;
	public String placeOrder(OrderRequest orderRequest) {
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

         Span name = tracer.nextSpan().name("InventoryServiceLookup");
        
         try(Tracer.SpanInScope spanScope= tracer.withSpan(name.start())){
        	 InventoryResponse[] inventoryResponses=webClientBuilder.build().get()
        		        .uri("http://inventory-service/api/inventory",uriBuilder -> uriBuilder.queryParam(
        		                "skuCode",skuCode).build())
        		        .retrieve()
        		        .bodyToMono(InventoryResponse[].class)
        		        .block();

        		if(inventoryResponses.length==0)  return "Inventory not available out of Stock";
        		        assert inventoryResponses != null;
        		        boolean allProductsInstock =  Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);

        		System.out.println("InventorySErvice:"+ " " +allProductsInstock);
        		if(allProductsInstock){
        		    orderRepository.save(order);
        		    return "Order placed Succesfully";
        		}else{
        			return "Product is not in stock ,please try again later";
        		}

         }finally {
        	 name.end();
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
