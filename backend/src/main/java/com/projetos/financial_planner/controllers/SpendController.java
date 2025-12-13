package com.projetos.financial_planner.controllers;


import com.projetos.financial_planner.dto.SpendDTO;
import com.projetos.financial_planner.dto.UserDTO;
import com.projetos.financial_planner.dto.UserMinDTO;
import com.projetos.financial_planner.services.SpendService;
import com.projetos.financial_planner.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/spends")
public class SpendController {

    @Autowired
    SpendService service;


    @GetMapping(value = "/{userId}")
    public ResponseEntity<Page<SpendDTO>> getAllSpendsByUserId(Pageable pageable,
                                                               @PathVariable Long userId,
                                                               @RequestParam (required = false) String startDate,
                                                               @RequestParam (required = false) String finalDate) {
        Page<SpendDTO> spends = service.getAllSpendsByUserId(pageable, userId, startDate, finalDate);
        return ResponseEntity.ok(spends);
    }
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PostMapping
    public ResponseEntity<?> create (@RequestBody SpendDTO dto) {
        dto = service.create(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }
}
