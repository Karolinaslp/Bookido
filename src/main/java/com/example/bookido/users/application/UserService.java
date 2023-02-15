package com.example.bookido.users.application;

import com.example.bookido.user.db.UserEntityRepository;
import com.example.bookido.user.domain.UserEntity;
import com.example.bookido.users.application.port.UserRegistrationUseCase;
import lombok.AllArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
class UserService implements UserRegistrationUseCase {

    private final UserEntityRepository repository;
    private final PasswordEncoder encoder;

    @Transactional
    @Override
    public RegisterResponse register(String username, String password) {
        if (repository.findByUsernameIgnoreCase(username).isPresent()) {
            return RegisterResponse.failure("Account already exists");
        }
        UserEntity entity = new UserEntity(username, encoder.encode(password));
        return RegisterResponse.success(repository.save(entity));
    }
}