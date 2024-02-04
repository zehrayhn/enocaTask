package com.example.enocatask.dao;

import com.example.enocatask.entities.Customer;
import com.example.enocatask.entities.Order;
import com.example.enocatask.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem,Integer> {
   List<OrderItem> findByOrder_Customer(Customer customer);
}
