package com.projetos.financial_planner.dto;

import com.projetos.financial_planner.entities.Role;
import com.projetos.financial_planner.entities.Spend;
import com.projetos.financial_planner.entities.User;

import java.util.List;

public class UserMinDTO {

    private Long id;
    private String name;
    private String email;

    public UserMinDTO(){}

    public UserMinDTO(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public UserMinDTO(User entity){
        id = entity.getId();
        name = entity.getName();
        email = entity.getEmail();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
