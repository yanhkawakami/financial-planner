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

    @Query("SELECT s FROM Spend s WHERE s.spendDate BETWEEN :startDate AND :finalDate")
    public Page<Spend> findSpendsBetweenStartAndFinalDate(Pageable pageable, LocalDate startDate, LocalDate finalDate);
}
