package com.shopsmart.shopping.service;

import com.shopsmart.common.ApiException;
import com.shopsmart.common.JsonUtil;
import com.shopsmart.security.CurrentUser;
import com.shopsmart.security.UserContext;
import com.shopsmart.shopping.dto.PreferenceDto;
import com.shopsmart.shopping.model.CustomerPreference;
import com.shopsmart.shopping.repository.CustomerPreferenceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PreferenceService {

    private final CustomerPreferenceRepository preferenceRepository;

    public PreferenceService(CustomerPreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    private UUID getAuthenticatedCustomerId() {
        CurrentUser user = UserContext.get();
        if (user == null) {
            throw new ApiException("UNAUTHORIZED", "Authentication required to access preferences.", HttpStatus.UNAUTHORIZED);
        }
        return user.getId();
    }

    public PreferenceDto getPreferences() {
        UUID customerId = getAuthenticatedCustomerId();
        return preferenceRepository.findById(customerId)
                .map(p -> new PreferenceDto(
                        JsonUtil.toList(p.getPreferredCategories()),
                        JsonUtil.toList(p.getPreferredBrands()),
                        p.getBudget()
                ))
                .orElseGet(() -> new PreferenceDto(null, null, null));
    }

    public PreferenceDto updatePreferences(PreferenceDto dto) {
        UUID customerId = getAuthenticatedCustomerId();
        CustomerPreference pref = preferenceRepository.findById(customerId)
                .orElseGet(() -> new CustomerPreference(customerId, null, null, null));

        if (dto.getCategories() != null) {
            pref.setPreferredCategories(JsonUtil.toJson(dto.getCategories()));
        }
        if (dto.getBrands() != null) {
            pref.setPreferredBrands(JsonUtil.toJson(dto.getBrands()));
        }
        if (dto.getBudget() != null) {
            pref.setBudget(dto.getBudget());
        }

        CustomerPreference saved = preferenceRepository.save(pref);
        return new PreferenceDto(
                JsonUtil.toList(saved.getPreferredCategories()),
                JsonUtil.toList(saved.getPreferredBrands()),
                saved.getBudget()
        );
    }
}
