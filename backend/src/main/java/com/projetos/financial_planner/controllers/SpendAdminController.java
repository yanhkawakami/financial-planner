package com.projetos.financial_planner.controllers;

import com.projetos.financial_planner.dto.SpendDTO;
import com.projetos.financial_planner.services.SpendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/admin/spends")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class SpendAdminController {

    @Autowired
    SpendService service;

    @GetMapping
    public ResponseEntity<Page<SpendDTO>> getAllSpends(Pageable pageable,
                                                       @RequestParam(required = false) String startDate,
                                                       @RequestParam(required = false) String finalDate,
                                                       @RequestParam(required = false) Long userId) {
        return ResponseEntity.ok(service.getSpends(pageable, startDate, finalDate, userId));
    }
}
