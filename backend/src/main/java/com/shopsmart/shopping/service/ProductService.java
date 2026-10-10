package com.shopsmart.shopping.service;

import com.shopsmart.common.ApiException;
import com.shopsmart.common.JsonUtil;
import com.shopsmart.orders.model.Inventory;
import com.shopsmart.orders.repository.InventoryRepository;
import com.shopsmart.shopping.dto.PageResponse;
import com.shopsmart.shopping.dto.ProductComparisonDto;
import com.shopsmart.shopping.dto.ProductDto;
import com.shopsmart.shopping.model.Category;
import com.shopsmart.shopping.model.Product;
import com.shopsmart.shopping.repository.CategoryRepository;
import com.shopsmart.shopping.repository.ProductRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public PageResponse<ProductDto> getProducts(String search, UUID categoryId, String brand,
                                               BigDecimal priceMin, BigDecimal priceMax,
                                               String sort, int page, int size) {
        Sort sortObj = Sort.by(Sort.Direction.DESC, "rating"); // default
        if ("price_asc".equalsIgnoreCase(sort)) {
            sortObj = Sort.by(Sort.Direction.ASC, "price");
        } else if ("price_desc".equalsIgnoreCase(sort)) {
            sortObj = Sort.by(Sort.Direction.DESC, "price");
        } else if ("rating_desc".equalsIgnoreCase(sort)) {
            sortObj = Sort.by(Sort.Direction.DESC, "rating");
        }

        Pageable pageable = PageRequest.of(page, Math.max(1, size), sortObj);

        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isBlank()) {
                String searchPattern = "%" + search.trim().toLowerCase() + "%";
                Predicate nameMatch = cb.like(cb.lower(root.get("name")), searchPattern);
                Predicate brandMatch = cb.like(cb.lower(root.get("brand")), searchPattern);
                Predicate descMatch = cb.like(cb.lower(root.get("description")), searchPattern);
                predicates.add(cb.or(nameMatch, brandMatch, descMatch));
            }

            if (categoryId != null) {
                // Also include subcategory IDs if any
                List<UUID> catIds = getCategoryAndChildrenIds(categoryId);
                predicates.add(root.get("categoryId").in(catIds));
            }

            if (brand != null && !brand.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("brand")), brand.trim().toLowerCase()));
            }

            if (priceMin != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), priceMin));
            }

            if (priceMax != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), priceMax));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Product> productPage = productRepository.findAll(spec, pageable);
        List<ProductDto> dtos = productPage.getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new PageResponse<>(dtos, productPage.getNumber(), productPage.getSize(),
                productPage.getTotalElements(), productPage.getTotalPages());
    }

    public ProductDto getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ApiException("NOT_FOUND", "Product not found with ID: " + id, HttpStatus.NOT_FOUND));
        return toDto(product);
    }

    public ProductComparisonDto compareProducts(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ProductComparisonDto(Collections.emptyList(), Collections.emptyList(), Collections.emptyMap());
        }

        List<Product> products = productRepository.findByIdIn(ids);
        List<ProductDto> dtos = products.stream().map(this::toDto).collect(Collectors.toList());

        // Collect all distinct specification keys across all products
        Set<String> allKeys = new LinkedHashSet<>();
        for (ProductDto dto : dtos) {
            if (dto.getSpecifications() != null) {
                allKeys.addAll(dto.getSpecifications().keySet());
            }
        }

        // Build comparison matrix: attributeKey -> list of values for each product
        Map<String, List<Object>> matrix = new LinkedHashMap<>();
        for (String key : allKeys) {
            List<Object> values = new ArrayList<>();
            for (ProductDto dto : dtos) {
                Object val = dto.getSpecifications() != null ? dto.getSpecifications().get(key) : "-";
                values.add(val != null ? val : "-");
            }
            matrix.put(key, values);
        }

        return new ProductComparisonDto(dtos, new ArrayList<>(allKeys), matrix);
    }

    public ProductDto toDto(Product product) {
        String categoryName = null;
        if (product.getCategoryId() != null) {
            categoryName = categoryRepository.findById(product.getCategoryId())
                    .map(Category::getName)
                    .orElse(null);
        }

        List<Inventory> inventoryList = inventoryRepository.findByIdProductId(product.getId());
        int totalQuantity = inventoryList.stream().mapToInt(Inventory::getAvailableQuantity).sum();
        String availability = totalQuantity > 0 ? "IN_STOCK" : "OUT_OF_STOCK";

        return new ProductDto(
                product.getId(),
                product.getCategoryId(),
                categoryName,
                product.getName(),
                product.getBrand(),
                product.getDescription(),
                product.getPrice(),
                product.getRating(),
                JsonUtil.toList(product.getImages()),
                JsonUtil.toMap(product.getSpecifications()),
                availability,
                totalQuantity
        );
    }

    private List<UUID> getCategoryAndChildrenIds(UUID categoryId) {
        List<UUID> result = new ArrayList<>();
        result.add(categoryId);
        List<Category> children = categoryRepository.findByParentCategoryId(categoryId);
        for (Category child : children) {
            result.addAll(getCategoryAndChildrenIds(child.getId()));
        }
        return result;
    }
}
