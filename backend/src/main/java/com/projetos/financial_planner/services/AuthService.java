package com.projetos.financial_planner.services;

import com.projetos.financial_planner.dto.EmailDTO;
import com.projetos.financial_planner.dto.NewPasswordDTO;
import com.projetos.financial_planner.entities.PasswordRecover;
import com.projetos.financial_planner.entities.User;
import com.projetos.financial_planner.repositories.PasswordRecoverRepository;
import com.projetos.financial_planner.repositories.UserRepository;
import com.projetos.financial_planner.services.exceptions.ForbiddenException;
import com.projetos.financial_planner.services.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class AuthService {

    @Value("${email.password-recover.token.minutes}")
    private Long tokenExpirationMinutes;

    @Value("${email.password-recover.uri}")
    private String recoverUri;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordRecoverRepository repository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public void validateSelfOrAdmin(Long userId){
        User user = userService.authenticated();
        if (!user.hasRole("ROLE_ADMIN") && !Objects.equals(user.getId(), userId)){
            throw new ForbiddenException("Acesso negado para o usuário " + userId);
        }
    }

    @Transactional
    public void createRecoverToken(EmailDTO body) {
        User user = userRepository.findByEmail(body.getEmail());
        if (user == null) {
            throw new ResourceNotFoundException("Email não encontrado: " + body.getEmail());
        }

        String token = UUID.randomUUID().toString();

        PasswordRecover passwordRecover = new PasswordRecover();
        passwordRecover.setEmail(body.getEmail());
        passwordRecover.setToken(token);
        passwordRecover.setExpiration(Instant.now().plusSeconds(tokenExpirationMinutes * 60));

        repository.save(passwordRecover);

        String emailBody = "Para recuperar sua senha, clique no link abaixo:\n"
                + recoverUri + token
                + "\n\nEste link expira em " + tokenExpirationMinutes + " minutos.";

        emailService.sendEmail(body.getEmail(), "Recuperação de Senha - Financial Planner", emailBody);

    }

    public void newPassword(@Valid NewPasswordDTO body) {
        List<PasswordRecover> validToken = repository.searchValidTokens(body.getToken(), Instant.now());
        if (validToken.isEmpty()) {
            throw new ResourceNotFoundException("Token inválido ou expirado");
        }

        User user = userRepository.findByEmail(validToken.get(0).getEmail());
        if (user == null) {
            throw new ResourceNotFoundException("Usuário não encontrado para o email: " + validToken.get(0).getEmail());
        }
        user.setPassword(passwordEncoder.encode(body.getPassword()));
        userRepository.save(user);
        repository.deleteAllByEmail(validToken.get(0).getEmail());
    }
}