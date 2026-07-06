# Laboratório de uso do GSD no OpenCode

Este repositório registra um laboratório de uso do **GSD Core** integrado ao **OpenCode** para avaliar como o workflow se comporta na criação de uma API Spring Boot.

- Repositório original do GSD Core: https://github.com/open-gsd/gsd-core
- Documentação oficial: https://docs.opengsd.net/
- Repositório deste laboratório: https://github.com/diasgarcia/teste-gsd-opencode-cwi

## Objetivo do laboratório

O objetivo foi testar se o GSD ajuda a estruturar melhor o uso de agentes de IA dentro do OpenCode, principalmente em tarefas maiores de desenvolvimento.

A análise observou:

- qualidade da entrega gerada;
- organização do fluxo de trabalho;
- aderência ao processo descrito na documentação do GSD;
- consumo aproximado de créditos;
- comportamento do agente em relação a planejamento, execução e leitura de contexto;
- viabilidade de uso em cenários reais com clientes.

A ideia não foi apenas gerar código, mas entender se o GSD funciona como uma camada de workflow sobre o OpenCode, reduzindo o comportamento solto do agente e guiando melhor a execução por fases.

## Resumo do resultado

O laboratório foi positivo.

O GSD organizou a criação da aplicação em fases, gerou artefatos de planejamento, capturou decisões antes de executar, criou um plano detalhado e depois implementou uma API funcional.

A entrega gerada não foi apenas um endpoint isolado. O resultado foi uma API REST completa de partituras, com CRUD, validações, camadas separadas, persistência, migration e testes unitários.

## Stack usada

A aplicação foi criada com:

- Java 17;
- Spring Boot;
- Maven;
- PostgreSQL;
- Flyway;
- Spring Web;
- Spring Data JPA;
- Bean Validation;
- Lombok;
- JUnit 5;
- Mockito.

## Escopo usado como entrada

O GSD recebeu um brief inicial em `docs/brief-partitura-api.md` com o seguinte escopo:

- cadastrar partitura;
- listar partituras com paginação;
- buscar partitura por ID;
- atualizar partitura;
- remover partitura;
- validar campos obrigatórios;
- usar DTOs;
- não expor entidade JPA diretamente no controller;
- separar controller, service, repository, dto, mapper e exception;
- criar migration Flyway inicial;
- criar docker-compose com PostgreSQL;
- criar testes unitários para service.

Ficaram fora do escopo:

- autenticação;
- upload real de arquivo;
- integração com storage;
- frontend.

## Como o GSD foi usado

A instalação foi feita localmente no repositório, para não alterar a configuração global do OpenCode:

```bash
npx @opengsd/gsd-core@latest --opencode --local
```

Depois, o fluxo foi conduzido pelo OpenCode usando comandos do GSD:

```text
/gsd-new-project --auto @docs/brief-partitura-api.md
/gsd-discuss-phase 1
/gsd-plan-phase 1
/gsd-execute-phase 1
```

A configuração gerada em `.planning/config.json` registrou o runtime como `opencode`, granularidade `coarse`, estratégia de commits atômicos por task e `subagents_enabled` como `false`.

## Artefatos gerados pelo GSD

O GSD criou a estrutura de planejamento em `.planning/`, incluindo:

- `.planning/PROJECT.md` — visão geral do projeto, valor central, constraints e decisões principais;
- `.planning/REQUIREMENTS.md` — requisitos v1 e rastreabilidade por fase;
- `.planning/ROADMAP.md` — divisão do trabalho em fases;
- `.planning/STATE.md` — estado do workflow;
- `.planning/config.json` — configuração do workflow;
- `.planning/phases/01-funda-o/01-CONTEXT.md` — decisões capturadas na discussão da fase;
- `.planning/phases/01-funda-o/01-PLAN.md` — plano executável da fase 1;
- `.planning/phases/01-funda-o/01-RESEARCH.md` — pesquisa/levantamento de apoio;
- `.planning/phases/01-funda-o/01-PLAN-CHECK.md` — verificação do plano.

Isso bate com a proposta do GSD: transformar uma tarefa aberta em um fluxo com contexto, requisitos, roadmap, plano e execução controlada.

## Roadmap gerado

O GSD dividiu o projeto em três fases principais:

