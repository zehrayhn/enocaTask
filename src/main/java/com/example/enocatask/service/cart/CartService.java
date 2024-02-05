package com.example.enocatask.service.cart;

import com.example.enocatask.entities.Cart;
import com.example.enocatask.entities.CartItem;
import com.example.enocatask.entities.Customer;
import com.example.enocatask.entities.Product;
import org.springframework.http.ResponseEntity;

public interface CartService {


    void addProductToCart(Customer customer, Product product, int quantity);

    void updateCartItem(Cart cart, int productId, int newQuantity);

    ResponseEntity<String> emptyCart(Cart cart);

    void removeProductFromCart(Cart cart, Product product, int quantityToRemove);

    boolean isStockAvailable(Product product, int quantity);

    void updateStockQuantity(Product product, int quantity);


    CartItem findCartItemByProductAndCustomer(Product product, Customer customer);

    ResponseEntity<?> getCart(int customerId);
}
