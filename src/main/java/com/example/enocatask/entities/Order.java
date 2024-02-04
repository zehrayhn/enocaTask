package com.example.enocatask.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order extends BaseEntity{
    @JsonIgnore
    @ManyToOne
    private Customer customer;

    private double totalPrice;

    private double totalAmount;

    @Column(name = "order_code", unique = true)
    private String orderCode;


    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<OrderItem> orderItems = new ArrayList<>();




    public Order() {
    }
    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public Order(int id, LocalDateTime createdDate, LocalDateTime lastModifiedDate, Customer customer, double totalPrice) {
        super(id, createdDate, lastModifiedDate);
        this.customer = customer;

        this.totalPrice = totalPrice;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        return "Order{" +
                "customer=" + customer +
                ", totalPrice=" + totalPrice +
                ", orderItems=" + orderItems +
                '}';
    }



    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setTotalAmount(double totalAmount) {
    }


}
