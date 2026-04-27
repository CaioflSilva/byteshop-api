package com.bytesoft.byteshop.service;

import com.bytesoft.byteshop.dto.request.ProdutoRequest;
import com.bytesoft.byteshop.dto.response.ProdutoResponse;
import com.bytesoft.byteshop.exception.RecursoNaoEncontradoException;
import com.bytesoft.byteshop.exception.RegraDeNegocioException;
import com.bytesoft.byteshop.model.Categoria;
import com.bytesoft.byteshop.model.Produto;
import com.bytesoft.byteshop.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository repository;
    private final CategoriaService categoriaService;

    @Transactional
    public ProdutoResponse criar(ProdutoRequest request) {
        Categoria categoria = categoriaService.buscarEntidade(request.getCategoriaId());

        if (repository.existsByNomeAndCategoriaId(request.getNome(), request.getCategoriaId())) {
            throw new RegraDeNegocioException("Já existe um produto com esse nome nesta categoria.");
        }

        Produto produto = Produto.builder()
                .nome(request.getNome().trim())
                .descricao(request.getDescricao())
                .preco(request.getPreco())
                .estoque(request.getEstoque())
                .categoria(categoria)
                .build();

        return toResponse(repository.save(produto));
    }

    @Transactional(readOnly = true)
    public Page<ProdutoResponse> listar(Long categoriaId, Pageable pageable) {
        if (categoriaId != null) {
            return repository.findByCategoriaIdAndAtivoTrue(categoriaId, pageable)
                    .map(this::toResponse);
        }
        return repository.findByAtivoTrue(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscarPorId(Long id) {
        return toResponse(buscarEntidade(id));
    }

    @Transactional
    public ProdutoResponse atualizar(Long id, ProdutoRequest request) {
        Produto produto = buscarEntidade(id);
        Categoria categoria = categoriaService.buscarEntidade(request.getCategoriaId());

        produto.setNome(request.getNome().trim());
        produto.setDescricao(request.getDescricao());
        produto.setPreco(request.getPreco());
        produto.setEstoque(request.getEstoque());
        produto.setCategoria(categoria);
        produto.setAtualizadoEm(LocalDateTime.now());

        return toResponse(produto);
    }

    @Transactional
    public void deletar(Long id) {
        Produto produto = buscarEntidade(id);
        produto.setAtivo(false);
        produto.setAtualizadoEm(LocalDateTime.now());
    }

    private Produto buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado: " + id));
    }

    private ProdutoResponse toResponse(Produto produto) {
        return new ProdutoResponse(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getEstoque(),
                produto.getAtivo(),
                produto.getCategoria().getNome()
        );
    }
}