package com.example.flatscraper.auth;

import com.example.flatscraper.config.jwt.JwtService;
import com.example.flatscraper.config.jwt.RefreshTokenRequest;
import com.example.flatscraper.config.jwt.RefreshTokenResponse;
import com.example.flatscraper.exception.*;
import com.example.flatscraper.user.UserEntity;
import com.example.flatscraper.user.UserRepository;
import com.example.flatscraper.user.UserService;
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

    public AuthResponse register(RegisterRequest request) {
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

        return new AuthResponse(jwtToken, refreshToken);
    }

    public AuthResponse authenticate(AuthRequest request) {
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

        return new AuthResponse(jwtToken, refreshToken);
    }

    public RefreshTokenResponse refreshToken(
            RefreshTokenRequest refreshTokenRequest
    ) {
        final String username;
        final String refreshToken = refreshTokenRequest.refreshToken();

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

        return new RefreshTokenResponse(accessToken);
    }
}
