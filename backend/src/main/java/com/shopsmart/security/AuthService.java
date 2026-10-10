package com.shopsmart.security;

import com.shopsmart.common.ApiException;
import com.shopsmart.common.JsonUtil;
import com.shopsmart.orders.model.Customer;
import com.shopsmart.orders.repository.CustomerRepository;
import com.shopsmart.security.dto.CustomerProfileDto;
import com.shopsmart.security.dto.LoginRequest;
import com.shopsmart.security.dto.LoginResponse;
import com.shopsmart.security.dto.RegisterRequest;
import com.shopsmart.shopping.model.CustomerPreference;
import com.shopsmart.shopping.repository.CustomerPreferenceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final CustomerRepository customerRepository;
    private final CustomerPreferenceRepository preferenceRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(CustomerRepository customerRepository,
                       CustomerPreferenceRepository preferenceRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.customerRepository = customerRepository;
        this.preferenceRepository = preferenceRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public LoginResponse login(LoginRequest request) {
        Customer customer = customerRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new ApiException("INVALID_CREDENTIALS", "Invalid email or password.", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(request.getPassword(), customer.getPasswordHash())) {
            throw new ApiException("INVALID_CREDENTIALS", "Invalid email or password.", HttpStatus.UNAUTHORIZED);
        }

        Optional<CustomerPreference> preferenceOpt = preferenceRepository.findById(customer.getId());

        CustomerProfileDto profile = new CustomerProfileDto(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getMembershipTier(),
                preferenceOpt.map(p -> JsonUtil.toList(p.getPreferredCategories())).orElse(null),
                preferenceOpt.map(p -> JsonUtil.toList(p.getPreferredBrands())).orElse(null),
                preferenceOpt.map(CustomerPreference::getBudget).orElse(null)
        );

        String token = jwtTokenProvider.generateToken(
                customer.getId(),
                customer.getEmail(),
                customer.getName(),
                customer.getMembershipTier()
        );

        return new LoginResponse(token, profile);
    }

    public LoginResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (customerRepository.existsByEmail(email)) {
            throw new ApiException("EMAIL_ALREADY_REGISTERED", "An account with this email already exists.", HttpStatus.CONFLICT);
        }

        Customer customer = customerRepository.save(new Customer(
                null,
                request.getName().trim(),
                email,
                passwordEncoder.encode(request.getPassword()),
                "Basic"
        ));

        return login(new LoginRequest(email, request.getPassword()));
    }
}
