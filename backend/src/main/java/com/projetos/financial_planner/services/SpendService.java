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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class SpendService {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    SpendRepository repository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Transactional(readOnly = true)
    public Page<SpendDTO> getSpends(Pageable pageable, String startDate, String finalDate, Long userId) {
        if (userId != null) {
            userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID " + userId));
        }

        LocalDate beginDate = isNotEmpty(startDate) ? parseDate(startDate, null) : null;
        LocalDate endDate = isNotEmpty(finalDate) ? parseDate(finalDate, null) : null;

        return repository.findSpends(pageable, userId, beginDate, endDate).map(SpendDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<SpendDTO> getAuthenticatedUserSpends(Pageable pageable, String startDate, String finalDate) {
        return getSpends(pageable, startDate, finalDate, userService.authenticated().getId());
    }


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

    private boolean isNotEmpty(String value) {
        return value != null && !value.isEmpty();
    }

    private LocalDate parseDate(String dateStr, LocalDate defaultValue) {
        return isNotEmpty(dateStr) ? LocalDate.parse(dateStr, formatter) : defaultValue;
    }


}
