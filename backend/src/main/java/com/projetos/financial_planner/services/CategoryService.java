package com.projetos.financial_planner.services;

import com.projetos.financial_planner.dto.CategoryDTO;
import com.projetos.financial_planner.entities.Category;
import com.projetos.financial_planner.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    public Page<CategoryDTO> getCategories(Pageable pageable) {
        Page<Category> page = repository.findAll(pageable);
        return page.map(CategoryDTO::new);
    }

}
