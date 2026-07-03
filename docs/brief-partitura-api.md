# API de Partituras em Spring Boot

Criar uma API REST para gerenciamento de partituras.

## Stack obrigatória

- Java 17
- Spring Boot
- Maven
- PostgreSQL
- Flyway
- Spring Web
- Spring Data JPA
- Bean Validation
- Lombok
- JUnit 5
- Mockito

## Escopo v1

A API deve permitir:

- Cadastrar partitura
- Listar partituras com paginação
- Buscar partitura por ID
- Atualizar partitura
- Remover partitura

## Entidade Partitura

Campos:

- id: Long
- titulo: String, obrigatório
- compositor: String, obrigatório
- instrumento: String, obrigatório
- genero: String, opcional
- dificuldade: enum INICIANTE, INTERMEDIARIO, AVANCADO
- ano: Integer, opcional
- arquivoUrl: String, opcional
- criadoEm: LocalDateTime
- atualizadoEm: LocalDateTime

## Endpoints esperados

- POST /api/partituras
- GET /api/partituras
- GET /api/partituras/{id}
- PUT /api/partituras/{id}
- DELETE /api/partituras/{id}

## Regras

- Retornar 404 quando a partitura não existir
- Validar campos obrigatórios
- Usar DTOs para request e response
- Não expor entidade JPA diretamente no controller
- Separar em controller, service, repository, dto, mapper, exception
- Criar migration Flyway inicial
- Criar docker-compose.yml com PostgreSQL
- Criar testes unitários para service
- Criar README com instruções de subir banco, rodar app e testar endpoints

## Fora do escopo v1

- Autenticação
- Upload real de arquivo
- Integração com storage
- Frontend
