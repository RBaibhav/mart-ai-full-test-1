package com.shopsmart.shopping.controller;

import com.shopsmart.shopping.dto.PageResponse;
import com.shopsmart.shopping.dto.ProductComparisonDto;
import com.shopsmart.shopping.dto.ProductDto;
import com.shopsmart.shopping.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<ProductDto>> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UUID category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(required = false, defaultValue = "rating_desc") String sort,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "12") int size) {
        return ResponseEntity.ok(productService.getProducts(search, category, brand, priceMin, priceMax, sort, page, size));
    }

    @GetMapping("/compare")
    public ResponseEntity<ProductComparisonDto> compareProducts(@RequestParam List<UUID> ids) {
        return ResponseEntity.ok(productService.compareProducts(ids));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
}
