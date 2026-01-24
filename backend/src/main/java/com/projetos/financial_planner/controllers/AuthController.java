package com.projetos.financial_planner.controllers;

import com.projetos.financial_planner.dto.EmailDTO;
import com.projetos.financial_planner.dto.NewPasswordDTO;
import com.projetos.financial_planner.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    @Autowired
    AuthService service;

    @PostMapping("/recover-token")
    public ResponseEntity<Void> createRecoverToken(@RequestBody @Valid EmailDTO body) {
        service.createRecoverToken(body);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/new-password")
    public ResponseEntity<Void> saveNewPassword(@RequestBody @Valid NewPasswordDTO body) {
        service.newPassword(body);
        return ResponseEntity.noContent().build();
    }
}
