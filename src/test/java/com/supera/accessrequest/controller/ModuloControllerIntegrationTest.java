package com.supera.accessrequest.controller;

import com.supera.accessrequest.entity.Modulo;
import com.supera.accessrequest.entity.Usuario;
import com.supera.accessrequest.enums.Departamento;
import com.supera.accessrequest.repository.ModuloRepository;
import com.supera.accessrequest.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("ModuloController - Testes de Integração")
class ModuloControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModuloRepository moduloRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        moduloRepository.deleteAll();
        usuarioRepository.deleteAll();

        usuario = Usuario.builder()
                .nome("João Silva")
                .email("joao.silva@empresa.com")
                .senha(passwordEncoder.encode("senha123"))
                .departamento(Departamento.TI)
                .ativo(true)
                .build();
        usuarioRepository.save(usuario);

        Modulo modulo1 = Modulo.builder()
                .nome("CRM")
                .descricao("Sistema de gestão de clientes")
                .ativo(Boolean.TRUE)
                .build();

        Modulo modulo2 = Modulo.builder()
                .nome("Financeiro")
                .descricao("Sistema financeiro")
                .ativo(Boolean.TRUE)
                .build();

        Modulo modulo3 = Modulo.builder()
                .nome("RH")
                .descricao("Sistema de recursos humanos")
                .ativo(Boolean.FALSE)
                .build();

        moduloRepository.save(modulo1);
        moduloRepository.save(modulo2);
        moduloRepository.save(modulo3);
    }

    @Test
    @DisplayName("Deve listar módulos disponíveis com autenticação")
    @WithMockUser(username = "joao.silva@empresa.com")
    void deveListarModulosDisponiveisComAutenticacao() throws Exception {
        mockMvc.perform(get("/api/modulos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").exists())
                .andExpect(jsonPath("$[0].descricao").exists());
    }

    @Test
    @DisplayName("Deve retornar 401 sem autenticação")
    void deveRetornar401SemAutenticacao() throws Exception {
        mockMvc.perform(get("/api/modulos"))
                .andExpect(status().isUnauthorized());
    }
}

