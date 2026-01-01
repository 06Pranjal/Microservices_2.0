package com.microservice_project.orderService.service;

import com.microservice_project.orderService.dto.OrderLineItemsDto;
import com.microservice_project.orderService.dto.OrderRequest;
import com.microservice_project.orderService.model.Order;
import com.microservice_project.orderService.model.OrderLineItems;
import com.microservice_project.orderService.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    private final WebClient webClient;

    public void placeOrder(OrderRequest orderRequest){
        Order order=new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems=orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodesorder.getOrderLineItemsList()
                .stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        // Call Inventory Service and place order if product is in stock
        Boolean result=webClient.get()
                .uri("http://localhost:8082/api/inventory")
                        .retrieve()
                                .bodyToMono(Boolean.class)
                // adding block method will make this communication Asynchronous a
                                        .block();

        if(Boolean.TRUE.equals(result)){
            orderRepository.save(order);
        }else{
            throw new IllegalArgumentException("Product is not in stock,please try again later");
        }

        orderRepository.save(order);
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto){
        OrderLineItems orderLineItems=new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;

    }
}
