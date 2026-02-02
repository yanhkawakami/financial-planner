package com.projetos.financial_planner.controllers;


import com.projetos.financial_planner.dto.IncomeDTO;
import com.projetos.financial_planner.dto.IncomeUpdateDTO;
import com.projetos.financial_planner.dto.SpendDTO;
import com.projetos.financial_planner.dto.SpendUpdateDTO;
import com.projetos.financial_planner.entities.Income;
import com.projetos.financial_planner.services.IncomeService;
import com.projetos.financial_planner.services.SpendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/incomes")
public class IncomeController {

    @Autowired
    IncomeService service;

    @GetMapping
    public ResponseEntity<Page<IncomeDTO>> getSpends(Pageable pageable,
                                                      @RequestParam(required = false) Long userId,
                                                      @RequestParam(required = false) String startDate,
                                                      @RequestParam(required = false) String finalDate,
                                                      @RequestParam(required = false) Long categoryId) {
        return ResponseEntity.ok(service.getIncomes(pageable, userId, startDate, finalDate, categoryId));
    }

    @GetMapping (value = "/{incomeId}")
    public ResponseEntity<IncomeDTO> getSpendById(@PathVariable Long incomeId) {
        IncomeDTO dto = service.getIncomeById(incomeId);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PostMapping
    public ResponseEntity<?> create (@RequestBody IncomeDTO dto) {
        dto = service.create(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PutMapping(value = "/{incomeId}")
    public ResponseEntity<IncomeDTO> update (@PathVariable Long incomeId, @RequestBody IncomeUpdateDTO dto) {
        IncomeDTO returnDto = service.update(incomeId, dto);
        return ResponseEntity.ok(returnDto);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @DeleteMapping(value = "/{incomeId}")
    public ResponseEntity<Void> delete (@PathVariable Long incomeId) {
        service.delete(incomeId);
        return ResponseEntity.noContent().build();
    }

}