| Fase | Nome | Objetivo |
|---|---|---|
| 1 | Fundação | Configurar PostgreSQL, Flyway, application.properties e migration inicial |
| 2 | CRUD de Partituras | Criar API REST completa com endpoints, DTOs, service, repository e exceptions |
| 3 | Testes & Documentação | Criar testes unitários e documentação de uso |

A divisão em fases foi boa para manter a execução mais organizada e facilitar a análise do que estava sendo feito em cada etapa.

## API gerada

A API criada possui o recurso `Partitura` com os campos principais:

- `id`;
- `titulo`;
- `compositor`;
- `instrumento`;
- `genero`;
- `dificuldade`;
- `ano`;
- `arquivoUrl`;
- `observacoes`;
- `criadoEm`;
- `atualizadoEm`.

O enum de dificuldade possui:

- `INICIANTE`;
- `INTERMEDIARIO`;
- `AVANCADO`.

## Endpoints criados

A API expõe o recurso em `/api/partituras`:

| Método | Endpoint | Objetivo |
|---|---|---|
| POST | `/api/partituras` | Criar uma partitura |
| GET | `/api/partituras` | Listar partituras com paginação |
| GET | `/api/partituras/{id}` | Buscar partitura por ID |
| PUT | `/api/partituras/{id}` | Atualizar partitura |
| DELETE | `/api/partituras/{id}` | Remover partitura |

Ou seja, a entrega foi uma API CRUD completa, não apenas um endpoint.

## Arquitetura criada

A aplicação foi organizada em camadas:

```text
controller -> service -> repository
       dto -> mapper -> entity
             exception
```

Principais componentes criados:

- `PartituraController`;
- `PartituraService`;
- `PartituraRepository`;
- `Partitura`;
- `Dificuldade`;
- `PartituraRequestDTO`;
- `PartituraResponseDTO`;
- `PartituraMapper`;
- `PartituraNotFoundException`;
- `GlobalExceptionHandler`;
- `ErrorResponse`.

O controller não expõe a entidade JPA diretamente e usa DTOs para entrada e saída, conforme solicitado no brief.

## Validações e tratamento de erros

Foram adicionadas validações com Bean Validation nos DTOs, principalmente para campos obrigatórios:

- `titulo`;
- `compositor`;
- `instrumento`;
- `dificuldade`.

Também foi criado tratamento global de exceções para:

- recurso não encontrado;
- erro de validação;
- erro de argumento inválido;
- erro geral interno.

## Banco de dados e Flyway

A fase de fundação criou:

- `docker-compose.yml` com PostgreSQL;
- configuração de datasource no `application.properties`;
- Flyway habilitado;
- migration inicial para criar a tabela `partituras`.

A migration define a tabela com os campos principais da entidade e uma constraint para garantir os valores aceitos em `dificuldade`.

## Testes gerados

Foram criados testes unitários para `PartituraService` usando JUnit 5 e Mockito.

Os testes cobrem os principais fluxos:

- criação com sucesso;
- criação com campos opcionais ausentes;
- listagem paginada;
- listagem vazia;
- busca por ID com sucesso;
- busca por ID inexistente;
- atualização com sucesso;
- atualização de ID inexistente;
- remoção com sucesso;
- remoção de ID inexistente.

## Avaliação da experiência com o GSD

### Pontos positivos

O GSD foi muito bom para organizar o trabalho. Em vez de o agente sair implementando diretamente, ele criou uma sequência mais controlada:

1. entender o projeto;
2. gerar requisitos;
3. montar roadmap;
4. discutir a fase;
5. travar decisões;
6. planejar a execução;
7. validar o plano;
8. implementar.

Esse fluxo deixou a execução mais previsível e mais fácil de auditar.

A qualidade final da API também foi boa. O projeto ficou funcional, bem separado em camadas e com os principais pontos de uma API Spring Boot cobertos.

### Pontos de atenção

O GSD não segue automaticamente padrões internos de uma empresa ou formação específica. Por exemplo, a API não seguiu exatamente o padrão aprendido no Crescer, mas isso era esperado porque esse padrão não foi especificado com detalhes no brief inicial.

Para uso real com clientes, é importante fornecer ao GSD um brief mais completo, incluindo:

- padrão de pacotes;
- convenções de nomes;
- estilo de DTOs;
- formato de exceptions;
- padrão de testes;
- padrão de commits;
- bibliotecas permitidas;
- exemplos de código existentes;
- critérios de aceite claros.

