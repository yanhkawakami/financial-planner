package com.projetos.financial_planner.repositories;

import com.projetos.financial_planner.entities.Spend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SpendRepository extends JpaRepository<Spend, Long> {

    @Query("SELECT s FROM Spend s WHERE s.user.id = :userId")
    public Page<Spend> findSpendsByUserId(Pageable pageable, Long userId);
}
