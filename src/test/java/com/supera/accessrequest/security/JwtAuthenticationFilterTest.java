package com.supera.accessrequest.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter - Testes Unitários")
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private UserDetails userDetails;
    private String validToken;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        validToken = "valid.jwt.token";
        
        userDetails = UserDetailsImpl.build(
                com.supera.accessrequest.entity.Usuario.builder()
                        .id(1L)
                        .nome("João Silva")
                        .email("joao@empresa.com")
                        .senha("senha")
                        .departamento(com.supera.accessrequest.enums.Departamento.TI)
                        .ativo(true)
                        .build()
        );
    }

    @Test
    @DisplayName("Deve processar requisição com token JWT válido e configurar autenticação")
    void deveProcessarRequisicaoComTokenJwtValido() throws ServletException, IOException {
        // Arrange
        String bearerToken = "Bearer " + validToken;
        when(request.getHeader(eq("Authorization"))).thenReturn(bearerToken);
        when(tokenProvider.validateToken(eq(validToken))).thenReturn(true);
        when(tokenProvider.getUserIdFromJWT(eq(validToken))).thenReturn(1L);
        when(userDetailsService.loadUserById(eq(1L))).thenReturn(userDetails);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        
        // Verify
        verify(request).getHeader(eq("Authorization"));
        verify(tokenProvider).validateToken(eq(validToken));
        verify(tokenProvider).getUserIdFromJWT(eq(validToken));
        verify(userDetailsService).loadUserById(eq(1L));
        verify(filterChain).doFilter(eq(request), eq(response));
    }

    @Test
    @DisplayName("Deve continuar sem autenticação quando não há token")
    void deveContinuarSemAutenticacaoQuandoNaoHaToken() throws ServletException, IOException {
        // Arrange
        when(request.getHeader(eq("Authorization"))).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // Verify
        verify(request).getHeader(eq("Authorization"));
        verify(tokenProvider, never()).validateToken(anyString());
        verify(filterChain).doFilter(eq(request), eq(response));
    }

    @Test
    @DisplayName("Deve continuar sem autenticação quando token é inválido")
    void deveContinuarSemAutenticacaoQuandoTokenInvalido() throws ServletException, IOException {
        // Arrange
        String bearerToken = "Bearer invalid.token";
        when(request.getHeader(eq("Authorization"))).thenReturn(bearerToken);
        when(tokenProvider.validateToken(eq("invalid.token"))).thenReturn(false);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // Verify
        verify(request).getHeader(eq("Authorization"));
        verify(tokenProvider).validateToken(eq("invalid.token"));
        verify(tokenProvider, never()).getUserIdFromJWT(anyString());
        verify(userDetailsService, never()).loadUserById(anyLong());
        verify(filterChain).doFilter(eq(request), eq(response));
    }

    @Test
    @DisplayName("Deve continuar sem autenticação quando Authorization header não começa com Bearer")
    void deveContinuarSemAutenticacaoQuandoHeaderNaoComecaComBearer() throws ServletException, IOException {
        // Arrange
        when(request.getHeader(eq("Authorization"))).thenReturn("InvalidFormat token");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // Verify
        verify(request).getHeader(eq("Authorization"));
        verify(tokenProvider, never()).validateToken(anyString());
        verify(filterChain).doFilter(eq(request), eq(response));
    }

    @Test
    @DisplayName("Deve tratar exceção durante processamento e continuar o filtro")
    void deveTratarExcecaoDuranteProcessamento() throws ServletException, IOException {
        // Arrange
        String bearerToken = "Bearer " + validToken;
        when(request.getHeader(eq("Authorization"))).thenReturn(bearerToken);
        when(tokenProvider.validateToken(eq(validToken))).thenReturn(true);
        when(tokenProvider.getUserIdFromJWT(eq(validToken))).thenThrow(new RuntimeException("Erro ao processar token"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        // Deve continuar mesmo com erro
        verify(filterChain).doFilter(eq(request), eq(response));
    }

    @Test
    @DisplayName("Deve extrair token corretamente do header Authorization")
    void deveExtrairTokenCorretamenteDoHeader() throws ServletException, IOException {
        // Arrange
        String bearerToken = "Bearer " + validToken;
        when(request.getHeader(eq("Authorization"))).thenReturn(bearerToken);
        when(tokenProvider.validateToken(eq(validToken))).thenReturn(true);
        when(tokenProvider.getUserIdFromJWT(eq(validToken))).thenReturn(1L);
        when(userDetailsService.loadUserById(eq(1L))).thenReturn(userDetails);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verify
        verify(request).getHeader(eq("Authorization"));
        verify(tokenProvider).validateToken(eq(validToken)); // Verifica que o token extraído foi usado
    }

    @Test
    @DisplayName("Deve processar requisição com token válido mas usuário não encontrado")
    void deveProcessarRequisicaoComTokenValidoMasUsuarioNaoEncontrado() throws ServletException, IOException {
        // Arrange
        String bearerToken = "Bearer " + validToken;
        when(request.getHeader(eq("Authorization"))).thenReturn(bearerToken);
        when(tokenProvider.validateToken(eq(validToken))).thenReturn(true);
        when(tokenProvider.getUserIdFromJWT(eq(validToken))).thenReturn(999L);
        when(userDetailsService.loadUserById(eq(999L)))
                .thenThrow(new org.springframework.security.core.userdetails.UsernameNotFoundException("Usuário não encontrado"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        // Deve continuar mesmo com erro
        verify(filterChain).doFilter(eq(request), eq(response));
    }

    @Test
    @DisplayName("Deve processar requisição com Authorization header vazio")
    void deveProcessarRequisicaoComAuthorizationHeaderVazio() throws ServletException, IOException {
        // Arrange
        when(request.getHeader(eq("Authorization"))).thenReturn("");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(tokenProvider, never()).validateToken(anyString());
        verify(filterChain).doFilter(eq(request), eq(response));
    }

    @Test
    @DisplayName("Deve processar requisição com Authorization header contendo apenas Bearer")
    void deveProcessarRequisicaoComAuthorizationHeaderApenasBearer() throws ServletException, IOException {
        // Arrange
        when(request.getHeader(eq("Authorization"))).thenReturn("Bearer ");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(tokenProvider, never()).validateToken(anyString());
        verify(filterChain).doFilter(eq(request), eq(response));
    }
}

