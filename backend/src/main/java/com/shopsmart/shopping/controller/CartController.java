package com.shopsmart.shopping.controller;

import com.shopsmart.shopping.dto.CartItemRequestDto;
import com.shopsmart.shopping.dto.CartItemUpdateRequestDto;
import com.shopsmart.shopping.dto.CartResponseDto;
import com.shopsmart.shopping.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartResponseDto> getCart() {
        return ResponseEntity.ok(cartService.getCart());
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponseDto> addToCart(@Valid @RequestBody CartItemRequestDto request) {
        return ResponseEntity.ok(cartService.addToCart(request.getProductId(), request.getQuantity()));
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<CartResponseDto> updateCartItem(@PathVariable UUID productId,
                                                          @Valid @RequestBody CartItemUpdateRequestDto request) {
        return ResponseEntity.ok(cartService.updateCartItem(productId, request.getQuantity()));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponseDto> removeFromCart(@PathVariable UUID productId) {
        return ResponseEntity.ok(cartService.removeFromCart(productId));
    }
}
