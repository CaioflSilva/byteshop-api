package com.bytesoft.byteshop.repository;

import com.bytesoft.byteshop.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProdutoRepository extends JpaRepository<Produto, Long>,
        JpaSpecificationExecutor<Produto> {

    Page<Produto> findByAtivoTrue(Pageable pageable);
    Page<Produto> findByCategoriaIdAndAtivoTrue(Long categoriaId, Pageable pageable);
    boolean existsByNomeAndCategoriaId(String nome, Long categoriaId);
}