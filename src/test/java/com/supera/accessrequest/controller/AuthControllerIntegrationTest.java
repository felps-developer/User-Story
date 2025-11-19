package com.supera.accessrequest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supera.accessrequest.dto.LoginRequest;
import com.supera.accessrequest.entity.Usuario;
import com.supera.accessrequest.enums.Departamento;
import com.supera.accessrequest.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("AuthController - Testes de Integração")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
        
        usuario = Usuario.builder()
                .nome("João Silva")
                .email("joao.silva@empresa.com")
                .senha(passwordEncoder.encode("senha123"))
                .departamento(Departamento.TI)
                .ativo(true)
                .build();
        
        usuarioRepository.save(usuario);
    }

    @Test
    @DisplayName("Deve realizar login com credenciais válidas")
    void deveRealizarLoginComCredenciaisValidas() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("joao.silva@empresa.com", "senha123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.usuarioId").value(usuario.getId()))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao.silva@empresa.com"))
                .andExpect(jsonPath("$.departamento").value("TI"));
    }

    @Test
    @DisplayName("Deve retornar 401 para credenciais inválidas")
    void deveRetornar401ParaCredenciaisInvalidas() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("joao.silva@empresa.com", "senhaErrada");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve retornar 401 para email não cadastrado")
    void deveRetornar401ParaEmailNaoCadastrado() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("email.nao.existe@empresa.com", "senha123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve retornar 400 para request inválido")
    void deveRetornar400ParaRequestInvalido() throws Exception {
        // Arrange
        String invalidJson = "{\"email\": \"\", \"senha\": \"\"}";

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Não deve fazer login com usuário inativo")
    void naoDeveFazerLoginComUsuarioInativo() throws Exception {
        // Arrange
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
        
        LoginRequest loginRequest = new LoginRequest("joao.silva@empresa.com", "senha123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}

