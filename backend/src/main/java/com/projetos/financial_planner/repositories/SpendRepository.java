package com.projetos.financial_planner.repositories;

import com.projetos.financial_planner.entities.Spend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpendRepository extends JpaRepository<Spend, Long> {
}
