package com.sachin.order_service.dto;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
  private List<OrderLineItemsDto> orderLineItemsDtoList;
}
