# Plano de Implementação - Sistema de Solicitação de Acesso a Módulos

## 📋 Visão Geral do Projeto

Este documento descreve o planejamento completo para implementação do sistema de **Solicitação de Acesso a Módulos**, um sistema corporativo que permite usuários autenticados solicitarem acesso a diferentes módulos com concessão automática baseada em regras de negócio.

---

## 🎯 Objetivos da Implementação

- Desenvolver API RESTful completa com Spring Boot 3.x e Java 21
- Implementar autenticação JWT com expiração de 15 minutos
- Criar sistema de solicitação de acesso com validação automática
- Garantir cobertura de testes ≥ 80% com JaCoCo
- Containerizar aplicação com Docker e balanceamento de carga
- Documentar com Swagger/OpenAPI

---

## 🏗️ Arquitetura da Solução

### Arquitetura de Camadas

```
┌─────────────────────────────────────────┐
│         Nginx (Load Balancer)           │
│            Port 80/443                   │
└─────────────────┬───────────────────────┘
                  │
        ┌─────────┴─────────┐
        │                   │
┌───────▼────────┐ ┌────────▼────────┐ ┌────────────────┐
│   App1:8081    │ │   App2:8082     │ │   App3:8083    │
│  Spring Boot   │ │  Spring Boot    │ │  Spring Boot   │
└───────┬────────┘ └────────┬────────┘ └────────┬────────┘
        │                   │                    │
        └───────────────────┴────────────────────┘
                            │
                   ┌────────▼─────────┐
                   │  PostgreSQL 17   │
                   │    Port 5432     │
                   └──────────────────┘
```

### Estrutura de Pacotes

```
src/main/java/com/supera/accessrequest/
├── config/              # Configurações (Security, Swagger, etc)
├── controller/          # Controllers REST
├── dto/                 # DTOs de Request/Response
├── entity/              # Entidades JPA
├── enums/               # Enumerações
├── exception/           # Exceções customizadas e handlers
├── repository/          # Repositories JPA
├── security/            # JWT, UserDetails, Filters
├── service/             # Lógica de negócio
├── util/                # Utilitários
└── validation/          # Validações customizadas
```

---

## 🗄️ Modelagem de Dados

### Entidades Principais

#### 1. Usuario (User)

```sql
- id: BIGINT (PK)
- nome: VARCHAR(100)
- email: VARCHAR(100) UNIQUE
- senha: VARCHAR(255) (BCrypt)
- departamento: VARCHAR(50)
- ativo: BOOLEAN
- created_at: TIMESTAMP
- updated_at: TIMESTAMP
```

#### 2. Modulo (Module)

```sql
- id: BIGINT (PK)
- nome: VARCHAR(100)
- descricao: VARCHAR(500)
- ativo: BOOLEAN
- created_at: TIMESTAMP
- updated_at: TIMESTAMP
```

#### 3. ModuloDepartamento (ModuleDepartment)

```sql
- id: BIGINT (PK)
- modulo_id: BIGINT (FK)
- departamento: VARCHAR(50)
```

#### 4. ModuloIncompativel (IncompatibleModule)

```sql
- id: BIGINT (PK)
- modulo_id: BIGINT (FK)
- modulo_incompativel_id: BIGINT (FK)
```

#### 5. Solicitacao (Request)

```sql
- id: BIGINT (PK)
- protocolo: VARCHAR(50) UNIQUE
- usuario_id: BIGINT (FK)
- justificativa: TEXT
- urgente: BOOLEAN
- status: VARCHAR(20) (ATIVO, NEGADO, CANCELADO)
- motivo_negacao: TEXT
- motivo_cancelamento: TEXT
- data_solicitacao: TIMESTAMP
- data_expiracao: TIMESTAMP
- solicitacao_origem_id: BIGINT (FK) (para renovações)
- created_at: TIMESTAMP
- updated_at: TIMESTAMP
```

#### 6. SolicitacaoModulo (RequestModule)

```sql
- id: BIGINT (PK)
- solicitacao_id: BIGINT (FK)
- modulo_id: BIGINT (FK)
```

#### 7. AcessoUsuarioModulo (UserModuleAccess)

