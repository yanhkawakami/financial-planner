package com.projetos.financial_planner.repositories;

import com.projetos.financial_planner.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
