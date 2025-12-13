package com.projetos.financial_planner.dto;

import com.projetos.financial_planner.entities.Category;
import com.projetos.financial_planner.entities.Spend;
import com.projetos.financial_planner.entities.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;
import java.util.Date;

public class SpendDTO {

    private Long id;
    private LocalDate spendDate;
    private Double spendValue;
    private String description;
    private Long categoryId;
    private Long userId;

    public SpendDTO() {}

    public SpendDTO(LocalDate spendDate, Double spendValue, String description, Long category, Long userId) {
        this.spendDate = spendDate;
        this.spendValue = spendValue;
        this.description = description;
        this.categoryId = category;
        this.userId = userId;
    }

    public SpendDTO(Spend entity) {
        this.id = entity.getId();
        this.spendDate = entity.getSpendDate();
        this.spendValue = entity.getSpendValue();
        this.description = entity.getDescription();
        this.categoryId = entity.getCategory().getId();
        this.userId = entity.getUser().getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getSpendDate() {
        return spendDate;
    }

    public void setSpendDate(LocalDate spendDate) {
        this.spendDate = spendDate;
    }

    public Double getSpendValue() {
        return spendValue;
    }

    public void setSpendValue(Double spendValue) {
        this.spendValue = spendValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
