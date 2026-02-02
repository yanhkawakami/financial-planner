package com.projetos.financial_planner.repositories;

import com.projetos.financial_planner.entities.Category;
import com.projetos.financial_planner.enums.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String name);
    Page<Category> findByType(CategoryType type, Pageable pageable);
    List<Category> findByType(CategoryType type);
}
