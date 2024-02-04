package com.example.enocatask.service.product;

import com.example.enocatask.dao.ProductRepository;
import com.example.enocatask.entities.Product;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(int productId, Product updateProduct) {
            Optional<Product> product = productRepository.findById(productId);
            if (product.isPresent()) {
                Product foundProduct = product.get();
                foundProduct.setName(updateProduct.getName());
                foundProduct.setPrice(updateProduct.getPrice());
                foundProduct.setStockQuantity(updateProduct.getStockQuantity());
                productRepository.save(foundProduct);
                return foundProduct;

            } else
                return null;
    }

    @Override
    public void deleteProduct(int id) {
        productRepository.deleteById(id);
    }

    @Override
    public Product getProduct(int productId) {
        return productRepository.findById(productId).orElse(null);
    }


}