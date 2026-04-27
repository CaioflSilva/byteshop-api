package com.bytesoft.byteshop.service;

import com.bytesoft.byteshop.dto.request.CategoriaRequest;
import com.bytesoft.byteshop.dto.response.CategoriaResponse;
import com.bytesoft.byteshop.exception.RecursoNaoEncontradoException;
import com.bytesoft.byteshop.exception.RegraDeNegocioException;
import com.bytesoft.byteshop.model.Categoria;
import com.bytesoft.byteshop.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository repository;

    @Transactional
    public CategoriaResponse criar(CategoriaRequest request) {
        if (repository.existsByNome(request.getNome())) {
            throw new RegraDeNegocioException("Já existe uma categoria com o nome: " + request.getNome());
        }

        Categoria categoria = Categoria.builder()
                .nome(request.getNome().trim())
                .descricao(request.getDescricao())
                .build();

        return toResponse(repository.save(categoria));
    }

    @Transactional(readOnly = true)
    public Page<CategoriaResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public CategoriaResponse buscarPorId(Long id) {
        return toResponse(buscarEntidade(id));
    }

    @Transactional
    public CategoriaResponse atualizar(Long id, CategoriaRequest request) {
        Categoria categoria = buscarEntidade(id);

        if (!categoria.getNome().equalsIgnoreCase(request.getNome())
                && repository.existsByNome(request.getNome())) {
            throw new RegraDeNegocioException("Já existe uma categoria com o nome: " + request.getNome());
        }

        categoria.setNome(request.getNome().trim());
        categoria.setDescricao(request.getDescricao());
        return toResponse(categoria);
    }

    @Transactional
    public void deletar(Long id) {
        Categoria categoria = buscarEntidade(id);
        repository.delete(categoria);
    }

    public Categoria buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada: " + id));
    }

    private CategoriaResponse toResponse(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNome(),
                categoria.getDescricao(),
                categoria.getAtivo()
        );
    }
}