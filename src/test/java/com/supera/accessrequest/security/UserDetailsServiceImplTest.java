package com.supera.accessrequest.security;

import com.supera.accessrequest.entity.Usuario;
import com.supera.accessrequest.enums.Departamento;
import com.supera.accessrequest.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDetailsServiceImpl - Testes Unitários")
class UserDetailsServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private Usuario usuario;

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
    }

    @Test
    @DisplayName("Deve carregar usuário por email com sucesso")
    void deveCarregarUsuarioPorEmailComSucesso() {
        // Arrange
        String email = "joao.silva@empresa.com";
        when(usuarioRepository.findByEmailAndAtivoTrue(eq(email))).thenReturn(Optional.of(usuario));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername(email);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getUsername());
        assertEquals(usuario.getSenha(), result.getPassword());
        assertTrue(result.isEnabled());

        // Verify
        verify(usuarioRepository).findByEmailAndAtivoTrue(eq(email));
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException quando usuário não encontrado por email")
    void deveLancarUsernameNotFoundExceptionQuandoUsuarioNaoEncontradoPorEmail() {
        // Arrange
        String email = "naoexiste@empresa.com";
        when(usuarioRepository.findByEmailAndAtivoTrue(eq(email))).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(email));
        
        assertTrue(exception.getMessage().contains("Usuário não encontrado com email: " + email));

        // Verify
        verify(usuarioRepository).findByEmailAndAtivoTrue(eq(email));
    }

    @Test
    @DisplayName("Deve carregar usuário por ID com sucesso")
    void deveCarregarUsuarioPorIdComSucesso() {
        // Arrange
        Long userId = 1L;
        when(usuarioRepository.findById(eq(userId))).thenReturn(Optional.of(usuario));

        // Act
        UserDetails result = userDetailsService.loadUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(usuario.getEmail(), result.getUsername());
        assertEquals(usuario.getSenha(), result.getPassword());
        assertTrue(result.isEnabled());

        // Verify
        verify(usuarioRepository).findById(eq(userId));
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException quando usuário não encontrado por ID")
    void deveLancarUsernameNotFoundExceptionQuandoUsuarioNaoEncontradoPorId() {
        // Arrange
        Long userId = 999L;
        when(usuarioRepository.findById(eq(userId))).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserById(userId));
        
        assertTrue(exception.getMessage().contains("Usuário não encontrado com ID: " + userId));

        // Verify
        verify(usuarioRepository).findById(eq(userId));
    }

    @Test
    @DisplayName("Deve retornar UserDetailsImpl ao carregar por email")
    void deveRetornarUserDetailsImplAoCarregarPorEmail() {
        // Arrange
        String email = "joao.silva@empresa.com";
        when(usuarioRepository.findByEmailAndAtivoTrue(eq(email))).thenReturn(Optional.of(usuario));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername(email);

        // Assert
        assertTrue(result instanceof UserDetailsImpl);
        UserDetailsImpl userDetails = (UserDetailsImpl) result;
        assertEquals(usuario.getId(), userDetails.getId());
        assertEquals(usuario.getNome(), userDetails.getNome());
    }

    @Test
    @DisplayName("Deve retornar UserDetailsImpl ao carregar por ID")
    void deveRetornarUserDetailsImplAoCarregarPorId() {
        // Arrange
        Long userId = 1L;
        when(usuarioRepository.findById(eq(userId))).thenReturn(Optional.of(usuario));

        // Act
        UserDetails result = userDetailsService.loadUserById(userId);

        // Assert
        assertTrue(result instanceof UserDetailsImpl);
        UserDetailsImpl userDetails = (UserDetailsImpl) result;
        assertEquals(usuario.getId(), userDetails.getId());
        assertEquals(usuario.getNome(), userDetails.getNome());
    }
}

