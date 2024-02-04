package com.example.enocatask.service.order;

import com.example.enocatask.controller.OrderNotFoundException;
import com.example.enocatask.converter.OrderItemDTO;
import com.example.enocatask.dao.OrderItemRepository;
import com.example.enocatask.dao.OrderRepository;
import com.example.enocatask.dto.OrderDTO;
import com.example.enocatask.entities.*;
import com.example.enocatask.service.cart.CartService;
import org.apache.juli.logging.Log;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LogManager.getLogger(OrderServiceImpl.class);

    private OrderRepository orderRepository;
    private CartService cartService;
    private OrderItemRepository orderItemRepository;

    public OrderServiceImpl(OrderRepository orderRepository, CartService cartService, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.orderItemRepository = orderItemRepository;
    }

    public void placeOrder(Customer customer) {
        String orderCode;
        do {
            orderCode = generateOrderCode();
        } while (orderRepository.existsByOrderCode(orderCode));
        Order order = new Order();
        order.setOrderCode(orderCode);
        order.setCustomer(customer);

        double totalAmount = calculateTotalAmount(customer.getCart().getItems());
        order.setTotalAmount(totalAmount);

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : customer.getCart().getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            orderItem.setOrder(order);

            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);
        double totalPrice = calculateTotalPrice(orderItems);
        order.setTotalPrice(totalPrice);

        customer.getOrders().add(order);

        // Sepetteki ürünleri kaldırma yerine sadece Order'a referans veren OrderItem'ları kaldır
        for (OrderItem orderItem : orderItems) {
            customer.getCart().getItems().removeIf(item -> item.getProduct().equals(orderItem.getProduct()));
        }

        // Sepetteki totalAmount'ı güncelle
        double updatedTotalAmount = calculateTotalAmount(customer.getCart().getItems());
        customer.getCart().setTotalAmount(updatedTotalAmount);

        orderRepository.save(order);
    }
    private String generateOrderCode() {
        return UUID.randomUUID().toString();
    }

    @Override
    public List<OrderItemDTO> getAllOrdersForCustomer(Customer customer) {
        List<Order> orders = orderRepository.findByCustomer(customer);

        List<OrderItemDTO> orderItemDTOs = new ArrayList<>();
        for (Order order : orders) {
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                OrderItemDTO orderItemDTO = new OrderItemDTO();
                orderItemDTO.setProductName(orderItem.getProduct().getName());
                orderItemDTO.setQuantity(orderItem.getQuantity());
                orderItemDTO.setPrice(orderItem.getPrice());

                orderItemDTOs.add(orderItemDTO);
            }
        }

        return orderItemDTOs;
    }

    @Override
    public OrderDTO getOrderForCode(String orderCode) {
        Optional<Order> optionalOrder = orderRepository.findByOrderCode(orderCode);

        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            return OrderDTO.convertToDTO(order);
        } else {
            throw new OrderNotFoundException("Sipariş bulunamadı: " + orderCode);
        }
    }

    private OrderDTO convertOrderToDTO(Order order) {
        // OrderDTO sınıfını kullanarak Order nesnesini DTO'ya çevir
        OrderDTO orderDTO = new OrderDTO();
        // Gerekli alanları set et
        orderDTO.setOrderCode(order.getOrderCode());
        orderDTO.setTotalAmount(order.getTotalAmount());
        // Diğer alanları da set et...

        return orderDTO;
    }

    private double calculateTotalAmount(List<CartItem> cartItems) {
        return cartItems.stream().mapToDouble(CartItem::getQuantity).sum();
    }

    private double calculateTotalPrice(List<OrderItem> orderItems) {
        return orderItems.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
    }

}