```sql
- id: BIGINT (PK)
- usuario_id: BIGINT (FK)
- modulo_id: BIGINT (FK)
- solicitacao_id: BIGINT (FK)
- data_inicio: TIMESTAMP
- data_expiracao: TIMESTAMP
- ativo: BOOLEAN
```

#### 8. HistoricoSolicitacao (RequestHistory)

```sql
- id: BIGINT (PK)
- solicitacao_id: BIGINT (FK)
- acao: VARCHAR(50)
- descricao: TEXT
- usuario_id: BIGINT (FK)
- data_acao: TIMESTAMP
```

---

## 🔐 Segurança e Autenticação

### JWT Implementation

**Componentes:**

- `JwtTokenProvider`: Geração e validação de tokens
- `JwtAuthenticationFilter`: Filtro para validar tokens
- `JwtAuthenticationEntryPoint`: Tratamento de erros de autenticação
- `UserDetailsServiceImpl`: Carregar usuário do banco

**Configuração:**

- Tempo de expiração: 15 minutos
- Algoritmo: HS512
- Secret Key: Variável de ambiente

**Senha:**

- BCryptPasswordEncoder com strength 12
- Salt automático

---

## 🛣️ Endpoints da API

### Autenticação

- `POST /api/auth/login` - Login e geração de token

### Solicitações

- `POST /api/solicitacoes` - Criar solicitação
- `GET /api/solicitacoes` - Listar solicitações (paginado)
- `GET /api/solicitacoes/{id}` - Detalhes da solicitação
- `PUT /api/solicitacoes/{id}/renovar` - Renovar solicitação
- `PUT /api/solicitacoes/{id}/cancelar` - Cancelar solicitação

### Módulos

- `GET /api/modulos` - Listar módulos disponíveis

---

## ⚙️ Regras de Negócio Implementadas

### 1. Validações de Solicitação

```java
- Mínimo 1, máximo 3 módulos por solicitação
- Justificativa: 20-500 caracteres
- Justificativa não pode ser genérica (blacklist: "teste", "aaa", "preciso", etc)
- Módulos devem estar ativos
- Usuário não pode ter solicitação ativa para o mesmo módulo
- Usuário não pode solicitar módulo que já possui acesso ativo
```

### 2. Compatibilidade de Departamento

```java
TI -> Todos os módulos
Financeiro -> Financeiro, Relatórios, Portal
RH -> RH, Relatórios, Portal
Operações -> Estoque, Compras, Relatórios, Portal
Outros -> Portal, Relatórios
```

### 3. Módulos Mutuamente Exclusivos

```java
- Aprovador Financeiro ⚔️ Solicitante Financeiro
- Administrador RH ⚔️ Colaborador RH
```

### 4. Limite de Módulos

```java
- Usuário TI: máximo 10 módulos ativos
- Outros usuários: máximo 5 módulos ativos
```

### 5. Geração de Protocolo

```java
Formato: SOL-YYYYMMDD-NNNN
Exemplo: SOL-20251119-0001
```

### 6. Renovação

```java
- Apenas solicitações ATIVAS
- Faltando menos de 30 dias para expirar
- Nova validade: +180 dias
- Reaplicar todas as regras de negócio
```

---

## 🧪 Estratégia de Testes

### Testes Unitários (JUnit 5 + Mockito)

**Regras Rigorosas:**

- ❌ PROIBIDO usar `any()`, `anyString()`, `anyLong()`
- ✅ OBRIGATÓRIO usar `eq()` e valores exatos
- ✅ OBRIGATÓRIO usar `verify()` nas chamadas

**Classes a Testar:**

```
Services:
- AuthService (login, validação de credenciais)
- SolicitacaoService (CRUD, regras de negócio)
- ModuloService (listagem, validações)
- AcessoService (concessão, renovação, cancelamento)

Validators:
- DepartamentoValidator
- ModuloIncompativelValidator
- LimiteModulosValidator
- JustificativaValidator
```

### Testes de Integração (MockMvc + Spring Security Test)

```
- Login com credenciais válidas/inválidas
- Criar solicitação com diferentes cenários
- Validar autorização (acesso apenas às próprias solicitações)
- Paginação e filtros
- Renovação de acesso
- Cancelamento de solicitação
```

### Cobertura de Código (JaCoCo)

