package com.sachin.order_service.service;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sachin.order_service.dto.OrderLineItemsDto;
import com.sachin.order_service.dto.OrderRequest;
import com.sachin.order_service.model.Order;
import com.sachin.order_service.model.OrderLineItems;
import com.sachin.order_service.repository.OrderRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {


	private final OrderRepository orderRepository;
	
	public void placeOrder(OrderRequest orderRequest) {
		Order order = new Order();
		order.setOrderNumber(UUID.randomUUID().toString());
		
		List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
		                                .stream()
		                                 .map(this::mapToDto)
		                                 .toList();
		
		order.setOrderLineItems(orderLineItems);
		
		orderRepository.save(order);
	}

	private OrderLineItems  mapToDto(OrderLineItemsDto orderLineItemsDto) {
		OrderLineItems orderLineItems = new OrderLineItems();
		orderLineItems.setPrice(orderLineItemsDto.getPrice());
		orderLineItems.setQuantity(orderLineItems.getQuantity());
		orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
		
		return orderLineItems;
	}

	
}
