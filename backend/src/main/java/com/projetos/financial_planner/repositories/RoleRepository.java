package com.projetos.financial_planner.repositories;

import com.projetos.financial_planner.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String roleUser);
}
