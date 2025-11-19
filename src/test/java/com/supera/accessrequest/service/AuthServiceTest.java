package com.supera.accessrequest.service;

import com.supera.accessrequest.dto.LoginRequest;
import com.supera.accessrequest.dto.LoginResponse;
import com.supera.accessrequest.entity.Usuario;
import com.supera.accessrequest.enums.Departamento;
import com.supera.accessrequest.repository.UsuarioRepository;
import com.supera.accessrequest.security.JwtTokenProvider;
import com.supera.accessrequest.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - Testes Unitários")
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private Usuario usuario;
    private UserDetailsImpl userDetails;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .nome("João Silva")
                .email("joao.silva@empresa.com")
                .senha("$2a$12$encoded_password")
                .departamento(Departamento.TI)
                .ativo(true)
                .build();

        userDetails = UserDetailsImpl.build(usuario);
        loginRequest = new LoginRequest("joao.silva@empresa.com", "senha123");
    }

    @Test
    @DisplayName("Deve realizar login com sucesso")
    void deveRealizarLoginComSucesso() {
        // Arrange
        String expectedToken = "jwt.token.here";
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginRequest.email(),
                loginRequest.senha()
        );

        when(authenticationManager.authenticate(eq(authToken))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(tokenProvider.generateToken(eq(authentication))).thenReturn(expectedToken);
        when(usuarioRepository.findById(eq(1L))).thenReturn(Optional.of(usuario));

        // Act
        LoginResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(expectedToken, response.token());
        assertEquals("Bearer", response.tipo());
        assertEquals(usuario.getId(), response.usuarioId());
        assertEquals(usuario.getNome(), response.nome());
        assertEquals(usuario.getEmail(), response.email());
        assertEquals("TI", response.departamento());

        // Verify
        verify(authenticationManager).authenticate(eq(authToken));
        verify(tokenProvider).generateToken(eq(authentication));
        verify(usuarioRepository).findById(eq(1L));
    }

    @Test
    @DisplayName("Deve lançar exceção quando credenciais são inválidas")
    void deveLancarExcecaoQuandoCredenciaisInvalidas() {
        // Arrange
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginRequest.email(),
                loginRequest.senha()
        );

        when(authenticationManager.authenticate(eq(authToken)))
                .thenThrow(new RuntimeException("Bad credentials"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authService.login(loginRequest));

        // Verify
        verify(authenticationManager).authenticate(eq(authToken));
        verify(tokenProvider, never()).generateToken(any());
        verify(usuarioRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não é encontrado após autenticação")
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        // Arrange
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginRequest.email(),
                loginRequest.senha()
        );

        when(authenticationManager.authenticate(eq(authToken))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(tokenProvider.generateToken(eq(authentication))).thenReturn("token");
        when(usuarioRepository.findById(eq(1L))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authService.login(loginRequest));

        // Verify
        verify(authenticationManager).authenticate(eq(authToken));
        verify(tokenProvider).generateToken(eq(authentication));
        verify(usuarioRepository).findById(eq(1L));
    }
}