```xml
<execution>
  <goals>
    <goal>check</goal>
  </goals>
  <configuration>
    <rules>
      <rule>
        <element>BUNDLE</element>
        <limits>
          <limit>
            <counter>LINE</counter>
            <value>COVEREDRATIO</value>
            <minimum>0.80</minimum>
          </limit>
        </limits>
      </rule>
    </rules>
  </configuration>
</execution>
```

**Meta: ≥ 80% cobertura (obrigatório)**

### Instancio para Geração de Dados de Teste

```java
// Usar Instancio para criar objetos de teste
Usuario usuario = Instancio.create(Usuario.class);
```

---

## 🐳 Infraestrutura Docker

### Dockerfile (Multi-stage build)

```dockerfile
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### docker-compose.yml

```yaml
version: "3.8"

services:
  postgres:
    image: postgres:17-alpine
    environment:
      POSTGRES_DB: accessrequest
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - app-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5

  app1:
    build: .
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/accessrequest
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgres
      SERVER_PORT: 8080
      JWT_SECRET: ${JWT_SECRET}
    ports:
      - "8081:8080"
    depends_on:
      postgres:
        condition: service_healthy
    networks:
      - app-network
    healthcheck:
      test:
        [
          "CMD",
          "wget",
          "--quiet",
          "--tries=1",
          "--spider",
          "http://localhost:8080/actuator/health",
        ]
      interval: 30s
      timeout: 10s
      retries: 3

  app2:
    build: .
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/accessrequest
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgres
      SERVER_PORT: 8080
      JWT_SECRET: ${JWT_SECRET}
    ports:
      - "8082:8080"
    depends_on:
      postgres:
        condition: service_healthy
    networks:
      - app-network
    healthcheck:
      test:
        [
          "CMD",
          "wget",
          "--quiet",
          "--tries=1",
          "--spider",
          "http://localhost:8080/actuator/health",
        ]
      interval: 30s
      timeout: 10s
      retries: 3

  app3:
    build: .
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/accessrequest
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgres
      SERVER_PORT: 8080
      JWT_SECRET: ${JWT_SECRET}
    ports:
      - "8083:8080"
    depends_on:
      postgres:
        condition: service_healthy
    networks:
      - app-network
    healthcheck:
      test:
        [
          "CMD",
          "wget",
          "--quiet",
          "--tries=1",
          "--spider",
          "http://localhost:8080/actuator/health",
        ]
      interval: 30s
      timeout: 10s
      retries: 3

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
    depends_on:
      - app1
      - app2
      - app3
    networks:
      - app-network

networks:
  app-network:
    driver: bridge

volumes:
  postgres_data:
```

### nginx.conf

```nginx
events {
    worker_connections 1024;
}

