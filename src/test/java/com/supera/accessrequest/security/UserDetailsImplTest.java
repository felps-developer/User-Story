package com.supera.accessrequest.security;

import com.supera.accessrequest.entity.Usuario;
import com.supera.accessrequest.enums.Departamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserDetailsImpl - Testes Unitários")
class UserDetailsImplTest {

    private Usuario usuario;
    private UserDetailsImpl userDetails;

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
    }

    @Test
    @DisplayName("Deve construir UserDetailsImpl a partir de Usuario")
    void deveConstruirUserDetailsImplAPartirDeUsuario() {
        // Act
        UserDetailsImpl result = UserDetailsImpl.build(usuario);

        // Assert
        assertNotNull(result);
        assertEquals(usuario.getId(), result.getId());
        assertEquals(usuario.getNome(), result.getNome());
        assertEquals(usuario.getEmail(), result.getEmail());
        assertEquals(usuario.getSenha(), result.getSenha());
        assertEquals(usuario.getDepartamento().name(), result.getDepartamento());
        assertEquals(usuario.getAtivo(), result.isAtivo());
    }

    @Test
    @DisplayName("Deve retornar authorities baseadas no departamento")
    void deveRetornarAuthoritiesBaseadasNoDepartamento() {
        // Act
        var authorities = userDetails.getAuthorities();

        // Assert
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_TI")));
    }

    @Test
    @DisplayName("Deve retornar senha corretamente")
    void deveRetornarSenhaCorretamente() {
        // Act
        String password = userDetails.getPassword();

        // Assert
        assertEquals(usuario.getSenha(), password);
    }

    @Test
    @DisplayName("Deve retornar email como username")
    void deveRetornarEmailComoUsername() {
        // Act
        String username = userDetails.getUsername();

        // Assert
        assertEquals(usuario.getEmail(), username);
    }

    @Test
    @DisplayName("Deve retornar true para isAccountNonExpired")
    void deveRetornarTrueParaIsAccountNonExpired() {
        // Act & Assert
        assertTrue(userDetails.isAccountNonExpired());
    }

    @Test
    @DisplayName("Deve retornar true para isAccountNonLocked")
    void deveRetornarTrueParaIsAccountNonLocked() {
        // Act & Assert
        assertTrue(userDetails.isAccountNonLocked());
    }

    @Test
    @DisplayName("Deve retornar true para isCredentialsNonExpired")
    void deveRetornarTrueParaIsCredentialsNonExpired() {
        // Act & Assert
        assertTrue(userDetails.isCredentialsNonExpired());
    }

    @Test
    @DisplayName("Deve retornar status ativo para isEnabled")
    void deveRetornarStatusAtivoParaIsEnabled() {
        // Act & Assert
        assertEquals(usuario.getAtivo(), userDetails.isEnabled());
    }

    @Test
    @DisplayName("Deve retornar false para isEnabled quando usuário está inativo")
    void deveRetornarFalseParaIsEnabledQuandoUsuarioInativo() {
        // Arrange
        usuario.setAtivo(false);
        UserDetailsImpl inactiveUser = UserDetailsImpl.build(usuario);

        // Act & Assert
        assertFalse(inactiveUser.isEnabled());
    }

    @Test
    @DisplayName("Deve retornar role correta para diferentes departamentos")
    void deveRetornarRoleCorretaParaDiferentesDepartamentos() {
        // Arrange & Act
        Usuario financeiro = Usuario.builder().departamento(Departamento.FINANCEIRO).build();
        Usuario rh = Usuario.builder().departamento(Departamento.RH).build();
        
        UserDetailsImpl financeiroDetails = UserDetailsImpl.build(financeiro);
        UserDetailsImpl rhDetails = UserDetailsImpl.build(rh);

        // Assert
        assertTrue(financeiroDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_FINANCEIRO")));
        assertTrue(rhDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_RH")));
    }
}

