package com.projetos.financial_planner.services;

import com.projetos.financial_planner.dto.CategoryDTO;
import com.projetos.financial_planner.entities.Category;
import com.projetos.financial_planner.repositories.CategoryRepository;
import com.projetos.financial_planner.services.exceptions.ResourceNotFoundException;
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

    public CategoryDTO createCategory(CategoryDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category = repository.save(category);
        return new CategoryDTO(category);
    }

    public Category update(Long categoryId, CategoryDTO dto) {
        Category category = repository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com o ID" + categoryId));
        category.setName(dto.getName());
        return repository.save(category);
    }

    public void delete(Long categoryId) {
        if (!repository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Categoria não encontrada com o ID " + categoryId);
        }
        repository.deleteById(categoryId);
    }

}
