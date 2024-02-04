package com.example.enocatask.controller;

import com.example.enocatask.converter.OrderItemDTO;
import com.example.enocatask.dao.CustomerRepository;
import com.example.enocatask.dao.OrderRepository;
import com.example.enocatask.dto.OrderDTO;
import com.example.enocatask.entities.Customer;
import com.example.enocatask.entities.Order;
import com.example.enocatask.service.cart.CartServiceImpl;
import com.example.enocatask.service.order.OrderService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value="/order")
public class OrderController {
    private static final Logger logger = LogManager.getLogger(OrderController.class);

        private OrderService orderService;
        private CustomerRepository customerRepository;

        private OrderRepository orderRepository;
        @Autowired
        public OrderController(OrderService orderService,CustomerRepository customerRepository,OrderRepository orderRepository) {
            this.orderService = orderService;
            this.customerRepository=customerRepository;
            this.orderRepository=orderRepository;
        }

        @PostMapping("/placeOrder/{customerId}")
        public ResponseEntity<String> placeOrder(@PathVariable int customerId) {
            Optional<Customer> optionalCustomer = customerRepository.findById(customerId);

            if (optionalCustomer.isPresent()) {
                Customer customer = optionalCustomer.get();
                orderService.placeOrder(customer);
                return ResponseEntity.ok("Sipariş başarıyla oluşturuldu.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Müşteri bulunamadı.");
            }
        }


    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderItemDTO>> getAllOrdersForCustomer(@PathVariable int customerId) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);

        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            List<OrderItemDTO> orderItemDTOs = orderService.getAllOrdersForCustomer(customer);
            return new ResponseEntity<>(orderItemDTOs, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping("/orderCode/{orderCode}")
    public ResponseEntity<OrderDTO> getOrderForCode(@PathVariable String orderCode) {
        try {
            OrderDTO orderDTO = orderService.getOrderForCode(orderCode);
            return new ResponseEntity<>(orderDTO, HttpStatus.OK);
        } catch (OrderNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }




    }


    }

