package com.projetos.financial_planner.services;

import com.projetos.financial_planner.dto.SpendDTO;
import com.projetos.financial_planner.entities.Category;
import com.projetos.financial_planner.entities.Spend;
import com.projetos.financial_planner.entities.User;
import com.projetos.financial_planner.repositories.CategoryRepository;
import com.projetos.financial_planner.repositories.SpendRepository;
import com.projetos.financial_planner.repositories.UserRepository;
import com.projetos.financial_planner.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SpendService {

    @Autowired
    SpendRepository repository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    UserRepository userRepository;

    @Transactional
    public SpendDTO create(SpendDTO dto) {
        Spend entity = new Spend();
        copyDtoToEntity(dto, entity);
        entity = repository.save(entity);
        return new SpendDTO(entity);
    }

    public void copyDtoToEntity(SpendDTO dto, Spend entity) {
        entity.setSpendValue(dto.getSpendValue());
        entity.setDescription(dto.getDescription());
        entity.setSpendDate(dto.getSpendDate());
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com ID " + dto.getCategoryId()));
        entity.setCategory(category);
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID " + dto.getUserId()));
        entity.setUser(user);
    }


}
