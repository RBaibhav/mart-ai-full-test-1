package com.shopsmart.shopping.service;

import com.shopsmart.common.ApiException;
import com.shopsmart.common.JsonUtil;
import com.shopsmart.orders.model.Inventory;
import com.shopsmart.orders.repository.InventoryRepository;
import com.shopsmart.security.CurrentUser;
import com.shopsmart.security.UserContext;
import com.shopsmart.shopping.dto.CartItemDto;
import com.shopsmart.shopping.dto.CartResponseDto;
import com.shopsmart.shopping.model.CartItem;
import com.shopsmart.shopping.model.CartItemId;
import com.shopsmart.shopping.model.Product;
import com.shopsmart.shopping.repository.CartRepository;
import com.shopsmart.shopping.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public CartService(CartRepository cartRepository,
                       ProductRepository productRepository,
                       InventoryRepository inventoryRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    private UUID getAuthenticatedCustomerId() {
        CurrentUser user = UserContext.get();
        if (user == null) {
            throw new ApiException("UNAUTHORIZED", "Authentication required to access cart.", HttpStatus.UNAUTHORIZED);
        }
        return user.getId();
    }

    public CartResponseDto getCart() {
        UUID customerId = getAuthenticatedCustomerId();
        List<CartItem> cartItems = cartRepository.findByIdCustomerId(customerId);
        List<CartItemDto> itemDtos = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        int totalItemCount = 0;

        for (CartItem item : cartItems) {
            Optional<Product> prodOpt = productRepository.findById(item.getId().getProductId());
            if (prodOpt.isPresent()) {
                Product product = prodOpt.get();
                List<Inventory> inventoryList = inventoryRepository.findByIdProductId(product.getId());
                int stock = inventoryList.stream().mapToInt(Inventory::getAvailableQuantity).sum();
                String availability = stock >= item.getQuantity() ? "IN_STOCK" : "LOW_STOCK";

                BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                total = total.add(subtotal);
                totalItemCount += item.getQuantity();

                itemDtos.add(new CartItemDto(
                        product.getId(),
                        product.getName(),
                        product.getBrand(),
                        product.getPrice(),
                        item.getQuantity(),
                        subtotal,
                        JsonUtil.toList(product.getImages()),
                        availability
                ));
            }
        }

        return new CartResponseDto(itemDtos, total, totalItemCount);
    }

    @Transactional
    public CartResponseDto addToCart(UUID productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new ApiException("INVALID_REQUEST", "Quantity must be greater than zero.");
        }

        UUID customerId = getAuthenticatedCustomerId();
        if (!productRepository.existsById(productId)) {
            throw new ApiException("NOT_FOUND", "Product not found with ID: " + productId, HttpStatus.NOT_FOUND);
        }

        CartItemId cartItemId = new CartItemId(customerId, productId);
        CartItem cartItem = cartRepository.findById(cartItemId)
                .orElse(new CartItem(customerId, productId, 0));

        cartItem.setQuantity(cartItem.getQuantity() + quantity);
        cartRepository.save(cartItem);

        return getCart();
    }

    @Transactional
    public CartResponseDto updateCartItem(UUID productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new ApiException("INVALID_REQUEST", "Quantity must be greater than zero.");
        }

        UUID customerId = getAuthenticatedCustomerId();
        CartItemId cartItemId = new CartItemId(customerId, productId);
        CartItem cartItem = cartRepository.findById(cartItemId)
                .orElseThrow(() -> new ApiException("NOT_FOUND", "Item not found in cart.", HttpStatus.NOT_FOUND));

        cartItem.setQuantity(quantity);
        cartRepository.save(cartItem);

        return getCart();
    }

    @Transactional
    public CartResponseDto removeFromCart(UUID productId) {
        UUID customerId = getAuthenticatedCustomerId();
        cartRepository.deleteByIdCustomerIdAndIdProductId(customerId, productId);
        return getCart();
    }
}
