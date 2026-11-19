package br.com.fatec.salaodebeleza.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "servicos")
@Schema(description = "Serviço oferecido pelo salão")
public class Servico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do serviço é obrigatório")
    @Size(min = 2, max = 80)
    @Column(nullable = false, length = 80)
    @Schema(example = "Corte")
    private String nome;

    @Size(max = 300)
    @Column(length = 300)
    @Schema(example = "Corte feminino")
    private String descricao;

    @NotNull(message = "A duração é obrigatória")
    @Min(value = 5, message = "A duração mínima é de 5 minutos")
    @Column(name = "duracao_minutos", nullable = false)
    @Schema(description = "Tempo reservado no calendário", example = "45")
    private Integer duracaoMinutos;

    @NotNull(message = "O preço é obrigatório")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
    @Column(nullable = false, precision = 10, scale = 2)
    @Schema(example = "60.00")
    private BigDecimal preco;

    @Column(nullable = false, columnDefinition = "boolean default true")
    @Schema(description = "Indica se o serviço está disponível para novos agendamentos", example = "true")
    private boolean ativo = true;
}
