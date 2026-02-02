package com.projetos.financial_planner.dto;

import com.projetos.financial_planner.entities.Category;
import com.projetos.financial_planner.enums.CategoryType;
import jakarta.persistence.*;


public class CategoryDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private CategoryType type;

    public CategoryDTO() {}

    public CategoryDTO(Long id, String name, CategoryType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public CategoryDTO(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.type = category.getType();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CategoryType getType() {
        return type;
    }

    public void setType(CategoryType type) {
        this.type = type;
    }
}
