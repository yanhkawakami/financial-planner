package com.projetos.financial_planner.services;

import com.projetos.financial_planner.dto.SpendDTO;
import com.projetos.financial_planner.dto.SpendUpdateDTO;
import com.projetos.financial_planner.entities.Category;
import com.projetos.financial_planner.entities.Role;
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
    public Page<SpendDTO> getSpends(Pageable pageable, Long userId, String startDate, String finalDate, Long categoryId) {
        User user = userService.authenticated();

        if (userId != null){
            if (!user.isAdmin()){
                Long finalUserId = userId;
                userRepository.findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID " + finalUserId));
                if (!Objects.equals(userService.authenticated().getId(), userId)){
                    throw new UnauthorizedOperationException("O usuário " + userId + " não pode visualizar esse gasto, pois não é dele");
                }
            }
        } else {
            if (!user.isAdmin()){
                userId = userService.authenticated().getId();
            }
        }

        LocalDate beginDate = parseDate(startDate);
        LocalDate endDate = parseDate(finalDate);

        return repository.findSpends(pageable, userId, beginDate, endDate, categoryId).map(SpendDTO::new);
    }


    @Transactional
    public SpendDTO create(SpendDTO dto) {
        Spend entity = new Spend();

        if (!userService.authenticated().isAdmin()){
            Long authenticatedUserId = userService.authenticated().getId();
            if (!Objects.equals(dto.getUserId(), authenticatedUserId)){
                throw new UnauthorizedOperationException("O usuário " + authenticatedUserId + " não pode criar um gasto para o usuário " + dto.getUserId());
            }
        }

        copyDtoToEntity(dto, entity);
        entity = repository.save(entity);
        return new SpendDTO(entity);
    }

    @Transactional
    public SpendDTO update(Long spendId, SpendUpdateDTO dto) {
        Long userId = userService.authenticated().getId();
        Spend entity = repository.findById(spendId)
                .orElseThrow(() -> new ResourceNotFoundException("Gasto não encontrado com ID " + spendId));

        if (!userService.authenticated().isAdmin() ) {
            userId = userService.authenticated().getId();
            if (!Objects.equals(entity.getUser().getId(), userId)) {
                throw new UnauthorizedOperationException("O usuário " + userId + " não pode atualizar esse gasto, pois não é dele");
            }
        }

        copyDtoToEntity(dto, entity);
        entity = repository.save(entity);
        return new SpendDTO(entity);
    }

    @Transactional
    public void delete(Long spendId) {
        Long userId = userService.authenticated().getId();
        Spend entity = repository.findById(spendId)
                .orElseThrow(() -> new ResourceNotFoundException("Gasto não encontrado com ID " + spendId));

        if (!userService.authenticated().isAdmin() ) {
            userId = userService.authenticated().getId();
            if (!Objects.equals(entity.getUser().getId(), userId)) {
                throw new UnauthorizedOperationException("O usuário " + userId + " não pode deletar esse gasto, pois não é dele");
            }
        }

        repository.deleteById(spendId);
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
