package com.bytesoft.byteshop.dto.request;

import com.bytesoft.byteshop.model.StatusPedido;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AtualizarStatusRequest {

    @NotNull(message = "Status é obrigatório")
    private StatusPedido status;
}