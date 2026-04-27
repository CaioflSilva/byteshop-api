package com.bytesoft.byteshop.controller;

import com.bytesoft.byteshop.dto.request.AtualizarStatusRequest;
import com.bytesoft.byteshop.dto.response.PedidoResponse;
import com.bytesoft.byteshop.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Gerenciamento de pedidos")
@SecurityRequirement(name = "bearerAuth")
public class PedidoController {

    private final PedidoService service;

    @Operation(summary = "Finalizar pedido a partir do carrinho")
    @PostMapping
    public ResponseEntity<PedidoResponse> finalizar() {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.finalizar());
    }

    @Operation(summary = "Listar meus pedidos")
    @GetMapping
    public ResponseEntity<Page<PedidoResponse>> listarMeusPedidos(
            @PageableDefault(size = 10, sort = "criadoEm") Pageable pageable) {
        return ResponseEntity.ok(service.listarMeusPedidos(pageable));
    }

    @Operation(summary = "Buscar pedido por ID")
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Atualizar status do pedido (ADMIN)")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PedidoResponse> atualizarStatus(@PathVariable Long id,
                                                          @Valid @RequestBody AtualizarStatusRequest request) {
        return ResponseEntity.ok(service.atualizarStatus(id, request));
    }
}