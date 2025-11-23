package com.projetos.financial_planner.entities;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "tb_spend")
public class Spend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date spendDate;
    private Double spendValue;
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public Spend() {}

    public Spend(Long id, Date spendDate, Double spendValue, String description, Category category) {
        this.id = id;
        this.spendDate = spendDate;
        this.spendValue = spendValue;
        this.description = description;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getSpendDate() {
        return spendDate;
    }

    public void setSpendDate(Date spendDate) {
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
