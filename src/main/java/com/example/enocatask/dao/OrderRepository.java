package com.example.enocatask.dao;

import com.example.enocatask.entities.Customer;
import com.example.enocatask.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Integer> {
    List<Order> findByCustomerOrderByCreatedDateDesc(Customer customer);

    Optional<Order> findByOrderCode(String orderCode);

    boolean existsByOrderCode(String orderCode);

    List<Order> findByCustomer(Customer customer);
}
