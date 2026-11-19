package br.com.fatec.salaodebeleza.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "API Salão de Beleza",
        version = "1.0.0",
        description = "API REST para cadastrar clientes e serviços e organizar agendamentos de um salão de beleza. " +
                "Impede conflitos de horário e mantém o histórico de agendamentos cancelados.",
        contact = @Contact(name = "Equipe do projeto")
))
public class SwaggerConfig {
}