Quanto mais explícito o padrão, maior a chance de o resultado seguir o esperado.

## Consumo observado

O consumo observado ficou abaixo do cálculo inicial discutido anteriormente.

- Estimativa/cálculo inicial discutido: aproximadamente **US$4**.
- Consumo observado para a execução principal: aproximadamente **US$2**.

  *Modelo: deepseek*

Considerando que a entrega foi uma API CRUD completa, o custo observado é interessante para um laboratório inicial.

Ainda assim, esse número deve ser interpretado como medição aproximada do teste, não como benchmark final. Para uma avaliação mais confiável, seria necessário repetir o mesmo tipo de tarefa algumas vezes, com escopos parecidos, registrando:

- modelo usado;
- número de chamadas;
- tokens por etapa;
- custo por fase;
- tempo total;
- quantidade de arquivos lidos;
- quantidade de arquivos alterados;
- retrabalho necessário.

## Hipótese sobre leitura excessiva de contexto

A experiência reforçou uma hipótese importante: o GSD melhora bastante o workflow, mas o comportamento de leitura excessiva pode estar mais relacionado à bridge, ao fork do OpenCode ou ao fluxo Node-RED usado no meio da integração.

Durante o fluxo do GSD, as leituras pareceram mais direcionadas por fase e por plano. O agente lia contexto, requisitos, roadmap e plano porque esses artefatos fazem parte do processo.

Ainda vale investigar se existe alguma instrução na bridge, no fork do OpenCode ou no Node-RED dizendo algo como:

- sempre ler os arquivos relevantes antes de responder;
- usar o workspace como fonte da verdade;
- não confiar no contexto recente;
- inspecionar o código antes de documentar ou editar;
- mapear o projeto antes de executar qualquer tarefa.

Esse tipo de instrução pode ser útil para evitar alucinação, mas também pode aumentar muito o consumo quando o agente relê arquivos que acabou de criar ou já tinha lido na mesma sessão.

## Sugestão de investigação técnica

Para investigar a hipótese de leitura excessiva, procurar no fork do OpenCode, na bridge e no fluxo Node-RED por termos como:

```text
always read
read before
inspect before
analyze codebase
source of truth
do not trust context
verify files
scan repository
glob
grep
read_file
list files
workspace
context
AGENTS.md
skills
documentation
```

Também seria útil instrumentar a bridge para registrar, por request:

- prompt final enviado ao modelo;
- instruções injetadas;
- tools disponíveis;
- quantidade de chamadas `read`, `glob`, `grep` e `bash`;
- arquivos lidos repetidamente;
- tokens de entrada e saída por chamada;
- custo por etapa do workflow.

## Recomendações para uso com clientes

O GSD parece promissor para cenários em que é importante ter rastreabilidade e controle sobre o que a IA está fazendo.

Casos em que faz sentido testar:

- criação de APIs pequenas e médias;
- geração de MVPs internos;
- padronização de tarefas repetitivas;
- criação de estrutura inicial de projetos;
- apoio em discovery técnico;
- geração de plano de execução antes de codar;
- documentação técnica baseada em artefatos planejados.

Cuidados recomendados:

- começar com instalação local por repositório;
- usar granularidade grossa para evitar excesso de fases;
- evitar modo autônomo em testes iniciais;
- fornecer brief detalhado;
- incluir padrões internos no prompt ou em documentação de apoio;
- monitorar custo por fase;
- medir tool calls e arquivos relidos;
- manter revisão humana antes de aceitar a entrega.

## Conclusão

O laboratório indica que o GSD funciona bem como uma camada de workflow para o OpenCode. Ele ajudou a transformar um pedido amplo em um processo estruturado, com requisitos, roadmap, decisões, plano e implementação.

O resultado foi uma API REST completa de partituras em Spring Boot, com CRUD, validação, persistência, migration e testes unitários.

A principal conclusão é que o GSD melhora a organização e a previsibilidade do trabalho do agente, mas ainda é importante investigar a bridge/OpenCode/Node-RED para entender melhor o comportamento de leitura de contexto e consumo de tokens.

Para uso em clientes, o próximo passo seria repetir o teste com um brief mais próximo de um padrão real de projeto da CWI/Crescer e medir custo, qualidade e retrabalho de forma mais controlada.
****
