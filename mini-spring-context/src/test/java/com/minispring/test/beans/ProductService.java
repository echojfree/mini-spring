package com.minispring.test.beans;

import com.minispring.beans.factory.annotation.Autowired;
import com.minispring.beans.factory.annotation.Component;

/**
 * ProductService - 测试 Setter 方法注入
 */
@Component
public class ProductService {

    private ProductRepository productRepository;

    public String getName() {
        return "ProductService";
    }

    public ProductRepository getProductRepository() {
        return productRepository;
    }

    // Setter 方法注入：可选依赖
    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void createProduct(String product) {
        System.out.println("ProductService 创建产品: " + product);
        productRepository.save(product);
    }
}
