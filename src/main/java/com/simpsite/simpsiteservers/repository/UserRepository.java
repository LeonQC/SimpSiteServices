package com.simpsite.simpsiteservers.repository;

import com.simpsite.simpsiteservers.model.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends CrudRepository<Customer,Long> {
    List<Customer> findByEmail(String email);

    Boolean existsByEmail(String email);

    Boolean existsByUsername(String username);

    Optional<Customer> findByUsername(String username);
}
