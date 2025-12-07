package com.projetos.financial_planner.services;

import com.projetos.financial_planner.dto.UserDTO;
import com.projetos.financial_planner.dto.UserMinDTO;
import com.projetos.financial_planner.entities.Role;
import com.projetos.financial_planner.entities.User;
import com.projetos.financial_planner.repositories.RoleRepository;
import com.projetos.financial_planner.repositories.UserRepository;
import com.projetos.financial_planner.services.exceptions.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    UserRepository repository;

    @Autowired
    RoleRepository roleRepository;

    @Transactional
    public UserMinDTO create(UserDTO dto) {
        User entity = new User();
        copyDtoToEntity(dto, entity);
        if(repository.findByEmail(entity.getEmail()) != null){
            throw new UserAlreadyExistsException("Usuário já existente com esse e-mail");
        }
        repository.save(entity);
        return new UserMinDTO(entity);
    }

    private void copyDtoToEntity(UserDTO dto, User entity) {
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setPassword(dto.getPassword());
        Role role = roleRepository.findByName("ROLE_USER");
        entity.getRoles().add(role);
    }
}
