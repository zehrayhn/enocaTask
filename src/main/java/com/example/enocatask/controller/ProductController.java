package com.example.enocatask.controller;

import com.example.enocatask.entities.Customer;
import com.example.enocatask.entities.Product;
import com.example.enocatask.service.customer.CustomerService;
import com.example.enocatask.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/product")
public class ProductController {

    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService= productService;
    }

    @PostMapping("/createproduct")
    public Product createProduct(@RequestBody Product newProduct) {
        return productService.createProduct(newProduct);
    }

    @PutMapping("/{productId}")
    public Product updateProduct(@PathVariable("productId") int productId,@RequestBody Product product) {
        return productService.updateProduct(productId,product);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable int id) {
        this.productService.deleteProduct(id);
    }

    @GetMapping("/{productId}")
    public Product getProduct(@PathVariable int productId) {
        return productService.getProduct(productId);
    }

}
