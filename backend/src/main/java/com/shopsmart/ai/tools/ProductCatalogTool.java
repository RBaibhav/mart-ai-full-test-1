package com.shopsmart.ai.tools;

import com.shopsmart.shopping.model.Product;
import com.shopsmart.shopping.repository.ProductRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductCatalogTool {

    private final ProductRepository productRepository;

    public ProductCatalogTool(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> searchCatalog(String query, BigDecimal maxPrice) {
        List<Product> allProducts = productRepository.findAll();
        String q = query != null ? query.toLowerCase().trim() : "";

        return allProducts.stream()
                .filter(p -> {
                    if (maxPrice != null && p.getPrice().compareTo(maxPrice) > 0) {
                        return false;
                    }
                    if (q.isBlank()) return true;

                    String name = p.getName().toLowerCase();
                    String brand = p.getBrand().toLowerCase();
                    String desc = p.getDescription() != null ? p.getDescription().toLowerCase() : "";
                    String specs = p.getSpecifications() != null ? p.getSpecifications().toLowerCase() : "";

                        boolean categorySearch = List.of("laptop", "headphone", "monitor", "smartphone", "phone", "camera")
                            .contains(q);
                        return name.contains(q) || brand.contains(q) || desc.contains(q)
                            || (!categorySearch && specs.contains(q));
                })
                .collect(Collectors.toList());
    }
}
