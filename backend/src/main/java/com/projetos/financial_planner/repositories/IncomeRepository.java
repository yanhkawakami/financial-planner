package com.projetos.financial_planner.repositories;

import com.projetos.financial_planner.entities.Income;
import com.projetos.financial_planner.entities.Spend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface IncomeRepository extends JpaRepository<Income, Long> {

    @Query("SELECT i FROM Income i WHERE i.user.id = :userId")
    public Page<Income> findIncomesByUserId(Pageable pageable, Long userId);

    @Query("SELECT i FROM Income i WHERE (:userId IS NULL OR i.user.id = :userId) " +
            "AND (:startDate IS NULL OR i.incomeDate >= :startDate) " +
            "AND (:finalDate IS NULL OR i.incomeDate <= :finalDate) " +
            "AND (:categoryId IS NULL OR i.category.id = :categoryId)")
    Page<Income> findIncomes(Pageable pageable,
                           Long userId,
                           LocalDate startDate,
                           LocalDate finalDate,
                           Long categoryId);
}
