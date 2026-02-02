package com.projetos.financial_planner.dto;

import com.projetos.financial_planner.entities.Income;

import java.time.LocalDate;

public class IncomeUpdateDTO {

    private Long id;
    private LocalDate incomeDate;
    private Double incomeValue;
    private String description;
    private Long categoryId;

    public IncomeUpdateDTO() {}

    public IncomeUpdateDTO(LocalDate incomeDate, Double incomeValue, String description, Long category) {
        this.incomeDate = incomeDate;
        this.incomeValue = incomeValue;
        this.description = description;
        this.categoryId = category;
    }

    public IncomeUpdateDTO(Income entity) {
        this.id = entity.getId();
        this.incomeDate = entity.getIncomeDate();
        this.incomeValue = entity.getIncomeValue();
        this.description = entity.getDescription();
        this.categoryId = entity.getCategory().getId();
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

    public void setCategoryId(Long category) {
        this.categoryId = category;
    }
}
