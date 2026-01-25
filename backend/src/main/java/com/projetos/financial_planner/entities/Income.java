package com.projetos.financial_planner.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "tb_income")
public class Income {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate incomeDate;
    private Double incomeValue;
    private String description;

    @ManyToOne
    @JoinColumn(name = "income_category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Income() {}

    public Income(Long id, LocalDate incomeDate, Double incomeValue, String description, Category category) {
        this.id = id;
        this.incomeDate = incomeDate;
        this.incomeValue = incomeValue;
        this.description = description;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getincomeDate() {
        return incomeDate;
    }

    public void setincomeDate(LocalDate setincomeDate) {
        this.incomeDate = setincomeDate;
    }

    public Double getincomeValue() {
        return incomeValue;
    }

    public void setincomeValue(Double incomeValue) {
        this.incomeValue = incomeValue;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
