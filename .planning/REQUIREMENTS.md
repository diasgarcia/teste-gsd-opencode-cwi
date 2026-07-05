# Requirements: Partitura API

**Defined:** 2026-07-03
**Core Value:** CRUD completo e robusto de partituras

## v1 Requirements

Requirements for initial release. Each maps to roadmap phases.

### Partituras (CRUD)

- [ ] **PART-01**: Usuário pode cadastrar uma partitura com título, compositor e instrumento obrigatórios
- [ ] **PART-02**: Usuário pode listar partituras com paginação (page, size)
- [ ] **PART-03**: Usuário pode buscar uma partitura por ID; retorna 404 se não existir
- [ ] **PART-04**: Usuário pode atualizar uma partitura existente; retorna 404 se não existir
- [ ] **PART-05**: Usuário pode remover uma partitura; retorna 404 se não existir

### Validação

- [ ] **VAL-01**: Campos obrigatórios (titulo, compositor, instrumento) são validados no request
- [ ] **VAL-02**: Enum de dificuldade é validado; aceita apenas INICIANTE, INTERMEDIARIO, AVANCADO

### Infraestrutura

- [ ] **INFRA-01**: docker-compose.yml com PostgreSQL pronto para desenvolvimento local
- [ ] **INFRA-02**: Migration Flyway inicial cria tabela partituras
- [ ] **INFRA-03**: application.properties configurado para PostgreSQL + Flyway

### Qualidade

- [ ] **QUAL-01**: Testes unitários para PartituraService cobrindo todos os casos (sucesso e borda)
- [ ] **QUAL-02**: README.md com instruções de setup, execução e endpoints

### Arquitetura

- [ ] **ARCH-01**: Controller não expõe entidade JPA diretamente; usa DTOs
- [ ] **ARCH-02**: Separação clara: controller, service, repository, dto, mapper, exception
- [ ] **ARCH-03**: Handler global de exceções (404, 400)

## v2 Requirements

Deferred to future release.

### Segurança

- **AUTH-01**: Autenticação JWT nos endpoints
- **AUTH-02**: Role-based access (admin vs usuário)

### Ficheiros

- **FILE-01**: Upload real de ficheiro PDF/MusicXML
- **FILE-02**: Integração com storage (S3/MinIO)
- **FILE-03**: Validação de tipo e tamanho de ficheiro

### Frontend

- **UI-01**: Interface web para gerenciamento de partituras
- **UI-02**: Visualizador de partitura embutido

## Out of Scope

| Feature | Reason |
|---------|--------|
| Autenticação/Autorização | API aberta suficiente para v1; complexidade adicional desnecessária agora |
| Upload real de ficheiro | arquivoUrl como string atende MVP; storage requer infra adicional |
| Integração com storage (S3/MinIO) | Sem upload real, não há necessidade de storage |
| Frontend | API-only; frontend é projeto separado |

## Traceability

| Requirement | Phase | Status |
|-------------|-------|--------|
| PART-01 | Phase 2 | Pending |
| PART-02 | Phase 2 | Pending |
| PART-03 | Phase 2 | Pending |
| PART-04 | Phase 2 | Pending |
| PART-05 | Phase 2 | Pending |
| VAL-01 | Phase 2 | Pending |
| VAL-02 | Phase 2 | Pending |
| INFRA-01 | Phase 1 | Pending |
| INFRA-02 | Phase 1 | Pending |
| INFRA-03 | Phase 1 | Pending |
| QUAL-01 | Phase 3 | Pending |
| QUAL-02 | Phase 3 | Pending |
| ARCH-01 | Phase 2 | Pending |
| ARCH-02 | Phase 2 | Pending |
| ARCH-03 | Phase 2 | Pending |

**Coverage:**
- v1 requirements: 15 total
- Mapped to phases: 15
- Unmapped: 0 ✓

---
*Requirements defined: 2026-07-03*
*Last updated: 2026-07-03 after initial definition*
