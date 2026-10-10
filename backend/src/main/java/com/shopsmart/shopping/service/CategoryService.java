package com.shopsmart.shopping.service;

import com.shopsmart.shopping.dto.CategoryDto;
import com.shopsmart.shopping.model.Category;
import com.shopsmart.shopping.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDto> getCategoryHierarchy() {
        List<Category> allCategories = categoryRepository.findAll();
        Map<UUID, List<Category>> subcategoryMap = allCategories.stream()
                .filter(c -> c.getParentCategoryId() != null)
                .collect(Collectors.groupingBy(Category::getParentCategoryId));

        List<Category> rootCategories = allCategories.stream()
                .filter(c -> c.getParentCategoryId() == null)
                .toList();

        return rootCategories.stream()
                .map(root -> toDtoWithSubcategories(root, subcategoryMap))
                .collect(Collectors.toList());
    }

    private CategoryDto toDtoWithSubcategories(Category category, Map<UUID, List<Category>> subcategoryMap) {
        CategoryDto dto = new CategoryDto(category.getId(), category.getName(), category.getParentCategoryId());
        List<Category> children = subcategoryMap.getOrDefault(category.getId(), new ArrayList<>());
        dto.setSubcategories(children.stream()
                .map(child -> toDtoWithSubcategories(child, subcategoryMap))
                .collect(Collectors.toList()));
        return dto;
    }
}
