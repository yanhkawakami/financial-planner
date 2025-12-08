package com.projetos.financial_planner.services;

import com.projetos.financial_planner.dto.UserDTO;
import com.projetos.financial_planner.dto.UserMinDTO;
import com.projetos.financial_planner.entities.Role;
import com.projetos.financial_planner.entities.User;
import com.projetos.financial_planner.repositories.RoleRepository;
import com.projetos.financial_planner.repositories.UserRepository;
import com.projetos.financial_planner.services.exceptions.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository repository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

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
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));

        entity.getRoles().clear();
        if (dto.getRoles() == null || dto.getRoles().isEmpty()) {
            Role defaultRole = roleRepository.findByAuthority("ROLE_USER");
            if (defaultRole != null) {
                entity.getRoles().add(defaultRole);
            }
        } else {
            for (String roleName : dto.getRoles()) {
                Role role = roleRepository.findByAuthority(roleName);
                if (role != null) {
                    entity.getRoles().add(role);
                }
            }
            if (entity.getRoles().isEmpty()) {
                Role defaultRole = roleRepository.findByAuthority("ROLE_USER");
                if (defaultRole != null) {
                    entity.getRoles().add(defaultRole);
                }
            }
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Usuário não encontrado: " + username);
        }
        return (UserDetails) user;
    }
}
