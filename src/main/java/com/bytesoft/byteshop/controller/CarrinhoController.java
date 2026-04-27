package com.bytesoft.byteshop.controller;

import com.bytesoft.byteshop.dto.request.ItemCarrinhoRequest;
import com.bytesoft.byteshop.dto.response.CarrinhoResponse;
import com.bytesoft.byteshop.service.CarrinhoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carrinho")
@RequiredArgsConstructor
@Tag(name = "Carrinho", description = "Gerenciamento do carrinho de compras")
@SecurityRequirement(name = "bearerAuth")
public class CarrinhoController {

    private final CarrinhoService service;

    @Operation(summary = "Ver carrinho do usuário logado")
    @GetMapping
    public ResponseEntity<CarrinhoResponse> buscar() {
        return ResponseEntity.ok(service.buscarCarrinho());
    }

    @Operation(summary = "Adicionar item ao carrinho")
    @PostMapping("/itens")
    public ResponseEntity<CarrinhoResponse> adicionarItem(@Valid @RequestBody ItemCarrinhoRequest request) {
        return ResponseEntity.ok(service.adicionarItem(request));
    }

    @Operation(summary = "Atualizar quantidade de um item")
    @PutMapping("/itens/{itemId}")
    public ResponseEntity<CarrinhoResponse> atualizarItem(@PathVariable Long itemId,
                                                          @Valid @RequestBody ItemCarrinhoRequest request) {
        return ResponseEntity.ok(service.atualizarItem(itemId, request));
    }

    @Operation(summary = "Remover item do carrinho")
    @DeleteMapping("/itens/{itemId}")
    public ResponseEntity<CarrinhoResponse> removerItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(service.removerItem(itemId));
    }

    @Operation(summary = "Limpar carrinho")
    @DeleteMapping
    public ResponseEntity<Void> limpar() {
        service.limparCarrinho();
        return ResponseEntity.noContent().build();
    }
}