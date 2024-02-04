package com.example.enocatask.service.order;

import com.example.enocatask.converter.OrderItemDTO;
import com.example.enocatask.dto.OrderDTO;
import com.example.enocatask.entities.Customer;
import com.example.enocatask.entities.OrderItem;

import java.util.List;
import java.util.Optional;

public interface OrderService {


    void placeOrder(Customer customer);

    List<OrderItemDTO> getAllOrdersForCustomer(Customer customer);

    OrderDTO getOrderForCode(String orderCode);
}
