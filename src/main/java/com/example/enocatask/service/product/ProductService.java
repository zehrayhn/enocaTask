package com.example.enocatask.service.product;

import com.example.enocatask.entities.Product;
import org.springframework.stereotype.Service;


public interface ProductService {
    Product createProduct(Product product);

    Product updateProduct(int productId, Product updateProduct);

    void deleteProduct(int id);

    Product getProduct(int productId);
}
