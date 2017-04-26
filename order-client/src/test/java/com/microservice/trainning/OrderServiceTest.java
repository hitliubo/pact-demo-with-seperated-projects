package com.microservice.trainning;

import au.com.dius.pact.consumer.*;
import au.com.dius.pact.model.PactFragment;
import com.microservice.trainning.model.Order;
import com.microservice.trainning.service.OrderService;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class OrderServiceTest {

    @Rule
    public PactRule rule = new PactRule("localhost", 8080, this);

    OrderService orderClient = new OrderService("http://localhost:8080");

    List<Order> orderResults = Arrays.asList(new Order(24), new Order(50));

    private static Map<String, String> getHTTPHeaders(){
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json;charset=UTF-8");
        return headers;
    }

    @Pact(state="WhenOrdersAvailable", provider="orderProvider", consumer="orderConsumer")
    public PactFragment createOrdersFragment(ConsumerPactBuilder.PactDslWithProvider.PactDslWithState builder)
    {
        return builder.uponReceiving("a request to retrieve orders")
                .path("/orders")
                .method("GET")
                .willRespondWith()
                .headers(getHTTPHeaders())
                .status(200)
                .body("[{\"id\":24,\"title\":\"The order is created with ID [24]\"}, {\"id\":50,\"title\":\"The order is created with ID [50] \"}]").toFragment();
    }

    @Pact(state="WhenOneOrderExists", provider="orderProvider", consumer="orderConsumer")
    public PactFragment createOrderFragment(ConsumerPactBuilder.PactDslWithProvider.PactDslWithState builder) {
        return builder.uponReceiving("a request to get order with id 24")
                .path("/orders/24")
                .method("GET")
                .willRespondWith()
                .headers(getHTTPHeaders())
                .status(200)
                .body("{\"id\":24,\"title\":\"The order is created with ID [24]\"}").toFragment();
    }

    @Test
    @PactVerification("WhenOrdersAvailable")
    public void testGetOrders() {
        assertEquals(orderClient.getOrders(),
                     Arrays.asList(new Order(24),
                                   new Order(50)));
    }

    @Test
    @PactVerification("WhenOneOrderExists")
    public void testOneOrderExist() {
        assertEquals(orderClient.getOrder(24), new Order(24));
    }
}