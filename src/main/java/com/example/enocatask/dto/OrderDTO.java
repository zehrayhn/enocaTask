package com.example.enocatask.dto;

import com.example.enocatask.entities.Order;
import com.example.enocatask.entities.OrderItem;
import jakarta.persistence.Column;

import java.util.ArrayList;
import java.util.List;

public class OrderDTO {

    private double totalAmount;

    @Column(name = "order_code", unique = true)
    private String orderCode;

    private List<OrderItemDTO> orderItems;

    public List<OrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }

    public static OrderDTO convertToDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderCode(order.getOrderCode());
        orderDTO.setTotalAmount(order.getTotalAmount());

        List<OrderItemDTO> orderItemDTOs = new ArrayList<>();
        for (OrderItem orderItem : order.getOrderItems()) {
            orderItemDTOs.add(OrderItemDTO.convertToDTO(orderItem));
        }

        orderDTO.setOrderItems(orderItemDTOs);

        return orderDTO;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }
}
