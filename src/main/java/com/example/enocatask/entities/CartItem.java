package com.example.enocatask.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items")
public class CartItem extends BaseEntity {
    @JsonIgnore
    @ManyToOne
    private Cart cart;
    @JsonIgnore
    @ManyToOne
    private Product product;
    private int quantity;


    public CartItem() {
    }

    public CartItem(Cart cart) {
        this.cart = cart;
    }

    public CartItem(int id, LocalDateTime createdDate, LocalDateTime lastModifiedDate, Cart cart) {
        super(id, createdDate, lastModifiedDate);
        this.cart = cart;
    }

    public Cart getCart() {
        return cart;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "cart=" + cart +
                ", product=" + product +
                ", quantity=" + quantity +
                '}';
    }


    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public OrderItem toOrderItem(Order order) {
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(this.getProduct());
        orderItem.setQuantity(this.getQuantity());
        orderItem.setPrice(this.getProduct().getPrice());
        orderItem.setOrder(null); // Order'a atama yapma
        return orderItem;
    }
}
