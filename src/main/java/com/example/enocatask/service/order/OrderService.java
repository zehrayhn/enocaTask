package com.example.enocatask.service.order;

import com.example.enocatask.dto.OrderItemDTO;
import com.example.enocatask.dto.OrderDTO;
import com.example.enocatask.entities.Customer;
import com.example.enocatask.entities.Product;

import java.util.List;

public interface OrderService {


    void placeOrder(Customer customer);

    void updateStockQuantity(Product product, int quantity);

    List<OrderItemDTO> getAllOrdersForCustomer(Customer customer);

    OrderDTO getOrderForCode(String orderCode);
}
