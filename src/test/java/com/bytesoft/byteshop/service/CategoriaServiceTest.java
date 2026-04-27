package com.bytesoft.byteshop.service;

import com.bytesoft.byteshop.dto.request.CategoriaRequest;
import com.bytesoft.byteshop.dto.response.CategoriaResponse;
import com.bytesoft.byteshop.exception.RecursoNaoEncontradoException;
import com.bytesoft.byteshop.exception.RegraDeNegocioException;
import com.bytesoft.byteshop.model.Categoria;
import com.bytesoft.byteshop.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do CategoriaService")
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository repository;

    @InjectMocks
    private CategoriaService service;

    private Categoria categoria;
    private CategoriaRequest request;

    @BeforeEach
    void setUp() {
        categoria = Categoria.builder()
                .id(1L)
                .nome("Eletrônicos")
                .descricao("Produtos eletrônicos")
                .ativo(true)
                .build();

        request = new CategoriaRequest("Eletrônicos", "Produtos eletrônicos");
    }

    @Test
    @DisplayName("Deve criar categoria com sucesso")
    void deveCriarCategoriaComSucesso() {
        when(repository.existsByNome(any())).thenReturn(false);
        when(repository.save(any())).thenReturn(categoria);

        CategoriaResponse response = service.criar(request);

        assertThat(response).isNotNull();
        assertThat(response.getNome()).isEqualTo("Eletrônicos");
        assertThat(response.getDescricao()).isEqualTo("Produtos eletrônicos");
        verify(repository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar categoria com nome duplicado")
    void deveLancarExcecaoNomeDuplicado() {
        when(repository.existsByNome(any())).thenReturn(true);

        assertThatThrownBy(() -> service.criar(request))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Já existe uma categoria");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve listar categorias com paginação")
    void deveListarCategorias() {
        Page<Categoria> page = new PageImpl<>(List.of(categoria));
        when(repository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<CategoriaResponse> resultado = service.listar(PageRequest.of(0, 10));

        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getNome()).isEqualTo("Eletrônicos");
    }

    @Test
    @DisplayName("Deve buscar categoria por ID com sucesso")
    void deveBuscarPorId() {
        when(repository.findById(1L)).thenReturn(Optional.of(categoria));

        CategoriaResponse response = service.buscarPorId(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNome()).isEqualTo("Eletrônicos");
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar categoria inexistente")
    void deveLancarExcecaoCategoriaNaoEncontrada() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Categoria não encontrada");
    }

    @Test
    @DisplayName("Deve atualizar categoria com sucesso")
    void deveAtualizarCategoria() {
        CategoriaRequest novoRequest = new CategoriaRequest("Informática", "Produtos de informática");
        when(repository.findById(1L)).thenReturn(Optional.of(categoria));
        when(repository.existsByNome("Informática")).thenReturn(false);

        CategoriaResponse response = service.atualizar(1L, novoRequest);

        assertThat(response.getNome()).isEqualTo("Informática");
        assertThat(response.getDescricao()).isEqualTo("Produtos de informática");
    }

    @Test
    @DisplayName("Deve deletar categoria com sucesso")
    void deveDeletarCategoria() {
        when(repository.findById(1L)).thenReturn(Optional.of(categoria));
        doNothing().when(repository).delete(any());

        assertThatCode(() -> service.deletar(1L)).doesNotThrowAnyException();
        verify(repository, times(1)).delete(categoria);
    }
}