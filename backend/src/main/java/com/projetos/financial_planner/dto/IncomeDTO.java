package com.projetos.financial_planner.dto;

import com.projetos.financial_planner.entities.Income;
import com.projetos.financial_planner.entities.Spend;

import java.time.LocalDate;

public class IncomeDTO {

    private Long id;
    private LocalDate incomeDate;
    private Double incomeValue;
    private String description;
    private Long categoryId;
    private Long userId;

    public IncomeDTO() {}

    public IncomeDTO(LocalDate incomeDate, Double incomeValue, String description, Long categoryId, Long userId) {
        this.incomeDate = incomeDate;
        this.incomeValue = incomeValue;
        this.description = description;
        this.categoryId = categoryId;
        this.userId = userId;
    }

    public IncomeDTO(Income entity) {
        this.id = entity.getId();
        this.incomeDate = entity.getIncomeDate();
        this.incomeValue = entity.getIncomeValue();
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

    public LocalDate getIncomeDate() {
        return incomeDate;
    }

    public void setIncomeDate(LocalDate incomeDate) {
        this.incomeDate = incomeDate;
    }

    public Double getIncomeValue() {
        return incomeValue;
    }

    public void setIncomeValue(Double incomeValue) {
        this.incomeValue = incomeValue;
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
