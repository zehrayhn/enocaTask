package com.example.enocatask.service.cart;

import com.example.enocatask.converter.CartDTOConverter;
import com.example.enocatask.dao.CartRepository;
import com.example.enocatask.dao.CustomerRepository;
import com.example.enocatask.dto.CartDTO;
import com.example.enocatask.entities.Cart;
import com.example.enocatask.entities.CartItem;
import com.example.enocatask.entities.Customer;
import com.example.enocatask.entities.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {
    private static final Logger logger = LogManager.getLogger(CartServiceImpl.class);

    private CustomerRepository customerRepository;
    private CartRepository cartRepository;
    private CartDTOConverter cartDTOConverter;

    @Autowired

    public CartServiceImpl(CustomerRepository customerRepository, CartRepository cartRepository, CartDTOConverter cartDTOConverter) {
        this.customerRepository = customerRepository;
        this.cartRepository = cartRepository;
        this.cartDTOConverter = cartDTOConverter;
    }


    @Override
    public void addProductToCart(Customer customer, Product product, int quantity) {
        Cart cart = customer.getCart();

        if (cart == null) {
            cart = new Cart();
            cart.setCustomer(customer);
            customer.setCart(cart);
        }

        if (!isStockAvailable(product, quantity)) {
            throw new RuntimeException("Ürün stokta yetersiz.");
        }

        CartItem existingCartItem = findCartItemByProductAndCustomer(product, customer);

        if (existingCartItem != null) {
            int newQuantity = existingCartItem.getQuantity() + quantity;
            existingCartItem.setQuantity(newQuantity);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cart.getItems().add(cartItem);
        }

        cart = cartRepository.save(cart);
        updateCartTotalAmount(cart);

    }

    @Override
    public CartItem findCartItemByProductAndCustomer(Product product, Customer customer) {

        Cart cart = customer.getCart();
        if (cart != null) {
            List<CartItem> cartItems = cart.getItems();
            for (CartItem cartItem : cartItems) {
                if (cartItem.getProduct().equals(product)) {
                    return cartItem;
                }
            }
        }
        return null;

    }

    @Override
    public ResponseEntity<?> getCart(int customerId) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);

        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            Cart cart = customer.getCart();

            if (cart == null) {
                String noCartMessage = "Müşterinin sepeti bulunamadı.";
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(noCartMessage);
            } else if (cart.getItems().isEmpty()) {
                String emptyCartMessage = "Sepet boş.";
                return ResponseEntity.ok(emptyCartMessage);
            } else {
                CartDTO cartDTO = cartDTOConverter.convertCartToDTO(cart);
                return ResponseEntity.ok(cartDTO);
            }
        } else {
            String errorMessage = "Müşteri bulunamadı. Müşteri ID: " + customerId;
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
    }


    @Override
    public void updateCartItem(Cart cart, int productId, int newQuantity) {
        for (CartItem cartItem : cart.getItems()) {
            if (cartItem.getProduct().getId() == productId) {
                if (!isStockAvailable(cartItem.getProduct(), newQuantity)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ürün stokta yetersiz.");
                }
                cartItem.setQuantity(newQuantity);
                break;
            }
        }

        cartRepository.save(cart);
        updateCartTotalAmount(cart);
    }

    @Override
    public ResponseEntity<String> emptyCart(Cart cart) {
        if (cart.getItems().isEmpty()) {
            String emptyCartMessage = "Sepet zaten boş.";
            return ResponseEntity.ok(emptyCartMessage);
        }

        List<CartItem> cartItems = new ArrayList<>(cart.getItems());

        cart.setTotalAmount(0.0);

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            int quantityToRemove = cartItem.getQuantity();
            updateStockQuantity(product, quantityToRemove);
            cart.getItems().clear();
        }

        try {
            cartRepository.save(cart);
            return ResponseEntity.ok("Sepet boşaltıldı.");
        } catch (Exception e) {
            logger.error(e.getMessage() + " emptycarthata");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Sepet boşaltılırken bir hata oluştu.");
        }
    }

    @Override
    public void removeProductFromCart(Cart cart, Product product, int quantityToRemove) {
        CartItem cartItemToRemove = cart.getItems().stream()
                .filter(item -> item.getProduct().equals(product))
                .findFirst()
                .orElse(null);

        if (cartItemToRemove != null) {
            int currentQuantity = cartItemToRemove.getQuantity();

            if (currentQuantity > quantityToRemove) {
                cartItemToRemove.setQuantity(currentQuantity - quantityToRemove);
            } else {
                cart.getItems().remove(cartItemToRemove);
            }
            updateStockQuantity(product, quantityToRemove);
            cartRepository.save(cart);
            updateCartTotalAmount(cart);
        }
    }

    @Override
    public boolean isStockAvailable(Product product, int quantity) {
        return product.getStockQuantity() >= quantity;
    }

    @Override
    public void updateStockQuantity(Product product, int quantity) {
        product.setStockQuantity(product.getStockQuantity() - quantity);
    }


    private void updateCartTotalAmount(Cart cart) {
        double totalAmount = cart.getItems().stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
        cart.setTotalAmount(totalAmount);
        cartRepository.save(cart);
    }


}
