package com.projetos.financial_planner.services;

import com.projetos.financial_planner.entities.User;
import com.projetos.financial_planner.services.exceptions.ForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthService {

    @Autowired
    private UserService userService;

    public void validateSelfOrAdmin(Long userId){
        User user = userService.authenticated();
        if (!user.hasRole("ROLE_ADMIN") && !Objects.equals(user.getId(), userId)){
            throw new ForbiddenException("Acesso negado para o usuário " + userId);
        }
    }
}