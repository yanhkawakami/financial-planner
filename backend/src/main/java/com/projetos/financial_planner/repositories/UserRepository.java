package com.projetos.financial_planner.repositories;

import com.projetos.financial_planner.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
