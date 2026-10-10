package com.shopsmart.shopping.repository;

import com.shopsmart.shopping.model.CustomerPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerPreferenceRepository extends JpaRepository<CustomerPreference, UUID> {
}