http {
    upstream backend {
        least_conn;
        server app1:8080 max_fails=3 fail_timeout=30s;
        server app2:8080 max_fails=3 fail_timeout=30s;
        server app3:8080 max_fails=3 fail_timeout=30s;
    }

    server {
        listen 80;

        location / {
            proxy_pass http://backend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            proxy_connect_timeout 60s;
            proxy_send_timeout 60s;
            proxy_read_timeout 60s;
        }

        location /swagger-ui.html {
            proxy_pass http://backend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
        }

        location /v3/api-docs {
            proxy_pass http://backend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
        }
    }
}
```

---

## 📊 Dados Iniciais (data.sql ou Flyway)

### Usuários (mínimo 4)

```sql
-- Senha para todos: "senha123"
INSERT INTO usuario (nome, email, senha, departamento, ativo) VALUES
('João Silva', 'joao.silva@empresa.com', '$2a$12$hash...', 'TI', true),
('Maria Santos', 'maria.santos@empresa.com', '$2a$12$hash...', 'Financeiro', true),
('Pedro Costa', 'pedro.costa@empresa.com', '$2a$12$hash...', 'RH', true),
('Ana Oliveira', 'ana.oliveira@empresa.com', '$2a$12$hash...', 'Operações', true);
```

### Módulos (10 módulos)

```sql
1. Portal do Colaborador
2. Relatórios Gerenciais
3. Gestão Financeira
4. Aprovador Financeiro (incompatível com #5)
5. Solicitante Financeiro (incompatível com #4)
6. Administrador RH (incompatível com #7)
7. Colaborador RH (incompatível com #6)
8. Gestão de Estoque
9. Compras
10. Auditoria
```

---

## 🔧 Tecnologias e Versões

### Obrigatórias

- **Java**: 21
- **Spring Boot**: 3.3.x
- **Spring Data JPA**: (incluído no Spring Boot)
- **Spring Security**: (incluído no Spring Boot)
- **Spring Validation**: (incluído no Spring Boot)
- **PostgreSQL**: 17
- **H2**: (para testes)
- **Maven**: 3.9+
- **Docker**: 24+
- **Docker Compose**: 2.x
- **Nginx**: alpine

### Bibliotecas

- **Lombok**: Redução de boilerplate
- **JWT (jjwt)**: 0.12.x
- **JUnit 5**: Testes
- **Mockito**: Mocks
- **JaCoCo**: Cobertura de testes
- **Instancio**: Geração de dados de teste
- **SpringDoc OpenAPI**: Swagger

---

## 📝 Documentação

### README.md deve conter:

1. Descrição do projeto
2. Tecnologias e versões
3. Pré-requisitos
4. Como executar com Docker
5. Como executar testes
6. Como visualizar relatório JaCoCo
7. Credenciais de teste
8. Exemplos de requisições (cURL)
9. Arquitetura da solução
10. Decisões técnicas

### Swagger/OpenAPI

- Acessível via `/swagger-ui.html`
- Documentação completa de todos os endpoints
- Exemplos de request/response
- Documentação de autenticação JWT

---

## 📅 Plano de Execução (8 etapas)

### Etapa 1: Setup Inicial (Dia 1)

- [ ] Criar estrutura do projeto Maven
- [ ] Configurar pom.xml com dependências
- [ ] Configurar application.yml (dev/prod profiles)
- [ ] Criar estrutura de pacotes
- [ ] Setup Docker e docker-compose
- [ ] Configurar PostgreSQL

### Etapa 2: Modelagem e Entidades (Dia 1-2)

- [ ] Criar entidades JPA
- [ ] Criar enums (Status, Departamento)
- [ ] Criar relacionamentos
- [ ] Criar data.sql ou migrations Flyway
- [ ] Testar criação do schema

### Etapa 3: Segurança e Autenticação (Dia 2-3)

- [ ] Implementar JWT (provider, filter, entry point)
- [ ] Configurar Spring Security
- [ ] Criar UserDetailsService
- [ ] Criar AuthController e AuthService
- [ ] Testar login e geração de token

### Etapa 4: Repositories e Services Base (Dia 3)

- [ ] Criar todos os repositories
- [ ] Criar services base (CRUD simples)
- [ ] Criar DTOs de request/response
- [ ] Implementar mappers (Entity ↔ DTO)

### Etapa 5: Regras de Negócio (Dia 4-5)

- [ ] Implementar validações de solicitação
- [ ] Implementar compatibilidade de departamento
- [ ] Implementar módulos incompatíveis
- [ ] Implementar limite de módulos
- [ ] Implementar concessão automática
- [ ] Implementar geração de protocolo
- [ ] Implementar renovação
- [ ] Implementar cancelamento

### Etapa 6: Controllers e Endpoints (Dia 5)

- [ ] Criar SolicitacaoController
- [ ] Criar ModuloController
- [ ] Implementar paginação e filtros
- [ ] Implementar exception handlers
- [ ] Validar autorização (usuário só acessa suas solicitações)

### Etapa 7: Testes (Dia 6-7)

- [ ] Testes unitários de Services (sem any())
- [ ] Testes unitários de Validators
- [ ] Testes de integração com MockMvc
- [ ] Configurar JaCoCo
- [ ] Garantir cobertura ≥ 80%
- [ ] Gerar relatório PDF

### Etapa 8: Finalização (Dia 7-8)

- [ ] Configurar Swagger
- [ ] Criar Dockerfile otimizado
- [ ] Configurar Nginx
- [ ] Testar docker-compose completo
- [ ] Testar balanceamento de carga
- [ ] Escrever README.md completo
- [ ] Testar aplicação do zero
- [ ] Organizar commits
- [ ] Criar .gitignore adequado

---

## ✅ Checklist de Qualidade

### Código

- [ ] Princípios SOLID aplicados
- [ ] Código limpo e legível
- [ ] Nomenclatura consistente (inglês)
- [ ] Sem duplicação de código
- [ ] Uso de Java 21 features (Records, Pattern Matching, etc)
- [ ] Tratamento adequado de exceções

### Testes

- [ ] Cobertura ≥ 80%
- [ ] Nenhum uso de any() no Mockito
- [ ] Uso de verify() em todos os mocks
- [ ] Cenários positivos e negativos
- [ ] Testes de integração funcionando

### Infraestrutura

- [ ] Dockerfile multi-stage otimizado
- [ ] docker-compose funcional
- [ ] Health checks configurados
- [ ] Balanceamento de carga funcional
- [ ] Rede Docker configurada

### Documentação

- [ ] README.md completo
- [ ] Swagger acessível
- [ ] Exemplos de requisições
- [ ] Decisões técnicas documentadas
- [ ] Instruções claras de execução

---

## 🎯 Critérios de Sucesso

**A implementação será considerada bem-sucedida quando:**

1. ✅ `docker-compose up -d` subir sem erros
2. ✅ Login funcionar via Postman/cURL
3. ✅ Criar solicitação e validar regras de negócio
4. ✅ Nginx balancear entre app1, app2 e app3
5. ✅ Swagger acessível e funcional
6. ✅ Todos os testes passarem
7. ✅ Cobertura ≥ 80%
8. ✅ Dados iniciais populados
9. ✅ README.md completo e claro

---

## 🚀 Diferenciais a Implementar

### Alta Prioridade

- ⭐ Migrations com Flyway (melhor que data.sql)
- ⭐ Logs estruturados com SLF4J
- ⭐ Profiles Spring (dev/prod)

### Média Prioridade

- 🌟 Diagramas de arquitetura (C4)
- 🌟 ADRs (Architecture Decision Records)

### Baixa Prioridade

- ⭐ Refresh token
- ⭐ Frontend simples

---

## 📌 Decisões Técnicas Iniciais

### 1. Banco de Dados

- **PostgreSQL 17**: Conforme especificação
- **H2**: Apenas para testes automatizados
- **Flyway**: Para migrations (melhor controle de versão)

### 2. Segurança

- **BCrypt**: Strength 12 para hashing de senhas
- **JWT**: HS512 com secret em variável de ambiente
- **Stateless**: Sessões não armazenadas no servidor

### 3. Validações

- **Bean Validation**: Validações básicas (@NotNull, @Size, etc)
- **Custom Validators**: Regras de negócio complexas
- **Service Layer**: Validações que dependem de consultas ao banco

### 4. DTOs vs Entities

- **Nunca expor entidades** nos endpoints
- **DTOs separados** para Request e Response
- **MapStruct ou manual**: Mapeamento entre DTOs e Entities

### 5. Exception Handling

- **@ControllerAdvice**: Centralizar tratamento de erros
- **Exceções customizadas**: BusinessException, NotFoundException, etc
- **Respostas padronizadas**: Formato JSON consistente

### 6. Paginação

- **Spring Data Pageable**: Padrão do Spring
- **Default**: 10 registros por página
- **Sort**: Ordenação por data (mais recentes primeiro)

### 7. Testes

- **H2 in-memory**: Para testes de integração
- **TestContainers**: Considerar para testes mais realistas (opcional)
- **@DataJpaTest**: Para testes de repositories
- **@WebMvcTest**: Para testes de controllers
- **@SpringBootTest**: Para testes de integração completos

---

## 📞 Contato e Dúvidas

**Princípio**: Fazer suposições razoáveis e documentar decisões

**Foco**: Habilidades técnicas, documentação, uso de ferramentas e entrega funcional

---

## 🏁 Conclusão

Este plano de implementação cobre todos os requisitos do teste técnico de forma estruturada e organizada. Seguindo este roteiro, teremos:

- ✅ API RESTful completa e funcional
- ✅ Segurança implementada corretamente
- ✅ Regras de negócio validadas
- ✅ Testes com alta cobertura
- ✅ Infraestrutura Docker com balanceamento de carga
- ✅ Documentação completa

**Tempo estimado: 8 dias**

**Boa sorte na implementação! 🚀**
