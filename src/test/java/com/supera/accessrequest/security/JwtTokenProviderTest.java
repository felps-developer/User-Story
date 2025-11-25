package com.supera.accessrequest.security;

import io.jsonwebtoken.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtTokenProvider - Testes Unitários")
class JwtTokenProviderTest {

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    private static final String JWT_SECRET = "mySecretKey12345678901234567890123456789012345678901234567890";
    private static final long JWT_EXPIRATION = 900000L; // 15 minutos em ms

    private UserDetailsImpl userDetails;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        // Configurar valores usando ReflectionTestUtils
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", JWT_SECRET);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", JWT_EXPIRATION);
        
        // Inicializar o provider
        jwtTokenProvider.init();

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

        authentication = mock(Authentication.class);
        lenient().when(authentication.getPrincipal()).thenReturn(userDetails);
    }

    @Test
    @DisplayName("Deve gerar token JWT a partir de Authentication com sucesso")
    void deveGerarTokenJwtAPartirDeAuthentication() {
        // Act
        String token = jwtTokenProvider.generateToken(authentication);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT tem 3 partes separadas por ponto

        // Verify
        verify(authentication).getPrincipal();
    }

    @Test
    @DisplayName("Deve gerar token JWT a partir de userId com sucesso")
    void deveGerarTokenJwtAPartirDeUserId() {
        // Arrange
        Long userId = 1L;

        // Act
        String token = jwtTokenProvider.generateTokenFromUserId(userId);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    @DisplayName("Deve extrair userId de token JWT válido")
    void deveExtrairUserIdDeTokenJwtValido() {
        // Arrange
        Long expectedUserId = 1L;
        String token = jwtTokenProvider.generateTokenFromUserId(expectedUserId);

        // Act
        Long userId = jwtTokenProvider.getUserIdFromJWT(token);

        // Assert
        assertNotNull(userId);
        assertEquals(expectedUserId, userId);
    }

    @Test
    @DisplayName("Deve validar token JWT válido retornando true")
    void deveValidarTokenJwtValido() {
        // Arrange
        String token = jwtTokenProvider.generateToken(authentication);

        // Act
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Deve retornar false para token com assinatura inválida")
    void deveRetornarFalseParaTokenComAssinaturaInvalida() {
        // Arrange
        String invalidToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxIn0.invalid_signature";

        // Act
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Deve retornar false para token malformado")
    void deveRetornarFalseParaTokenMalformado() {
        // Arrange
        String malformedToken = "not.a.valid.jwt.token";

        // Act
        boolean isValid = jwtTokenProvider.validateToken(malformedToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Deve retornar false para token expirado")
    void deveRetornarFalseParaTokenExpirado() {
        // Arrange
        // Criar token expirado manualmente
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() - 1000); // Token expirado há 1 segundo
        
        String expiredToken = Jwts.builder()
                .subject("1")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(JWT_SECRET.getBytes()))
                .compact();

        // Act
        boolean isValid = jwtTokenProvider.validateToken(expiredToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Deve retornar false para token vazio")
    void deveRetornarFalseParaTokenVazio() {
        // Arrange
        String emptyToken = "";

        // Act
        boolean isValid = jwtTokenProvider.validateToken(emptyToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Deve retornar false para token null")
    void deveRetornarFalseParaTokenNull() {
        // Act
        boolean isValid = jwtTokenProvider.validateToken(null);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Deve gerar tokens diferentes para usuários diferentes")
    void deveGerarTokensDiferentesParaUsuariosDiferentes() {
        // Arrange
        UserDetailsImpl user2 = UserDetailsImpl.build(
                com.supera.accessrequest.entity.Usuario.builder()
                        .id(2L)
                        .nome("Maria Santos")
                        .email("maria@empresa.com")
                        .senha("senha")
                        .departamento(com.supera.accessrequest.enums.Departamento.FINANCEIRO)
                        .ativo(true)
                        .build()
        );
        
        Authentication auth1 = mock(Authentication.class);
        Authentication auth2 = mock(Authentication.class);
        when(auth1.getPrincipal()).thenReturn(userDetails);
        when(auth2.getPrincipal()).thenReturn(user2);

        // Act
        String token1 = jwtTokenProvider.generateToken(auth1);
        String token2 = jwtTokenProvider.generateToken(auth2);

        // Assert
        assertNotEquals(token1, token2);
    }

    @Test
    @DisplayName("Deve extrair userId correto de token gerado por generateToken")
    void deveExtrairUserIdCorretoDeTokenGeradoPorGenerateToken() {
        // Arrange
        String token = jwtTokenProvider.generateToken(authentication);

        // Act
        Long userId = jwtTokenProvider.getUserIdFromJWT(token);

        // Assert
        assertEquals(userDetails.getId(), userId);
    }

    @Test
    @DisplayName("Deve gerar token com expiração correta")
    void deveGerarTokenComExpiracaoCorreta() {
        // Arrange
        String token = jwtTokenProvider.generateToken(authentication);

        // Act
        Claims claims = Jwts.parser()
                .verifyWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(JWT_SECRET.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Date expiration = claims.getExpiration();
        Date issuedAt = claims.getIssuedAt();

        // Assert
        assertNotNull(expiration);
        assertNotNull(issuedAt);
        // Expiração deve ser aproximadamente 15 minutos após issuedAt
        long expirationTime = expiration.getTime() - issuedAt.getTime();
        assertTrue(expirationTime >= JWT_EXPIRATION - 1000); // Margem de 1 segundo
        assertTrue(expirationTime <= JWT_EXPIRATION + 1000);
    }
}

