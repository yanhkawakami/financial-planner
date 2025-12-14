package com.projetos.financial_planner.dto;

import com.projetos.financial_planner.entities.Spend;

import java.time.LocalDate;

public class SpendUpdateDTO {

    private Long id;
    private LocalDate spendDate;
    private Double spendValue;
    private String description;
    private Long categoryId;

    public SpendUpdateDTO() {}

    public SpendUpdateDTO(LocalDate spendDate, Double spendValue, String description, Long category) {
        this.spendDate = spendDate;
        this.spendValue = spendValue;
        this.description = description;
        this.categoryId = category;
    }

    public SpendUpdateDTO(Spend entity) {
        this.id = entity.getId();
        this.spendDate = entity.getSpendDate();
        this.spendValue = entity.getSpendValue();
        this.description = entity.getDescription();
        this.categoryId = entity.getCategory().getId();
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
}
