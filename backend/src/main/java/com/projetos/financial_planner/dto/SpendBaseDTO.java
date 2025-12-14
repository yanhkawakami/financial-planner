package com.projetos.financial_planner.dto;

import java.time.LocalDate;

public interface SpendBaseDTO {
    Double getSpendValue();
    String getDescription();
    LocalDate getSpendDate();
    Long getCategoryId();
}

