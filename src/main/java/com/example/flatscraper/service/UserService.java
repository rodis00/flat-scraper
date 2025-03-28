package com.example.flatscraper.service;

import com.example.flatscraper.entity.UserEntity;
import com.example.flatscraper.exception.EntityNotFoundException;
import com.example.flatscraper.exception.UserAlreadyExistsException;
import com.example.flatscraper.exception.UsernameIsTakenException;
import com.example.flatscraper.record.UserDto;
import com.example.flatscraper.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity getUserByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));
    }

    public UserDto updateUser(
            String username,
            UserEntity user
    ) {
        if (existsByEmail(user.getEmail()))
            throw new UserAlreadyExistsException("User with this email already exists.");

        if (existsByUsername(user.getUsername()))
            throw new UsernameIsTakenException("This username is taken.");

        UserEntity existingUser = getUserByUsername(username);

        if (Objects.nonNull(user.getEmail()))
            existingUser.setEmail(user.getEmail());

        if (Objects.nonNull(user.getUsername()))
            existingUser.setUsername(user.getUsername());

        if (Objects.nonNull(user.getPassword()))
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(existingUser);

        return new UserDto(existingUser.getUsername(), existingUser.getEmail());
    }

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUserByUsername(String username) {
        UserEntity user = getUserByUsername(username);
        userRepository.delete(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public void checkIfUserExists(String username) {
        if (!userRepository.existsByUsername(username))
            throw new EntityNotFoundException("User not found.");
    }
}
