package com.projetos.financial_planner.dto;

import com.projetos.financial_planner.entities.Role;
import com.projetos.financial_planner.entities.Spend;
import com.projetos.financial_planner.entities.User;
import jakarta.validation.constraints.*;

import java.util.List;

public class UserDTO {

    private Long id;
    @NotBlank
    private String name;
    @Email
    private String email;
    @Pattern(regexp="^\\+?[0-9. ()-]{7,25}$", message="Invalid phone number")
    private String phone;
    @NotBlank
    @NotNull
    private String password;
    private List<Spend> spends;
    private List<Role> roles;

    public UserDTO(){}

    public UserDTO(Long id, String name, String email, String phone, String password, List<Spend> spends, List<Role> roles) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.spends = spends;
        this.roles = roles;
    }

    public UserDTO(User entity){
        id = entity.getId();
        name = entity.getName();
        email = entity.getEmail();
        phone = entity.getPhone();
        password = entity.getPassword();
        spends = entity.getSpends();
        roles = entity.getRoles();
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Spend> getSpends() {
        return spends;
    }

    public void setSpends(List<Spend> spends) {
        this.spends = spends;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
