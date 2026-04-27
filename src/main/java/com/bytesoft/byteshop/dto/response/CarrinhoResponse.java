package com.bytesoft.byteshop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CarrinhoResponse {
    private Long id;
    private List<ItemCarrinhoResponse> itens;
    private BigDecimal total;
    private Integer quantidadeItens;
}