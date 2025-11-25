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
import static org.mockito.Mockito.mockStatic;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
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
        verify(tokenProvider, never()).generateToken(eq(authentication));
        verify(usuarioRepository, never()).findById(eq(1L));
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

    @Test
    @DisplayName("Deve retornar ID do usuário logado com sucesso")
    void deveRetornarIdDoUsuarioLogado() {
        // Arrange
        try (var mockedSecurityContext = mockStatic(org.springframework.security.core.context.SecurityContextHolder.class)) {
            org.springframework.security.core.context.SecurityContext securityContext = mock(org.springframework.security.core.context.SecurityContext.class);
            
            mockedSecurityContext.when(org.springframework.security.core.context.SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetails);

            // Act
            Long usuarioId = authService.getUsuarioLogadoId();

            // Assert
            assertNotNull(usuarioId);
            assertEquals(1L, usuarioId);
        }
    }

    @Test
    @DisplayName("Deve lançar exceção quando não há usuário autenticado ao buscar ID")
    void deveLancarExcecaoQuandoNaoHaUsuarioAutenticadoAoBuscarId() {
        // Arrange
        try (var mockedSecurityContext = mockStatic(org.springframework.security.core.context.SecurityContextHolder.class)) {
            org.springframework.security.core.context.SecurityContext securityContext = mock(org.springframework.security.core.context.SecurityContext.class);
            
            mockedSecurityContext.when(org.springframework.security.core.context.SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(null);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, 
                    () -> authService.getUsuarioLogadoId());
            assertEquals("Usuário não autenticado", exception.getMessage());
        }
    }

    @Test
    @DisplayName("Deve retornar UserDetails do usuário logado com sucesso")
    void deveRetornarUserDetailsDoUsuarioLogado() {
        // Arrange
        try (var mockedSecurityContext = mockStatic(org.springframework.security.core.context.SecurityContextHolder.class)) {
            org.springframework.security.core.context.SecurityContext securityContext = mock(org.springframework.security.core.context.SecurityContext.class);
            
            mockedSecurityContext.when(org.springframework.security.core.context.SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetails);

            // Act
            UserDetailsImpl result = authService.getUsuarioLogado();

            // Assert
            assertNotNull(result);
            assertEquals(userDetails.getId(), result.getId());
            assertEquals(userDetails.getUsername(), result.getUsername());
        }
    }

    @Test
    @DisplayName("Deve lançar exceção quando não há usuário autenticado ao buscar UserDetails")
    void deveLancarExcecaoQuandoNaoHaUsuarioAutenticadoAoBuscarUserDetails() {
        // Arrange
        try (var mockedSecurityContext = mockStatic(org.springframework.security.core.context.SecurityContextHolder.class)) {
            org.springframework.security.core.context.SecurityContext securityContext = mock(org.springframework.security.core.context.SecurityContext.class);
            
            mockedSecurityContext.when(org.springframework.security.core.context.SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(null);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, 
                    () -> authService.getUsuarioLogado());
            assertEquals("Usuário não autenticado", exception.getMessage());
        }
    }
}

