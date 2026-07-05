# Partitura API

## What This Is

Uma API REST em Spring Boot para gerenciamento de partituras musicais. Permite cadastrar, listar, buscar, atualizar e remover partituras, com validação de campos obrigatórios, paginação, e respostas padronizadas via DTOs.

## Core Value

**CRUD completo e robusto de partituras** — se tudo o resto falhar, criar e consultar partituras com validação correta tem de funcionar.

## Requirements

### Validated

(None yet — ship to validate)

### Active

- [ ] **PART-01**: Cadastrar partitura com validação de campos obrigatórios
- [ ] **PART-02**: Listar partituras com paginação
- [ ] **PART-03**: Buscar partitura por ID (404 se não existir)
- [ ] **PART-04**: Atualizar partitura existente (404 se não existir)
- [ ] **PART-05**: Remover partitura (404 se não existir)

### Out of Scope

- Autenticação — v1 é API aberta
- Upload real de arquivo — arquivoUrl é apenas string na v1
- Integração com storage (S3, etc.) — fase futura
- Frontend — API-only

## Context

Projeto greenfield com stack Spring Boot já configurada via Spring Initializr. `pom.xml` inclui: Spring Web MVC, Spring Data JPA, Flyway, PostgreSQL, Bean Validation, Lombok, JUnit 5. O código fonte está vazio — apenas a classe `Application` e o teste base.

O projeto usa Spring Boot 4.1.0 (versão recente), Java 17 como target, e Maven wrapper incluso.

## Constraints

- **Tech stack**: Java 17, Spring Boot, Maven, PostgreSQL, Flyway — stack obrigatória
- **Arquitetura**: Controller → Service → Repository; DTOs separados da entidade JPA
- **Qualidade**: Testes unitários obrigatórios para service layer
- **Infra**: docker-compose.yml obrigatório para subir PostgreSQL local
- **Documentação**: README com instruções de setup e endpoints

## Key Decisions

| Decision | Rationale | Outcome |
|----------|-----------|---------|
| Spring Boot 4.1.0 | Última versão estável disponível no Initializr | ✓ Good |
| Flyway para migrations | Gerenciamento declarativo de schema; stack obrigatória | ✓ Good |
| DTOs com MapStruct ou manual | A decidir na fase de implementação | — Pending |
| Enum para dificuldade | INICIANTE, INTERMEDIARIO, AVANCADO — modela bem o domínio | ✓ Good |

---
*Last updated: 2026-07-03 after project initialization*
