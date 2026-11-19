# API Salão de Beleza

Salões que controlam horários por mensagens ou anotações podem perder informações e marcar mais de um atendimento para o mesmo período. Esta API REST centraliza clientes, serviços e agendamentos, permite que diferentes aplicações consultem e atualizem esses dados e bloqueia horários sobrepostos. O cancelamento mantém o histórico.

## Tecnologias

- Java 21 e Spring Boot
- Spring Web, Spring Data JPA e Bean Validation
- PostgreSQL
- Swagger UI / OpenAPI
- Maven

## Estrutura

O projeto segue a organização do projeto Ninja de referência:

```text
src/main/java/br/com/fatec/salaodebeleza/
├── config/       # configuração do Swagger
├── controller/   # endpoints REST
├── dto/          # formato de entrada para agendamento
├── model/        # entidades persistidas
├── repository/   # acesso ao PostgreSQL via JPA
└── service/      # regras de negócio
```

## Regras de negócio

1. Não permite horários sobrepostos. O bloqueio considera a duração em minutos cadastrada para o serviço.
2. Um agendamento precisa apontar para cliente e serviço existentes e sua data deve estar no futuro.
3. Agendamento cancelado não bloqueia mais o horário e permanece no banco para consulta do histórico.
4. A exclusão de cliente ou serviço é lógica: marca como inativo, remove das listagens e da busca comum por ID, e impede novos agendamentos, preservando os registros históricos relacionados. Os agendamentos antigos continuam exibindo os dados vinculados. Use `POST /clientes/{id}/reativar` ou `POST /servicos/{id}/reativar` para reativar.

O escopo inicial considera uma única profissional no salão.

## Preparação do banco no pgAdmin

1. Abra o **pgAdmin** e conecte ao servidor PostgreSQL local (normalmente o servidor `PostgreSQL` na porta `5432`).
2. Expanda **Servers → seu servidor → Databases**.
3. Clique com o botão direito em **Databases → Create → Database...**.
4. Informe `salao_beleza` em **Database** e salve. Não é necessário criar tabelas manualmente: o JPA cria/atualiza as tabelas ao iniciar a aplicação.
5. A configuração inicial assume usuário `postgres` e senha `123456`. Se a senha local for diferente, no IntelliJ abra `src/main/resources/application.properties` e ajuste `spring.datasource.password`. Alternativamente, configure as variáveis de ambiente `DB_USERNAME` e `DB_PASSWORD`.

## Abrir e executar no IntelliJ IDEA

1. Descompacte/abra a pasta `salaoBeleza` no IntelliJ: **File → Open** e selecione a pasta onde está este `pom.xml`.
2. Quando o IntelliJ perguntar, escolha confiar no projeto e aguarde o Maven importar as dependências. Configure um **Project SDK Java 21** em **File → Project Structure → Project SDK** se necessário.
3. Confirme que o PostgreSQL está instalado e iniciado e que o banco `salao_beleza` foi criado no pgAdmin.
4. Abra `SalaoDeBelezaApplication.java` e clique no botão verde **Run** ao lado da classe. A aplicação usa a porta `8080`.
5. Com a aplicação em execução, abra [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html). O JSON OpenAPI fica em [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs).

## Endpoints

| Método | Endpoint | Descrição | Respostas comuns |
|---|---|---|---|
| POST | `/clientes` | Cadastra cliente | 201, 400 |
| GET | `/clientes` | Lista clientes | 200 |
| GET | `/clientes/{id}` | Busca cliente | 200, 404 |
| PUT | `/clientes/{id}` | Atualiza cliente | 200, 400, 404 |
| DELETE | `/clientes/{id}` | Inativa cliente e preserva histórico | 204, 404 |
| POST | `/clientes/{id}/reativar` | Reativa cliente | 200, 404, 409 |
| POST | `/servicos` | Cadastra serviço | 201, 400 |
| GET | `/servicos` | Lista serviços | 200 |
| GET | `/servicos/{id}` | Busca serviço | 200, 404 |
| PUT | `/servicos/{id}` | Atualiza serviço | 200, 400, 404 |
| DELETE | `/servicos/{id}` | Inativa serviço e preserva histórico | 204, 404 |
| POST | `/servicos/{id}/reativar` | Reativa serviço | 200, 404, 409 |
| POST | `/agendamentos` | Cria agendamento | 201, 400, 404, 409 |
| GET | `/agendamentos` | Lista ativos; `?incluirCancelados=true` inclui histórico | 200 |
| GET | `/agendamentos/{id}` | Busca agendamento | 200, 404 |
| GET | `/agendamentos/periodo?inicio=...&fim=...` | Consulta por período | 200, 400 |
| GET | `/agendamentos/cliente/{clienteId}` | Histórico por cliente | 200, 404 |
| PUT | `/agendamentos/{id}` | Atualiza agendamento | 200, 400, 404, 409 |
| DELETE | `/agendamentos/{id}` | Cancela e mantém histórico | 204, 404, 409 |

## Exemplo de uso no Swagger

Cadastre primeiro um cliente (`POST /clientes`):

```json
{
  "nome": "Ana Souza",
  "telefone": "17999990000",
  "email": "ana@example.com"
}
```

Cadastre um serviço (`POST /servicos`):

```json
{
  "nome": "Corte",
  "descricao": "Corte feminino",
  "duracaoMinutos": 45,
  "preco": 60.00
}
```

Use os IDs retornados para criar um agendamento (`POST /agendamentos`):

```json
{
  "clienteId": 1,
  "servicoId": 1,
  "dataHoraInicio": "2026-11-25T14:30:00",
  "observacoes": "Preferência por corte em camadas"
}
```

Datas usam o fuso horário local da aplicação no formato ISO `yyyy-MM-dd'T'HH:mm:ss`. Ao testar, use uma data futura. Se tentar criar outro agendamento que se sobreponha a esse horário, a API responde `409 Conflict`.

## Códigos HTTP

- `200 OK`: consulta ou atualização concluída.
- `201 Created`: recurso criado.
- `204 No Content`: exclusão/cancelamento concluído.
- `400 Bad Request`: JSON inválido ou campos que falharam na validação.
- `404 Not Found`: recurso ou relacionamento informado não existe.
- `409 Conflict`: conflito de horário ou outra regra de negócio violada, como tentar cancelar um agendamento já cancelado.

## Requisitos contemplados

- API REST com JSON e camadas Controller, Service, Repository e Model.
- Três entidades relacionadas: Cliente, Serviço e Agendamento.
- CRUD completo para Cliente e Serviço (com exclusão lógica); operações de criação, consulta, atualização e cancelamento para Agendamento.
- Mais de oito endpoints, validações e regras de negócio.
- Persistência relacional PostgreSQL e documentação OpenAPI/Swagger.
