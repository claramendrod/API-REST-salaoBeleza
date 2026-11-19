package br.com.fatec.salaodebeleza.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "clientes")
@Schema(description = "Pessoa que realiza serviços no salão")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    @Column(nullable = false, length = 100)
    @Schema(description = "Nome completo", example = "Ana Souza")
    private String nome;

    @NotBlank(message = "O telefone é obrigatório")
    @Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres")
    @Column(nullable = false, length = 20)
    @Schema(description = "Telefone para contato", example = "17999990000")
    private String telefone;

    @Email(message = "Informe um e-mail válido")
    @Size(max = 120)
    @Column(length = 120)
    @Schema(description = "E-mail para contato", example = "ana@example.com")
    private String email;

    @Column(nullable = false, columnDefinition = "boolean default true")
    @Schema(description = "Indica se o cliente pode realizar novos agendamentos", example = "true")
    private boolean ativo = true;
}
