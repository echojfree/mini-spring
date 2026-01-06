package com.minispring.example.web.service;

import com.minispring.beans.factory.annotation.Service;
import com.minispring.example.web.domain.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 产品服务
 */
@Service
public class ProductService {

    private final Map<Long, Product> products = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public ProductService() {
        // 初始化测试数据
        createProduct("Laptop", "High performance laptop", new BigDecimal("999.99"), 10);
        createProduct("Mouse", "Wireless mouse", new BigDecimal("29.99"), 50);
        createProduct("Keyboard", "Mechanical keyboard", new BigDecimal("79.99"), 30);
    }

    public Product createProduct(String name, String description, BigDecimal price, Integer stock) {
        Long id = idGenerator.getAndIncrement();
        Product product = new Product(id, name, description, price, stock);
        products.put(id, product);
        System.out.println("[ProductService] Created product: " + product);
        return product;
    }

    public Product getProduct(Long id) {
        Product product = products.get(id);
        if (product == null) {
            throw new IllegalArgumentException("Product not found: " + id);
        }
        return product;
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }

    public Product updateProduct(Long id, String name, String description, BigDecimal price, Integer stock) {
        Product product = getProduct(id);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        System.out.println("[ProductService] Updated product: " + product);
        return product;
    }

    public void deleteProduct(Long id) {
        products.remove(id);
        System.out.println("[ProductService] Deleted product: " + id);
    }
}
