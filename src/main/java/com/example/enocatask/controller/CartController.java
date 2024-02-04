package com.example.enocatask.controller;

import com.example.enocatask.converter.CartDTOConverter;
import com.example.enocatask.dao.CustomerRepository;
import com.example.enocatask.dao.ProductRepository;
import com.example.enocatask.dto.CartDTO;
import com.example.enocatask.entities.Cart;
import com.example.enocatask.entities.Customer;
import com.example.enocatask.entities.Product;
import com.example.enocatask.service.cart.CartService;
import com.example.enocatask.service.cart.CartServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(value="/cart")
public class CartController {
    private CartService cartService;
    private CustomerRepository customerRepository;
    private ProductRepository productRepository;

    private CartDTOConverter cartDTOConverter;




    public CartController(CartServiceImpl cartService, CustomerRepository customerRepository,ProductRepository productRepository,CartDTOConverter cartDTOConverter) {
        this.cartService = cartService;
        this.customerRepository=customerRepository;
        this.productRepository=productRepository;
        this.cartDTOConverter=cartDTOConverter;


    }


    @PostMapping("/addProduct")
    public ResponseEntity<String> addProductToCart(
            @RequestParam int customerId,
            @RequestParam int productId,
            @RequestParam int quantity) {

        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        Optional<Product> optionalProduct = productRepository.findById(productId);

        if (optionalCustomer.isPresent() && optionalProduct.isPresent()) {
            Customer customer = optionalCustomer.get();
            Product product = optionalProduct.get();

            try {

                cartService.addProductToCart(customer, product, quantity);

                return ResponseEntity.ok("Ürün sepete eklendi.");
            }catch (RuntimeException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hata: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Müşteri veya ürün bulunamadı.");
        }
    }

    @GetMapping("/getCart/{customerId}")
    public ResponseEntity<CartDTO> getCart(@PathVariable int customerId) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);

        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            CartDTO cartDTO = cartDTOConverter.convertCartToDTO(customer.getCart());
            return ResponseEntity.ok(cartDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/updateCart")
    public ResponseEntity<String> updateCart(
            @RequestParam int customerId,
            @RequestParam int productId,
            @RequestParam int newQuantity) {

        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);

        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();

            // Müşterinin sepetini al
            Cart cart = customer.getCart();

            // Sepetteki ürünleri güncelle
            cartService.updateCartItem(cart, productId, newQuantity);

            return ResponseEntity.ok("Sepet güncellendi.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Müşteri bulunamadı.");
        }
    }

    @DeleteMapping("/emptyCart/{customerId}")
    public ResponseEntity<String> emptyCart(@PathVariable int customerId) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);

        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();

            // Müşterinin sepetini boşalt
            cartService.emptyCart(customer.getCart());

            return ResponseEntity.ok("Sepet boşaltıldı.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Müşteri bulunamadı.");
        }
    }

    @DeleteMapping("/removeProduct/{customerId}/{productId}/{quantityToRemove}")
    public ResponseEntity<String> removeProductFromCart(
            @PathVariable int customerId,
            @PathVariable int productId,
            @PathVariable int quantityToRemove) {

        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        Optional<Product> optionalProduct = productRepository.findById(productId);

        if (optionalCustomer.isPresent() && optionalProduct.isPresent()) {
            Customer customer = optionalCustomer.get();
            Product product = optionalProduct.get();

            // Müşterinin sepetinden belirli bir ürünü kaldır
            cartService.removeProductFromCart(customer.getCart(), product,quantityToRemove);

            return ResponseEntity.ok("Ürün sepet+" +
                    "ten kaldırıldı.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Müşteri veya ürün bulunamadı.");
        }
    }
}
