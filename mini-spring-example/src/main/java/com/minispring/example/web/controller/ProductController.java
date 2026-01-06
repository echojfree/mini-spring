package com.minispring.example.web.controller;

import com.minispring.beans.factory.annotation.Autowired;
import com.minispring.example.web.domain.Product;
import com.minispring.example.web.dto.ApiResponse;
import com.minispring.example.web.dto.CreateProductRequest;
import com.minispring.example.web.service.ProductService;
import com.minispring.web.bind.annotation.*;

import java.util.List;

/**
 * 产品控制器 - RESTful API
 * 演示 Web MVC 功能
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 获取所有产品
     * GET /api/products
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ApiResponse<List<Product>> getAllProducts() {
        System.out.println("[Controller] GET /api/products");
        List<Product> products = productService.getAllProducts();
        return ApiResponse.success(products);
    }

    /**
     * 根据 ID 获取产品
     * GET /api/products/{id}
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ApiResponse<Product> getProduct(@PathVariable("id") Long id) {
        System.out.println("[Controller] GET /api/products/" + id);
        Product product = productService.getProduct(id);
        return ApiResponse.success(product);
    }

    /**
     * 创建产品
     * POST /api/products
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse<Product> createProduct(@RequestBody CreateProductRequest request) {
        System.out.println("[Controller] POST /api/products");
        Product product = productService.createProduct(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getStock()
        );
        return ApiResponse.success(product);
    }

    /**
     * 更新产品
     * PUT /api/products/{id}
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public ApiResponse<Product> updateProduct(
            @PathVariable("id") Long id,
            @RequestBody CreateProductRequest request) {
        System.out.println("[Controller] PUT /api/products/" + id);
        Product product = productService.updateProduct(
                id,
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getStock()
        );
        return ApiResponse.success(product);
    }

    /**
     * 删除产品
     * DELETE /api/products/{id}
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ApiResponse<Void> deleteProduct(@PathVariable("id") Long id) {
        System.out.println("[Controller] DELETE /api/products/" + id);
        productService.deleteProduct(id);
        return ApiResponse.success(null);
    }

    /**
     * 搜索产品（演示 @RequestParam）
     * GET /api/products/search?keyword=laptop
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public ApiResponse<List<Product>> searchProducts(@RequestParam("keyword") String keyword) {
        System.out.println("[Controller] GET /api/products/search?keyword=" + keyword);
        // 简化实现：返回所有产品
        List<Product> products = productService.getAllProducts();
        return ApiResponse.success(products);
    }
}
