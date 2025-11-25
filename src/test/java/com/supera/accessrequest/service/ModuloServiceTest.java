package com.supera.accessrequest.service;

import com.supera.accessrequest.dto.ModuloResponse;
import com.supera.accessrequest.entity.Modulo;
import com.supera.accessrequest.repository.ModuloRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
@DisplayName("ModuloService - Testes Unitários")
class ModuloServiceTest {

    @Mock
    private ModuloRepository moduloRepository;

    @InjectMocks
    private ModuloService moduloService;

    private Modulo modulo1;
    private Modulo modulo2;
    private Modulo modulo3;

    @BeforeEach
    void setUp() {
        modulo1 = Modulo.builder()
                .id(1L)
                .nome("CRM")
                .descricao("Sistema de gestão de clientes")
                .ativo(Boolean.TRUE)
                .build();

        modulo2 = Modulo.builder()
                .id(2L)
                .nome("Financeiro")
                .descricao("Sistema financeiro")
                .ativo(Boolean.TRUE)
                .build();

        modulo3 = Modulo.builder()
                .id(3L)
                .nome("RH")
                .descricao("Sistema de recursos humanos")
                .ativo(Boolean.TRUE)
                .build();
    }

    @Test
    @DisplayName("Deve listar todos os módulos disponíveis com sucesso")
    void deveListarTodosModulosDisponiveisComSucesso() {
        // Arrange
        List<Modulo> modulos = Arrays.asList(modulo1, modulo2, modulo3);
        when(moduloRepository.findByAtivoTrue()).thenReturn(modulos);

        // Act
        List<ModuloResponse> resultado = moduloService.listarModulosDisponiveis();

        // Assert
        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        assertEquals("CRM", resultado.get(0).nome());
        assertEquals("Financeiro", resultado.get(1).nome());
        assertEquals("RH", resultado.get(2).nome());

        // Verify
        verify(moduloRepository).findByAtivoTrue();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver módulos ativos")
    void deveRetornarListaVaziaQuandoNaoHouverModulosAtivos() {
        // Arrange
        when(moduloRepository.findByAtivoTrue()).thenReturn(Arrays.asList());

        // Act
        List<ModuloResponse> resultado = moduloService.listarModulosDisponiveis();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        // Verify
        verify(moduloRepository).findByAtivoTrue();
    }

    @Test
    @DisplayName("Deve buscar módulo por ID com sucesso")
    void deveBuscarModuloPorIdComSucesso() {
        // Arrange
        when(moduloRepository.findById(eq(1L))).thenReturn(Optional.of(modulo1));

        // Act
        Modulo resultado = moduloService.buscarPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("CRM", resultado.getNome());
        assertEquals("Sistema de gestão de clientes", resultado.getDescricao());
        assertTrue(resultado.getAtivo());

        // Verify
        verify(moduloRepository).findById(eq(1L));
    }

    @Test
    @DisplayName("Deve lançar exceção quando módulo não é encontrado por ID")
    void deveLancarExcecaoQuandoModuloNaoEncontradoPorId() {
        // Arrange
        when(moduloRepository.findById(eq(999L))).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> moduloService.buscarPorId(999L));
        assertTrue(exception.getMessage().contains("Módulo não encontrado com ID: 999"));

        // Verify
        verify(moduloRepository).findById(eq(999L));
    }

    @Test
    @DisplayName("Deve buscar múltiplos módulos por IDs com sucesso")
    void deveBuscarMultiplosModulosPorIdsComSucesso() {
        // Arrange
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        List<Modulo> modulos = Arrays.asList(modulo1, modulo2, modulo3);
        when(moduloRepository.findAllByIdsAndAtivoTrue(eq(ids))).thenReturn(modulos);

        // Act
        List<Modulo> resultado = moduloService.buscarPorIds(ids);

        // Assert
        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        assertEquals("CRM", resultado.get(0).getNome());
        assertEquals("Financeiro", resultado.get(1).getNome());
        assertEquals("RH", resultado.get(2).getNome());

        // Verify
        verify(moduloRepository).findAllByIdsAndAtivoTrue(eq(ids));
    }

    @Test
    @DisplayName("Deve lançar exceção quando nem todos os módulos são encontrados")
    void deveLancarExcecaoQuandoNemTodosModulosSaoEncontrados() {
        // Arrange
        List<Long> ids = Arrays.asList(1L, 2L, 999L);
        List<Modulo> modulos = Arrays.asList(modulo1, modulo2); // Apenas 2 módulos encontrados
        when(moduloRepository.findAllByIdsAndAtivoTrue(eq(ids))).thenReturn(modulos);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> moduloService.buscarPorIds(ids));
        assertEquals("Um ou mais módulos não foram encontrados ou estão inativos", exception.getMessage());

        // Verify
        verify(moduloRepository).findAllByIdsAndAtivoTrue(eq(ids));
    }

    @Test
    @DisplayName("Deve lançar exceção quando nenhum módulo é encontrado")
    void deveLancarExcecaoQuandoNenhumModuloEncontrado() {
        // Arrange
        List<Long> ids = Arrays.asList(998L, 999L);
        when(moduloRepository.findAllByIdsAndAtivoTrue(eq(ids))).thenReturn(Arrays.asList());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> moduloService.buscarPorIds(ids));
        assertEquals("Um ou mais módulos não foram encontrados ou estão inativos", exception.getMessage());

        // Verify
        verify(moduloRepository).findAllByIdsAndAtivoTrue(eq(ids));
    }

    @Test
    @DisplayName("Deve buscar um único módulo por lista de IDs")
    void deveBuscarUnicoModuloPorListaDeIds() {
        // Arrange
        List<Long> ids = Arrays.asList(1L);
        List<Modulo> modulos = Arrays.asList(modulo1);
        when(moduloRepository.findAllByIdsAndAtivoTrue(eq(ids))).thenReturn(modulos);

        // Act
        List<Modulo> resultado = moduloService.buscarPorIds(ids);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("CRM", resultado.get(0).getNome());

        // Verify
        verify(moduloRepository).findAllByIdsAndAtivoTrue(eq(ids));
    }
}

