package com.projetos.financial_planner.repositories;

import com.projetos.financial_planner.entities.Spend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface SpendRepository extends JpaRepository<Spend, Long> {

    @Query("SELECT s FROM Spend s WHERE s.user.id = :userId")
    public Page<Spend> findSpendsByUserId(Pageable pageable, Long userId);

    @Query("SELECT s FROM Spend s WHERE (:userId IS NULL OR s.user.id = :userId) " +
            "AND (:startDate IS NULL OR s.spendDate >= :startDate) " +
            "AND (:finalDate IS NULL OR s.spendDate <= :finalDate)")
    Page<Spend> findSpends(Pageable pageable,
                           Long userId,
                           LocalDate startDate,
                           LocalDate finalDate);
}
