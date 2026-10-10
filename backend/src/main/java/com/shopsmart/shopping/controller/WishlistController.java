package com.shopsmart.shopping.controller;

import com.shopsmart.shopping.dto.WishlistRequestDto;
import com.shopsmart.shopping.dto.WishlistResponseDto;
import com.shopsmart.shopping.service.WishlistService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public ResponseEntity<WishlistResponseDto> getWishlist() {
        return ResponseEntity.ok(wishlistService.getWishlist());
    }

    @PostMapping
    public ResponseEntity<WishlistResponseDto> addToWishlist(@Valid @RequestBody WishlistRequestDto request) {
        return ResponseEntity.ok(wishlistService.addToWishlist(request.getProductId()));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<WishlistResponseDto> removeFromWishlist(@PathVariable UUID productId) {
        return ResponseEntity.ok(wishlistService.removeFromWishlist(productId));
    }
}
