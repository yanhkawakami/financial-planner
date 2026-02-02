package com.projetos.financial_planner.controllers;

import com.projetos.financial_planner.dto.CategoryDTO;
import com.projetos.financial_planner.enums.CategoryType;
import com.projetos.financial_planner.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/categories")
public class CategoryController {

    @Autowired
    CategoryService service;

    @GetMapping
    public ResponseEntity<Page<CategoryDTO>> getCategories(
            @RequestParam(required = false) CategoryType type,
            Pageable pageable) {
        Page<CategoryDTO> categories;
        if (type != null) {
            categories = service.getCategoriesByType(type, pageable);
        } else {
            categories = service.getCategories(pageable);
        }
        return ResponseEntity.ok().body(categories);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO dto) {
        dto = service.createCategory(dto);
        return ResponseEntity.ok().body(dto);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PutMapping(value = "/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long categoryId, @RequestBody CategoryDTO dto) {
        CategoryDTO returnDto = new CategoryDTO(service.update(categoryId, dto));
        return ResponseEntity.ok().body(returnDto);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        service.delete(categoryId);
        return ResponseEntity.noContent().build();
    }
}
