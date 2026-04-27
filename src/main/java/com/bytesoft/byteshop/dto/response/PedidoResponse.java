package com.bytesoft.byteshop.dto.response;

import com.bytesoft.byteshop.model.StatusPedido;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponse {
    private Long id;
    private StatusPedido status;
    private BigDecimal total;
    private List<ItemPedidoResponse> itens;
    private LocalDateTime criadoEm;
}