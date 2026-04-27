package com.bytesoft.byteshop.service;

import com.bytesoft.byteshop.dto.request.ProdutoRequest;
import com.bytesoft.byteshop.dto.response.ProdutoResponse;
import com.bytesoft.byteshop.exception.RecursoNaoEncontradoException;
import com.bytesoft.byteshop.exception.RegraDeNegocioException;
import com.bytesoft.byteshop.model.Categoria;
import com.bytesoft.byteshop.model.Produto;
import com.bytesoft.byteshop.repository.ProdutoRepository;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ProdutoService")
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository repository;

    @Mock
    private CategoriaService categoriaService;

    @InjectMocks
    private ProdutoService service;

    private Produto produto;
    private Categoria categoria;
    private ProdutoRequest request;

    @BeforeEach
    void setUp() {
        categoria = Categoria.builder()
                .id(1L)
                .nome("Eletrônicos")
                .build();

        produto = Produto.builder()
                .id(1L)
                .nome("Notebook")
                .descricao("Notebook gamer")
                .preco(new BigDecimal("3500.00"))
                .estoque(10)
                .ativo(true)
                .categoria(categoria)
                .build();

        request = new ProdutoRequest("Notebook", "Notebook gamer",
                new BigDecimal("3500.00"), 10, 1L);
    }

    @Test
    @DisplayName("Deve criar produto com sucesso")
    void deveCriarProdutoComSucesso() {
        when(categoriaService.buscarEntidade(1L)).thenReturn(categoria);
        when(repository.existsByNomeAndCategoriaId(any(), any())).thenReturn(false);
        when(repository.save(any())).thenReturn(produto);

        ProdutoResponse response = service.criar(request);

        assertThat(response).isNotNull();
        assertThat(response.getNome()).isEqualTo("Notebook");
        assertThat(response.getPreco()).isEqualByComparingTo("3500.00");
        verify(repository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar produto com nome duplicado na categoria")
    void deveLancarExcecaoNomeDuplicado() {
        when(categoriaService.buscarEntidade(1L)).thenReturn(categoria);
        when(repository.existsByNomeAndCategoriaId(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> service.criar(request))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Já existe um produto");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve listar produtos ativos")
    void deveListarProdutosAtivos() {
        Page<Produto> page = new PageImpl<>(List.of(produto));
        when(repository.findByAtivoTrue(any())).thenReturn(page);

        Page<ProdutoResponse> resultado = service.listar(null, PageRequest.of(0, 10));

        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getNome()).isEqualTo("Notebook");
    }

    @Test
    @DisplayName("Deve listar produtos por categoria")
    void deveListarProdutosPorCategoria() {
        Page<Produto> page = new PageImpl<>(List.of(produto));
        when(repository.findByCategoriaIdAndAtivoTrue(eq(1L), any())).thenReturn(page);

        Page<ProdutoResponse> resultado = service.listar(1L, PageRequest.of(0, 10));

        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Deve buscar produto por ID")
    void deveBuscarPorId() {
        when(repository.findById(1L)).thenReturn(Optional.of(produto));

        ProdutoResponse response = service.buscarPorId(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar produto inexistente")
    void deveLancarExcecaoProdutoNaoEncontrado() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Produto não encontrado");
    }

    @Test
    @DisplayName("Deve fazer soft delete do produto")
    void deveDeletarProduto() {
        when(repository.findById(1L)).thenReturn(Optional.of(produto));

        service.deletar(1L);

        assertThat(produto.getAtivo()).isFalse();
    }
}