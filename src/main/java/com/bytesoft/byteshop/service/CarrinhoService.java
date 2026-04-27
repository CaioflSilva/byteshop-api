package com.bytesoft.byteshop.service;

import com.bytesoft.byteshop.dto.request.ItemCarrinhoRequest;
import com.bytesoft.byteshop.dto.response.CarrinhoResponse;
import com.bytesoft.byteshop.dto.response.ItemCarrinhoResponse;
import com.bytesoft.byteshop.exception.RecursoNaoEncontradoException;
import com.bytesoft.byteshop.exception.RegraDeNegocioException;
import com.bytesoft.byteshop.model.Carrinho;
import com.bytesoft.byteshop.model.ItemCarrinho;
import com.bytesoft.byteshop.model.Produto;
import com.bytesoft.byteshop.model.Usuario;
import com.bytesoft.byteshop.repository.CarrinhoRepository;
import com.bytesoft.byteshop.repository.ItemCarrinhoRepository;
import com.bytesoft.byteshop.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarrinhoService {

    private final CarrinhoRepository carrinhoRepository;
    private final ItemCarrinhoRepository itemCarrinhoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoService produtoService;

    @Transactional(readOnly = true)
    public CarrinhoResponse buscarCarrinho() {
        Carrinho carrinho = buscarOuCriarCarrinho();
        return toResponse(carrinho);
    }

    @Transactional
    public CarrinhoResponse adicionarItem(ItemCarrinhoRequest request) {
        Carrinho carrinho = buscarOuCriarCarrinho();
        Produto produto = produtoService.buscarEntidade(request.getProdutoId());

        if (!produto.getAtivo()) {
            throw new RegraDeNegocioException("Produto indisponível: " + produto.getNome());
        }

        if (produto.getEstoque() < request.getQuantidade()) {
            throw new RegraDeNegocioException("Estoque insuficiente. Disponível: " + produto.getEstoque());
        }

        itemCarrinhoRepository.findByCarrinhoIdAndProdutoId(carrinho.getId(), produto.getId())
                .ifPresentOrElse(
                        item -> {
                            int novaQtd = item.getQuantidade() + request.getQuantidade();
                            if (produto.getEstoque() < novaQtd) {
                                throw new RegraDeNegocioException("Estoque insuficiente. Disponível: " + produto.getEstoque());
                            }
                            item.setQuantidade(novaQtd);
                        },
                        () -> {
                            ItemCarrinho novoItem = ItemCarrinho.builder()
                                    .produto(produto)
                                    .quantidade(request.getQuantidade())
                                    .precoUnit(produto.getPreco())
                                    .build();
                            carrinho.adicionarItem(novoItem);
                        }
                );

        return toResponse(carrinhoRepository.save(carrinho));
    }

    @Transactional
    public CarrinhoResponse atualizarItem(Long itemId, ItemCarrinhoRequest request) {
        Carrinho carrinho = buscarOuCriarCarrinho();

        ItemCarrinho item = itemCarrinhoRepository.findById(itemId)
                .filter(i -> i.getCarrinho().getId().equals(carrinho.getId()))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Item não encontrado no carrinho"));

        if (item.getProduto().getEstoque() < request.getQuantidade()) {
            throw new RegraDeNegocioException("Estoque insuficiente. Disponível: " + item.getProduto().getEstoque());
        }

        item.setQuantidade(request.getQuantidade());
        return toResponse(carrinho);
    }

    @Transactional
    public CarrinhoResponse removerItem(Long itemId) {
        Carrinho carrinho = buscarOuCriarCarrinho();

        ItemCarrinho item = itemCarrinhoRepository.findById(itemId)
                .filter(i -> i.getCarrinho().getId().equals(carrinho.getId()))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Item não encontrado no carrinho"));

        carrinho.removerItem(item);
        return toResponse(carrinhoRepository.save(carrinho));
    }

    @Transactional
    public void limparCarrinho() {
        Carrinho carrinho = buscarOuCriarCarrinho();
        carrinho.getItens().clear();
        carrinhoRepository.save(carrinho);
    }

    public Carrinho buscarOuCriarCarrinho() {
        Usuario usuario = getUsuarioLogado();
        return carrinhoRepository.findByUsuarioId(usuario.getId())
                .orElseGet(() -> {
                    Carrinho novo = Carrinho.builder()
                            .usuario(usuario)
                            .build();
                    return carrinhoRepository.save(novo);
                });
    }

    private Usuario getUsuarioLogado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
    }

    private CarrinhoResponse toResponse(Carrinho carrinho) {
        List<ItemCarrinhoResponse> itens = carrinho.getItens().stream()
                .map(this::toItemResponse)
                .toList();

        BigDecimal total = itens.stream()
                .map(ItemCarrinhoResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CarrinhoResponse(
                carrinho.getId(),
                itens,
                total,
                itens.size()
        );
    }

    private ItemCarrinhoResponse toItemResponse(ItemCarrinho item) {
        BigDecimal subtotal = item.getPrecoUnit()
                .multiply(BigDecimal.valueOf(item.getQuantidade()));

        return new ItemCarrinhoResponse(
                item.getId(),
                item.getProduto().getId(),
                item.getProduto().getNome(),
                item.getQuantidade(),
                item.getPrecoUnit(),
                subtotal
        );
    }
}