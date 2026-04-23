package com.bytesoft.byteshop.service;

import com.bytesoft.byteshop.dto.AuthResponse;
import com.bytesoft.byteshop.dto.LoginRequest;
import com.bytesoft.byteshop.dto.RegisterRequest;
import com.bytesoft.byteshop.exception.RegraDeNegocioException;
import com.bytesoft.byteshop.model.Role;
import com.bytesoft.byteshop.model.Usuario;
import com.bytesoft.byteshop.repository.UsuarioRepository;
import com.bytesoft.byteshop.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse registrar(RegisterRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new RegraDeNegocioException("Email já cadastrado: " + request.getEmail());
        }

        Usuario usuario = Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .role(Role.USER)
                .build();

        repository.save(usuario);
        return new AuthResponse(jwtService.gerarToken(usuario));
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
        );
        Usuario usuario = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RegraDeNegocioException("Usuário não encontrado"));
        return new AuthResponse(jwtService.gerarToken(usuario));
    }
}