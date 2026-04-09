# Adventure Management API — Registro Oficial da Guilda de Aventureiros

Sistema de Gestão de Aventureiros — API RESTful desenvolvida com **Spring Boot 3.2.3**, integrando persistência JPA (PostgreSQL), busca NoSQL (Elasticsearch), cache distribuído (Redis) e segurança (Spring Security).

---

## 1. Criação do Projeto (Spring Boot Initializer)

### Ferramenta de Construção: Maven

O projeto utiliza **Apache Maven** como ferramenta de build. A escolha se deu por:

- **Ampla adoção e documentação**: Maven é o build tool mais utilizado no ecossistema Java/Spring, com extensa documentação oficial e da comunidade.
- **Convenção sobre configuração**: a estrutura padrão `src/main/java`, `src/test/java` e `src/main/resources` elimina a necessidade de configuração manual de diretórios.
- **Gerenciamento de dependências centralizado**: via `pom.xml` com herança do `spring-boot-starter-parent`, que gerencia versões compatíveis de todas as bibliotecas automaticamente.
- **Integração nativa com IDEs**: IntelliJ IDEA e VS Code importam projetos Maven diretamente.

### Iniciação do Projeto

O projeto foi iniciado via **Spring Initializr (interface web)** em [start.spring.io](https://start.spring.io), selecionando:

- **Project**: Maven
- **Language**: Java
- **Spring Boot**: 3.2.3
- **Java**: 21
- **Dependências iniciais**: Spring Web, Spring Data JPA, PostgreSQL Driver, Lombok, Validation

#### Alternativa: Spring Boot CLI

O mesmo projeto poderia ser iniciado pela **Spring Boot CLI**:

```bash
spring init \
  --build=maven \
  --java-version=21 \
  --dependencies=web,data-jpa,postgresql,lombok,validation \
  --name=adventure-management-api \
  adventure-management-api.zip
```

#### Quando usar cada método?

| Método | Cenário ideal |
|--------|--------------|
| **Initializr Web** | Início rápido, exploração visual de dependências, geração de projetos em equipe com configuração padronizada |
| **Spring Boot CLI** | Automação em pipelines CI/CD, criação scriptada de múltiplos microserviços, ambientes sem interface gráfica |

### Gerenciamento de Dependências

O projeto herda do `spring-boot-starter-parent` (v3.2.3), que:

- Define versões compatíveis de todas as dependências transitivas (BOM — Bill of Materials)
- Configura plugins Maven padrão (compiler, surefire, spring-boot-maven-plugin)
- Permite declarar dependências **sem especificar versão** (ex: `spring-boot-starter-web`)
- Propriedades customizadas em `<properties>` para bibliotecas como Lombok (`1.18.30`)

### Autoconfiguração do Spring Boot

A anotação `@SpringBootApplication` (que engloba `@EnableAutoConfiguration`) ativa as seguintes autoconfigurações:

- **DataSourceAutoConfiguration**: configura conexão com PostgreSQL baseado nas propriedades `spring.datasource.*`
- **JpaRepositoriesAutoConfiguration**: escaneia e registra interfaces `JpaRepository` como beans Spring
- **WebMvcAutoConfiguration**: configura o DispatcherServlet, conversores JSON (Jackson), e content negotiation
- **SecurityAutoConfiguration**: ativa autenticação HTTP Basic com Spring Security
- **ElasticsearchDataAutoConfiguration**: configura cliente Elasticsearch e `ElasticsearchOperations`
- **RedisAutoConfiguration**: configura `RedisConnectionFactory` e `RedisTemplate`
- **CacheAutoConfiguration**: ativa o gerenciamento de cache (usado com `@EnableCaching`)
- **ActuatorAutoConfiguration**: ativa endpoints de health check e métricas

### Configuração da IDE

O projeto foi desenvolvido com **IntelliJ IDEA** (diretório `.idea`) e **VS Code** (diretório `.vscode`), ambos configurados com:

- Java 21 SDK
- Plugin Maven integrado
- Spring Boot DevTools (reinicialização automática)
- Lombok plugin (geração de código em tempo de compilação)

---

## 2. API RESTful

### Endpoints Principais

#### Aventureiros (`/api/adventurers`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/adventurers` | Listar aventureiros com filtros (ativo, classe, nível, nome) |
| GET | `/api/adventurers/{id}` | Perfil completo com companheiro e última missão |
| POST | `/api/adventurers` | Criar aventureiro |
| POST | `/api/adventurers/{id}/companion` | Adicionar companheiro |
| PUT | `/api/adventurers/{id}` | Atualizar aventureiro |
| DELETE | `/api/adventurers/{id}` | Remover aventureiro (cascade no companheiro) |

#### Missões (`/api/missions`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/missions` | Listar missões com filtros (status, perigo, datas) |
| GET | `/api/missions/{id}` | Detalhes com lista de participantes |
| POST | `/api/missions` | Criar missão |
| POST | `/api/missions/{id}/participants` | Adicionar participante (com validações) |
| PUT | `/api/missions/{id}` | Atualizar missão |
| DELETE | `/api/missions/{id}` | Remover missão (bloqueado se tem participantes) |

#### Relatórios (`/api/reports`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/reports/ranking` | Ranking de aventureiros por participação |
| GET | `/api/reports/missions-metrics` | Relatório de missões com métricas agregadas |

#### Produtos Elasticsearch (`/produtos`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/produtos/busca/nome` | Busca textual por nome |
| GET | `/produtos/busca/descricao` | Busca textual por descrição |
| GET | `/produtos/busca/frase` | Busca por frase exata |
| GET | `/produtos/busca/fuzzy` | Busca fuzzy (tolerância a erros) |
| GET | `/produtos/busca/multicampos` | Busca em múltiplos campos |
| GET | `/produtos/busca/com-filtro` | Busca com filtro de categoria |
| GET | `/produtos/busca/faixa-preco` | Busca por faixa de preço |
| GET | `/produtos/busca/avancada` | Busca avançada (categoria + raridade + preço) |
| GET | `/produtos/agregacoes/por-categoria` | Agregação por categoria |
| GET | `/produtos/agregacoes/por-raridade` | Agregação por raridade |
| GET | `/produtos/agregacoes/preco-medio` | Preço médio |
| GET | `/produtos/agregacoes/faixas-preco` | Distribuição por faixas de preço |

#### Painel Tático (`/missoes`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/missoes/top15dias` | Top 10 missões táticas com cache Redis |

> **Nota:** Todos os endpoints de `/api/**` requerem autenticação HTTP Basic e o header `X-Organization-Id`.

### Organização do Código

```
src/main/java/com/example/adventure_management_api/
├── config/             # Configurações (Security, Redis, Elasticsearch)
├── controller/         # Endpoints REST
├── dto/                # Data Transfer Objects
│   ├── es/             # DTOs do Elasticsearch
│   └── reports/        # DTOs de relatórios
├── entity/             # Entidades JPA e Elasticsearch
│   └── enums/          # Enumerações do domínio
├── exception/          # Exceções e handler global
├── repository/         # Repositórios JPA e Elasticsearch
└── service/            # Camada de serviço (regras de negócio)
```

### Tratamento de Erros (Respostas HTTP)

| Cenário | Status HTTP | Handler |
|---------|-------------|---------|
| Sucesso | 200 OK / 201 Created / 204 No Content | Controllers |
| Recurso não encontrado | 404 Not Found | `ResourceNotFoundException` |
| Violação de regra de negócio | 400 Bad Request | `BusinessRuleException` |
| Dados inválidos (validação) | 422 Unprocessable Entity | `MethodArgumentNotValidException` |
| Violação de integridade | 409 Conflict | `DataIntegrityViolationException` |
| Erro interno | 500 Internal Server Error | `Exception` genérica |

---

## 3. Persistência de Dados

### JPA + PostgreSQL (Relacional)

- **9 entidades JPA** mapeadas: `Adventurer`, `Companion`, `Mission`, `MissionParticipation`, `Organization`, `User`, `Role`, `Permission`, `ApiKey`, `AuditEntry`, `MvPainelTaticoMissao`
- **Repositórios**: `JpaRepository` + `JpaSpecificationExecutor` para consultas dinâmicas
- **Filtros e ordenação**: via `Specification<T>`, `Pageable`, `Sort`
- **Profiles de ambiente**: `application.properties` (prod), `application-test.properties` (H2), `application-integration.properties` (PostgreSQL real)

### Redis (Cache Distribuído)

- **`RedisConfig.java`**: configura `RedisCacheManager` com TTL e `RedisTemplate<String, Object>` para operações diretas
- **Cache `topMissoesCache`**: TTL de 60 segundos para o painel tático
- **`@Cacheable` + `@CacheEvict` + `@Scheduled`**: cache transparente com invalidação automática

### Elasticsearch (NoSQL / Documento)

- **Entidade `Produto`** anotada com `@Document(indexName = "guilda_loja")`
- **`ProdutoRepository`** extends `ElasticsearchRepository<Produto, String>`
- **`ProdutoService`**: buscas textuais (match, phrase, fuzzy, multi_match), filtros (categoria, preço), agregações (terms, avg, range)

---

## 4. Testes

### Estrutura

| Tipo | Anotação | Escopo |
|------|----------|--------|
| Slice (Repository) | `@DataJpaTest` | Testa camada de repositório isolada com H2 |
| Slice (Web) | `@WebMvcTest` | Testa controllers com MockMvc + mocks |
| Integração | `@SpringBootTest` | Carrega contexto completo da aplicação |

### Classes de Teste

| Classe | Tipo | O que testa |
|--------|------|-------------|
| `ApplicationTests` | `@SpringBootTest` | Contexto da aplicação carrega corretamente |
| `AdventurerQueryTest` | `@DataJpaTest` | Consultas JPA de aventureiros (filtros, perfil, paginação) |
| `MissionQueryTest` | `@DataJpaTest` | Consultas JPA de missões (filtros, detalhamento) |
| `ReportQueryTest` | `@DataJpaTest` | Relatórios gerenciais (ranking, métricas) |
| `AuditSchemaH2Test` | `@DataJpaTest` | Mapeamento do schema legado (audit) |
| `OrganizationRepositoryTest` | `@DataJpaTest` | Repositório de organizações |
| `ContextIntegrationTest` | `@DataJpaTest` | Integração com PostgreSQL real (condicional) |
| `AdventurerControllerTest` | `@WebMvcTest` | Endpoints REST com MockMvc |

### Como Executar

```bash
# Testes com H2 em memória (sem dependência de PostgreSQL)
mvn test

# Testes de integração com PostgreSQL real
set RUN_INTEGRATION=true && mvn test -Dtest=ContextIntegrationTest
```

---

## 5. Segurança (Spring Security)

### Configuração

- **`SecurityConfig.java`**: define `SecurityFilterChain` com autenticação HTTP Basic
- **Sessões stateless**: `SessionCreationPolicy.STATELESS`
- **CSRF desabilitado**: API REST não usa cookies/sessões

### Perfis de Acesso

| Usuário | Senha | Role | Permissões |
|---------|-------|------|-----------|
| `admin` | `admin123` | ADMIN, USER | CRUD completo |
| `user` | `user123` | USER | Apenas leitura (GET) |

### Regras de Autorização

| Operação | Role necessária |
|----------|----------------|
| GET `/api/**`, `/produtos/**`, `/missoes/**` | USER ou ADMIN |
| POST, PUT, DELETE `/api/**` | ADMIN |
| `/actuator/**` | Público |

### Exemplo de chamada autenticada

```bash
# Como admin
curl -u admin:admin123 http://localhost:8080/api/adventurers -H "X-Organization-Id: 1"

# Como user (somente leitura)
curl -u user:user123 http://localhost:8080/api/adventurers -H "X-Organization-Id: 1"
```

---

## 6. Deploy

### Build do projeto

```bash
# Compilar e gerar o JAR executável
mvn clean package -DskipTests

# O JAR será gerado em:
# target/adventure-management-api-0.0.1-SNAPSHOT.jar
```

### Execução autônoma via JAR

```bash
java -jar target/adventure-management-api-0.0.1-SNAPSHOT.jar
```

A aplicação será iniciada em `http://localhost:8080`.

### Health Check (Actuator)

```bash
# Verificar saúde da aplicação
curl http://localhost:8080/actuator/health

# Informações da aplicação
curl http://localhost:8080/actuator/info

# Métricas
curl http://localhost:8080/actuator/metrics
```

### Docker

#### Dockerfile (multi-stage)

O `Dockerfile` utiliza build multi-estágio:
1. **Estágio de build**: `maven:3.9-eclipse-temurin-21` compila e empacota o projeto
2. **Estágio de runtime**: `eclipse-temurin:21-jre-alpine` executa o JAR com footprint mínimo

#### Execução com Docker Compose

```bash
# Subir toda a infraestrutura + aplicação
docker compose up -d

# Verificar logs
docker compose logs -f app

# Parar tudo
docker compose down
```

Os serviços incluem:
- **postgres** (porta 5432): banco relacional
- **elasticsearch** (porta 9200): busca NoSQL
- **redis** (porta 6379): cache distribuído
- **app** (porta 8080): aplicação Spring Boot

---

## Tecnologias Utilizadas

| Tecnologia | Versão | Finalidade |
|-----------|--------|-----------|
| Spring Boot | 3.2.3 | Framework principal |
| Spring Data JPA | — | Persistência relacional |
| Spring Data Elasticsearch | — | Persistência NoSQL |
| Spring Data Redis | — | Cache distribuído |
| Spring Security | — | Autenticação e autorização |
| Spring Boot Actuator | — | Health checks e métricas |
| Hibernate | 6.4 | ORM |
| PostgreSQL | 15+ | Banco relacional |
| Elasticsearch | 8.12 | Busca/NoSQL |
| Redis | 7 | Cache |
| H2 Database | — | Testes em memória |
| JUnit 5 + AssertJ | — | Testes automatizados |
| Lombok | 1.18.30 | Redução de boilerplate |
| Maven | 3.8+ | Build e dependências |
| Docker | — | Containerização |

---

## Pré-requisitos

- **Java 21** (JDK)
- **Maven 3.8+**
- **Docker** e **Docker Compose** (para execução containerizada)

Para execução local sem Docker, é necessário:
- PostgreSQL rodando na porta 5432
- Elasticsearch rodando na porta 9200
- Redis rodando na porta 6379
