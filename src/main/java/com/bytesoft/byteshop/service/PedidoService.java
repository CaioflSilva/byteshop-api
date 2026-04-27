package com.bytesoft.byteshop.service;

import com.bytesoft.byteshop.dto.request.AtualizarStatusRequest;
import com.bytesoft.byteshop.dto.response.ItemPedidoResponse;
import com.bytesoft.byteshop.dto.response.PedidoResponse;
import com.bytesoft.byteshop.exception.RecursoNaoEncontradoException;
import com.bytesoft.byteshop.exception.RegraDeNegocioException;
import com.bytesoft.byteshop.model.*;
import com.bytesoft.byteshop.repository.PedidoRepository;
import com.bytesoft.byteshop.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final CarrinhoService carrinhoService;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public PedidoResponse finalizar() {
        Usuario usuario = getUsuarioLogado();
        Carrinho carrinho = carrinhoService.buscarOuCriarCarrinho();

        if (carrinho.getItens().isEmpty()) {
            throw new RegraDeNegocioException("Carrinho está vazio");
        }

        Pedido pedido = Pedido.builder()
                .usuario(usuario)
                .total(BigDecimal.ZERO)
                .build();

        List<ItemPedido> itensPedido = carrinho.getItens().stream().map(itemCarrinho -> {
            Produto produto = itemCarrinho.getProduto();

            if (produto.getEstoque() < itemCarrinho.getQuantidade()) {
                throw new RegraDeNegocioException(
                        "Estoque insuficiente para: " + produto.getNome()
                                + ". Disponível: " + produto.getEstoque()
                );
            }

            produto.setEstoque(produto.getEstoque() - itemCarrinho.getQuantidade());

            BigDecimal subtotal = itemCarrinho.getPrecoUnit()
                    .multiply(BigDecimal.valueOf(itemCarrinho.getQuantidade()));

            return ItemPedido.builder()
                    .pedido(pedido)
                    .produto(produto)
                    .quantidade(itemCarrinho.getQuantidade())
                    .precoUnit(itemCarrinho.getPrecoUnit())
                    .subtotal(subtotal)
                    .build();
        }).toList();

        BigDecimal total = itensPedido.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        pedido.setTotal(total);
        pedido.getItens().addAll(itensPedido);

        carrinho.getItens().clear();

        return toResponse(pedidoRepository.save(pedido));
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponse> listarMeusPedidos(Pageable pageable) {
        Usuario usuario = getUsuarioLogado();
        return pedidoRepository.findByUsuarioId(usuario.getId(), pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscarPorId(Long id) {
        Usuario usuario = getUsuarioLogado();
        Pedido pedido = pedidoRepository.findById(id)
                .filter(p -> p.getUsuario().getId().equals(usuario.getId()))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado: " + id));
        return toResponse(pedido);
    }

    @Transactional
    public PedidoResponse atualizarStatus(Long id, AtualizarStatusRequest request) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado: " + id));

        if (pedido.getStatus() == StatusPedido.CANCELADO ||
                pedido.getStatus() == StatusPedido.ENTREGUE) {
            throw new RegraDeNegocioException("Pedido não pode ser atualizado no status: " + pedido.getStatus());
        }

        pedido.setStatus(request.getStatus());
        pedido.setAtualizadoEm(LocalDateTime.now());
        return toResponse(pedido);
    }

    private Usuario getUsuarioLogado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
    }

    private PedidoResponse toResponse(Pedido pedido) {
        List<ItemPedidoResponse> itens = pedido.getItens().stream()
                .map(item -> new ItemPedidoResponse(
                        item.getId(),
                        item.getProduto().getId(),
                        item.getProduto().getNome(),
                        item.getQuantidade(),
                        item.getPrecoUnit(),
                        item.getSubtotal()
                )).toList();

        return new PedidoResponse(
                pedido.getId(),
                pedido.getStatus(),
                pedido.getTotal(),
                itens,
                pedido.getCriadoEm()
        );
    }
}