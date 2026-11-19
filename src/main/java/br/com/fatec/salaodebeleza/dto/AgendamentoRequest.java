package br.com.fatec.salaodebeleza.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Schema(description = "Dados necessários para criar ou atualizar um agendamento")
public record AgendamentoRequest(
        @NotNull(message = "Informe o ID do cliente")
        @Schema(example = "1")
        Long clienteId,

        @NotNull(message = "Informe o ID do serviço")
        @Schema(example = "1")
        Long servicoId,

        @NotNull(message = "Informe a data e hora de início")
        @Future(message = "A data do agendamento deve estar no futuro")
        @Schema(example = "2026-11-25T14:30:00")
        LocalDateTime dataHoraInicio,

        @Size(max = 300, message = "As observações devem ter no máximo 300 caracteres")
        @Schema(example = "Preferência por esmalte claro")
        String observacoes
) {
}
