package com.bytesoft.byteshop.service;

import com.bytesoft.byteshop.dto.request.LoginRequest;
import com.bytesoft.byteshop.dto.request.RegisterRequest;
import com.bytesoft.byteshop.dto.response.AuthResponse;
import com.bytesoft.byteshop.exception.RegraDeNegocioException;
import com.bytesoft.byteshop.model.Role;
import com.bytesoft.byteshop.model.Usuario;
import com.bytesoft.byteshop.repository.UsuarioRepository;
import com.bytesoft.byteshop.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do AuthService")
class AuthServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private Usuario usuario;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .nome("Caio Silva")
                .email("caio@email.com")
                .senha("senha123")
                .role(Role.USER)
                .build();

        registerRequest = new RegisterRequest("Caio Silva", "caio@email.com", "senha123");
        loginRequest = new LoginRequest("caio@email.com", "senha123");
    }

    @Test
    @DisplayName("Deve registrar usuário com sucesso")
    void deveRegistrarUsuarioComSucesso() {
        when(repository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("senhaCriptografada");
        when(repository.save(any())).thenReturn(usuario);
        when(jwtService.gerarToken(any())).thenReturn("token-jwt");

        AuthResponse response = authService.registrar(registerRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("token-jwt");
        verify(repository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar email duplicado")
    void deveLancarExcecaoEmailDuplicado() {
        when(repository.existsByEmail(any())).thenReturn(true);

        assertThatThrownBy(() -> authService.registrar(registerRequest))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Email já cadastrado");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve realizar login com sucesso")
    void deveRealizarLoginComSucesso() {
        when(authenticationManager.authenticate(any())).thenReturn(
                new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities())
        );
        when(repository.findByEmail(any())).thenReturn(Optional.of(usuario));
        when(jwtService.gerarToken(any())).thenReturn("token-jwt");

        AuthResponse response = authService.login(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("token-jwt");
    }

    @Test
    @DisplayName("Deve lançar exceção ao logar com email inexistente")
    void deveLancarExcecaoLoginEmailInexistente() {
        when(authenticationManager.authenticate(any())).thenReturn(
                new UsernamePasswordAuthenticationToken(usuario, null)
        );
        when(repository.findByEmail(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Usuário não encontrado");
    }
}