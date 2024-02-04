package com.example.enocatask.service.cart;

import com.example.enocatask.dao.CartRepository;
import com.example.enocatask.dao.CustomerRepository;
import com.example.enocatask.entities.Cart;
import com.example.enocatask.entities.CartItem;
import com.example.enocatask.entities.Customer;
import com.example.enocatask.entities.Product;
import com.example.enocatask.service.cart.CartService;
import com.example.enocatask.service.order.OrderServiceImpl;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    private static final Logger logger = LogManager.getLogger(CartServiceImpl.class);

    private CustomerRepository customerRepository;
    private CartRepository cartRepository;

    @Autowired
    public CartServiceImpl(CustomerRepository customerRepository, CartRepository cartRepository) {
        this.customerRepository = customerRepository;
        this.cartRepository = cartRepository;
    }
    @Override
    public Cart createCartForCustomer(Customer customer) {
        Cart cart = new Cart();
        cart.setCustomer(customer);
        return cartRepository.save(cart);
    }
    @Override
    public void addProductToCart(Customer customer, Product product, int quantity) {
        Cart cart = customer.getCart();

        // Eğer müşterinin sepiti yoksa yeni bir sepet oluştur
        if (cart == null) {
            cart = new Cart();
            cart.setCustomer(customer);
            customer.setCart(cart);
        }

        if (!isStockAvailable(product, quantity)) {
            // Stok yetersiz, uygun bir hata mesajı döndürün veya istisna fırlatın
            throw new RuntimeException("Ürün stokta yetersiz.");
        }


        // Sepete ürünü ekle
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);



        cart.getItems().add(cartItem);


        // Sepeti güncelle
        cart=cartRepository.save(cart);
        updateCartTotalAmount(cart);

    }

    @Override
    public void updateCartItem(Cart cart, int productId, int newQuantity) {
        // Sepetteki ürünleri kontrol et
        for (CartItem cartItem : cart.getItems()) {
            if (cartItem.getProduct().getId() == productId) {
                // Güncellenen miktarı ayarla
                cartItem.setQuantity(newQuantity);
                // Cart'ın toplam tutarını güncelle (isteğe bağlı)
                // cart.setTotalAmount(updatedTotalAmount);

                break; // İlgili ürünü bulduk, döngüden çık
            }
        }

        // Cart'ı güncelle
        cartRepository.save(cart);
        updateCartTotalAmount(cart);
    }

    @Override
    public void emptyCart(Cart cart) {

        List<CartItem> cartItems = new ArrayList<>(cart.getItems());

        // Sepetin toplam tutarını sıfırla
        cart.setTotalAmount(0.0);

        // Kopyalanan ürünleri kullanarak stokları güncelle
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            int quantityToRemove = cartItem.getQuantity();
            updateStockQuantity(product, quantityToRemove);
            cart.getItems().clear();
        }

        // Kopyalanan ürünleri sepetten kaldır


        // Sepeti güncelle
        try {
            cartRepository.save(cart);
        } catch (Exception e) {
            logger.error(e.getMessage() + " emptycarthata");
        }

    }

    @Override
    public void removeProductFromCart(Cart cart, Product product, int quantityToRemove) {
        CartItem cartItemToRemove = cart.getItems().stream()
                .filter(item -> item.getProduct().equals(product))
                .findFirst()
                .orElse(null);

        // Eğer ürün bulunduysa, sepetten kaldır
        if (cartItemToRemove != null) {
            int currentQuantity = cartItemToRemove.getQuantity();

            if (currentQuantity > quantityToRemove) {
                // Belirtilen miktar kadar ürün varsa sadece miktarını azalt
                cartItemToRemove.setQuantity(currentQuantity - quantityToRemove);
            } else {
                // Belirtilen miktar kadar ürün yoksa tamamen kaldır
                cart.getItems().remove(cartItemToRemove);
            }
            updateStockQuantity(product, quantityToRemove);
            // Sepetin toplam tutarını güncelle

            // Sepeti güncelle
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
        // Sepetteki tüm ürünlerin toplam tutarını hesapla
        double totalAmount = cart.getItems().stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
        // Sepetin toplam tutarını güncelle
        cart.setTotalAmount(totalAmount);
        cartRepository.save(cart);
    }


}
