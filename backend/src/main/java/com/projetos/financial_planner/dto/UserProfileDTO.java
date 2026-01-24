package com.projetos.financial_planner.dto;

import com.projetos.financial_planner.entities.Role;
import com.projetos.financial_planner.entities.Spend;
import com.projetos.financial_planner.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;
import java.util.Set;

public class UserProfileDTO {

    private Long id;
    @NotBlank
    private String name;
    @Email
    private String email;
    @Pattern(regexp="^\\+?[0-9. ()-]{7,25}$", message="Invalid phone number")
    private String phone;


    public UserProfileDTO(){}

    public UserProfileDTO(Long id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public UserProfileDTO(User entity){
        id = entity.getId();
        name = entity.getName();
        email = entity.getEmail();
        phone = entity.getPhone();
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
