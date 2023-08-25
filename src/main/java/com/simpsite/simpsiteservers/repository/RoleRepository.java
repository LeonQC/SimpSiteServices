package com.simpsite.simpsiteservers.repository;

import com.simpsite.simpsiteservers.constants.RoleName;
import com.simpsite.simpsiteservers.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {

    Optional<Role> findByName(RoleName name);

}
