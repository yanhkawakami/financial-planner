package com.projetos.financial_planner.services;

import com.projetos.financial_planner.dto.IncomeDTO;
import com.projetos.financial_planner.dto.IncomeUpdateDTO;
import com.projetos.financial_planner.entities.*;
import com.projetos.financial_planner.enums.CategoryType;
import com.projetos.financial_planner.repositories.*;
import com.projetos.financial_planner.services.exceptions.InvalidCategoryException;
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
public class IncomeService {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    IncomeRepository repository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Transactional(readOnly = true)
    public Page<IncomeDTO> getIncomes(Pageable pageable, Long userId, String startDate, String finalDate, Long categoryId) {
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

        return repository.findIncomes(pageable, userId, beginDate, endDate, categoryId).map(IncomeDTO::new);
    }

    @Transactional(readOnly = true)
    public IncomeDTO getIncomeById(Long incomeId) {
        Long userId = userService.authenticated().getId();
        Income entity = repository.findById(incomeId)
                .orElseThrow(() -> new ResourceNotFoundException("Gasto não encontrado com ID " + incomeId));

        if (!userService.authenticated().isAdmin() ) {
            userId = userService.authenticated().getId();
            if (!Objects.equals(entity.getUser().getId(), userId)) {
                throw new UnauthorizedOperationException("O usuário " + userId + " não pode visualizar esse gasto, pois não é dele");
            }
        }

        return new IncomeDTO(entity);
    }


    @Transactional
    public IncomeDTO create(IncomeDTO dto) {
        Income entity = new Income();

        if (!userService.authenticated().isAdmin()){
            Long authenticatedUserId = userService.authenticated().getId();
            if (!Objects.equals(dto.getUserId(), authenticatedUserId)){
                throw new UnauthorizedOperationException("O usuário " + authenticatedUserId + " não pode criar um gasto para o usuário " + dto.getUserId());
            }
        }

        copyDtoToEntity(dto, entity);
        entity = repository.save(entity);
        return new IncomeDTO(entity);
    }

    @Transactional
    public IncomeDTO update(Long incomeId, IncomeUpdateDTO dto) {
        Long userId = userService.authenticated().getId();
        Income entity = repository.findById(incomeId)
                .orElseThrow(() -> new ResourceNotFoundException("Gasto não encontrado com ID " + incomeId));

        if (!userService.authenticated().isAdmin() ) {
            userId = userService.authenticated().getId();
            if (!Objects.equals(entity.getUser().getId(), userId)) {
                throw new UnauthorizedOperationException("O usuário " + userId + " não pode atualizar esse gasto, pois não é dele");
            }
        }

        copyDtoToEntity(dto, entity);
        entity = repository.save(entity);
        return new IncomeDTO(entity);
    }

    @Transactional
    public void delete(Long spendId) {
        Long userId = userService.authenticated().getId();
        Income entity = repository.findById(spendId)
                .orElseThrow(() -> new ResourceNotFoundException("Gasto não encontrado com ID " + spendId));

        if (!userService.authenticated().isAdmin() ) {
            userId = userService.authenticated().getId();
            if (!Objects.equals(entity.getUser().getId(), userId)) {
                throw new UnauthorizedOperationException("O usuário " + userId + " não pode deletar esse gasto, pois não é dele");
            }
        }

        repository.deleteById(spendId);
    }

    public void copyDtoToEntity(IncomeDTO dto, Income entity) {
        copyCommonFields(dto.getIncomeValue(), dto.getDescription(), dto.getIncomeDate(), dto.getCategoryId(), entity);
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID " + dto.getUserId()));
        entity.setUser(user);
    }

    public void copyDtoToEntity(IncomeUpdateDTO dto, Income entity) {
        copyCommonFields(dto.getIncomeValue(), dto.getDescription(), dto.getIncomeDate(), dto.getCategoryId(), entity);
    }

    private void copyCommonFields(Double incomeValue, String description, LocalDate incomeDate, Long categoryId, Income entity) {
        entity.setIncomeValue(incomeValue);
        entity.setDescription(description);
        entity.setIncomeDate(incomeDate);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com ID " + categoryId));
        if (category.getType().equals(CategoryType.SPEND)) {
            throw new InvalidCategoryException("A categoria com ID " + categoryId + " é de gasto, não de receita");
        }
        entity.setCategory(category);
    }

    private boolean isNotEmpty(String value) {
        return value != null && !value.isEmpty();
    }

    private LocalDate parseDate(String dateStr) {
        return isNotEmpty(dateStr) ? LocalDate.parse(dateStr, formatter) : null;
    }


}
