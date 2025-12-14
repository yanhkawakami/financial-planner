package com.projetos.financial_planner.services;

import com.projetos.financial_planner.dto.SpendDTO;
import com.projetos.financial_planner.dto.SpendUpdateDTO;
import com.projetos.financial_planner.entities.Category;
import com.projetos.financial_planner.entities.Spend;
import com.projetos.financial_planner.entities.User;
import com.projetos.financial_planner.repositories.CategoryRepository;
import com.projetos.financial_planner.repositories.SpendRepository;
import com.projetos.financial_planner.repositories.UserRepository;
import com.projetos.financial_planner.services.exceptions.ResourceNotFoundException;
import com.projetos.financial_planner.services.exceptions.UnauthorizedOperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

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

        LocalDate beginDate = parseDate(startDate);
        LocalDate endDate = parseDate(finalDate);

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

    @Transactional
    public SpendDTO update(Long spendId, SpendUpdateDTO dto) {
        Long userId = userService.authenticated().getId();
        Spend entity = repository.findById(spendId)
                .orElseThrow(() -> new ResourceNotFoundException("Gasto não encontrado com ID " + spendId));

        if (!Objects.equals(entity.getUser().getId(), userId)){
            throw new UnauthorizedOperationException("O usuário " + userId + " não pode atualizar esse gasto, pois não é dele");
        }

        copyDtoToEntity(dto, entity);
        entity = repository.save(entity);
        return new SpendDTO(entity);
    }

    public void copyDtoToEntity(SpendDTO dto, Spend entity) {
        copyCommonFields(dto.getSpendValue(), dto.getDescription(), dto.getSpendDate(), dto.getCategoryId(), entity);
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID " + dto.getUserId()));
        entity.setUser(user);
    }

    public void copyDtoToEntity(SpendUpdateDTO dto, Spend entity) {
        copyCommonFields(dto.getSpendValue(), dto.getDescription(), dto.getSpendDate(), dto.getCategoryId(), entity);
    }

    private void copyCommonFields(Double spendValue, String description, LocalDate spendDate, Long categoryId, Spend entity) {
        entity.setSpendValue(spendValue);
        entity.setDescription(description);
        entity.setSpendDate(spendDate);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com ID " + categoryId));
        entity.setCategory(category);
    }

    private boolean isNotEmpty(String value) {
        return value != null && !value.isEmpty();
    }

    private LocalDate parseDate(String dateStr) {
        return isNotEmpty(dateStr) ? LocalDate.parse(dateStr, formatter) : null;
    }


}
