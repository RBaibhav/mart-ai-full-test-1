package com.shopsmart.shopping.service;

import com.shopsmart.common.ApiException;
import com.shopsmart.security.CurrentUser;
import com.shopsmart.security.UserContext;
import com.shopsmart.shopping.dto.ProductDto;
import com.shopsmart.shopping.dto.WishlistResponseDto;
import com.shopsmart.shopping.model.WishlistItem;
import com.shopsmart.shopping.repository.ProductRepository;
import com.shopsmart.shopping.repository.WishlistRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    public WishlistService(WishlistRepository wishlistRepository,
                           ProductRepository productRepository,
                           ProductService productService) {
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
        this.productService = productService;
    }

    private UUID getAuthenticatedCustomerId() {
        CurrentUser user = UserContext.get();
        if (user == null) {
            throw new ApiException("UNAUTHORIZED", "Authentication required to access wishlist.", HttpStatus.UNAUTHORIZED);
        }
        return user.getId();
    }

    public WishlistResponseDto getWishlist() {
        UUID customerId = getAuthenticatedCustomerId();
        List<WishlistItem> items = wishlistRepository.findByIdCustomerIdOrderByAddedAtDesc(customerId);
        List<ProductDto> products = items.stream()
                .map(item -> productRepository.findById(item.getId().getProductId()))
                .filter(java.util.Optional::isPresent)
                .map(opt -> productService.toDto(opt.get()))
                .collect(Collectors.toList());
        return new WishlistResponseDto(products);
    }

    @Transactional
    public WishlistResponseDto addToWishlist(UUID productId) {
        UUID customerId = getAuthenticatedCustomerId();
        if (!productRepository.existsById(productId)) {
            throw new ApiException("NOT_FOUND", "Product not found with ID: " + productId, HttpStatus.NOT_FOUND);
        }
        if (!wishlistRepository.existsByIdCustomerIdAndIdProductId(customerId, productId)) {
            wishlistRepository.save(new WishlistItem(customerId, productId));
        }
        return getWishlist();
    }

    @Transactional
    public WishlistResponseDto removeFromWishlist(UUID productId) {
        UUID customerId = getAuthenticatedCustomerId();
        wishlistRepository.deleteByIdCustomerIdAndIdProductId(customerId, productId);
        return getWishlist();
    }
}
