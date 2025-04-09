package com.example.flatscraper.service;

import com.example.flatscraper.entity.UserEntity;
import com.example.flatscraper.exception.*;
import com.example.flatscraper.dto.*;
import com.example.flatscraper.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            UserService userService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    public AuthResponseDto register(RegisterRequestDto request) {
        if (userService.existsByUsername(request.username()))
            throw new UsernameIsTakenException("This username is taken.");

        if (userService.existsByEmail(request.email()))
            throw new UserAlreadyExistsException("User with this email already exists.");

        UserEntity user = new UserEntity();
        user.setEmail(request.email());
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));

        UserEntity savedUser = userRepository.save(user);

        String jwtToken = jwtService.generateToken(savedUser.getUsername());
        String refreshToken = jwtService.generateRefreshToken(savedUser.getUsername());
        Cookie cookie = jwtService.crateCookie(refreshToken);

        return new AuthResponseDto(jwtToken, cookie);
    }

    public AuthResponseDto authenticate(AuthRequestDto request) {
        UserEntity user = userRepository
                .findByUsername(request.username())
                .orElseThrow(() -> new InvalidUsernameException("Username does not exist."));

        if (!passwordEncoder.matches(request.password(), user.getPassword()))
            throw new InvalidPasswordException("Invalid password.");

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        String jwtToken = jwtService.generateToken(user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());
        Cookie cookie = jwtService.crateCookie(refreshToken);

        return new AuthResponseDto(jwtToken, cookie);
    }

    public RefreshTokenResponseDto refreshToken(
            RefreshTokenRequestDto refreshTokenRequestDto
    ) {
        final String username;
        final String refreshToken = refreshTokenRequestDto.refreshToken();

        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new InvalidTokenException("Refresh token is missing");
        }

        username = jwtService.extractUsername(refreshToken);

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!jwtService.isTokenValid(refreshToken, user) || !jwtService.isRefreshToken(refreshToken)) {
            throw new InvalidTokenException("Refresh token is not valid");
        }

        String accessToken = jwtService.generateToken(user.getUsername());

        return new RefreshTokenResponseDto(accessToken);
    }
}
