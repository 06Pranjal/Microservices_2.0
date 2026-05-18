package com.microservice_project.orderService.controller;

import com.microservice_project.orderService.dto.OrderRequest;
import com.microservice_project.orderService.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name="inventory", fallbackMethod = "fallbackMethod")
    @TimeLimiter(name="inventory")
    public CompletableFuture<String> placeOrder(@RequestBody OrderRequest orderRequest){
        return CompletableFuture.supplyAsync(()->orderService.placeOrder(orderRequest));

    }
    public CompletableFuture<String > fallbackMethod(OrderRequest orderRequest,Exception exception){
        return CompletableFuture.supplyAsync(()->"Oops! Something Went Wrong ..Please try after some time....!");
    }
}
