package com.example.enocatask.service.cart;

import com.example.enocatask.entities.Cart;
import com.example.enocatask.entities.Customer;
import com.example.enocatask.entities.Product;

public interface CartService {
    Cart createCartForCustomer(Customer customer);

    void addProductToCart(Customer customer, Product product, int quantity);
    void updateCartItem(Cart cart, int productId, int newQuantity);

    void emptyCart(Cart cart);
    void removeProductFromCart(Cart cart, Product product, int quantityToRemove);
    boolean isStockAvailable(Product product, int quantity);
    void updateStockQuantity(Product product, int quantity);
}
