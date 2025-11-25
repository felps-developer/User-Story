![Supera Cover](imgs/supera_cover.jpeg)

# Sistema de Solicitação de Acesso a Módulos

## 📋 Descrição do Projeto

Sistema corporativo desenvolvido em Java/Spring Boot para gerenciamento de solicitações de acesso a módulos do sistema. O sistema permite que usuários autenticados solicitem acesso a diferentes módulos, com validação automática de regras de negócio e concessão imediata de acesso quando aprovado.

### Objetivo

Facilitar o processo de solicitação e concessão de acessos a módulos do sistema, automatizando a aprovação baseada em regras de negócio predefinidas, garantindo segurança, rastreabilidade e conformidade com políticas organizacionais.

### Principais Funcionalidades

- **Autenticação JWT**: Sistema de autenticação seguro com tokens JWT (expiração de 15 minutos)
- **Solicitação de Acesso**: Criação de solicitações com validação automática de regras de negócio
- **Concessão Automática**: Aprovação/negação automática baseada em:
  - Compatibilidade de departamento
  - Módulos mutuamente exclusivos
  - Limite de módulos por usuário
  - Validação de justificativa
- **Gestão de Solicitações**: Consulta, renovação e cancelamento de solicitações
- **Consulta de Módulos**: Listagem de módulos disponíveis com informações de compatibilidade e incompatibilidades
- **Histórico de Alterações**: Rastreamento completo de todas as mudanças nas solicitações
- **Protocolo Único**: Geração automática de protocolos no formato SOL-YYYYMMDD-NNNN

---

## 🛠️ Tecnologias Utilizadas e Versões

### Backend

| Tecnologia                      | Versão     | Descrição                           |
| ------------------------------- | ---------- | ----------------------------------- |
| **Java**                        | 21         | Linguagem de programação (LTS)      |
| **Spring Boot**                 | 3.3.5      | Framework principal da aplicação    |
| **Spring Data JPA**             | 3.3.5      | Persistência de dados               |
| **Spring Security**             | 3.3.5      | Autenticação e autorização          |
| **Spring Validation**           | 3.3.5      | Validação de dados                  |
| **SpringDoc OpenAPI (Swagger)** | 2.6.0      | Documentação da API                 |
| **JWT (jjwt)**                  | 0.12.6     | Geração e validação de tokens JWT   |
| **Lombok**                      | (incluído) | Redução de boilerplate              |
| **Maven**                       | 3.9+       | Gerenciador de dependências e build |

### Banco de Dados

| Tecnologia     | Versão     | Uso                                   |
| -------------- | ---------- | ------------------------------------- |
| **PostgreSQL** | 17         | Banco de dados de produção            |
| **H2**         | (incluído) | Banco de dados em memória para testes |
| **Flyway**     | (incluído) | Versionamento e migração do schema    |

### Testes

| Tecnologia               | Versão     | Descrição                      |
| ------------------------ | ---------- | ------------------------------ |
| **JUnit 5**              | (incluído) | Framework de testes            |
| **Mockito**              | (incluído) | Framework de mocking           |
| **MockMvc**              | (incluído) | Testes de integração HTTP      |
| **Spring Security Test** | (incluído) | Testes de segurança            |
| **JaCoCo**               | 0.8.12     | Análise de cobertura de código |
| **Instancio**            | 5.0.1      | Geração de objetos de teste    |

### Infraestrutura

| Tecnologia         | Versão | Descrição                     |
| ------------------ | ------ | ----------------------------- |
| **Docker**         | 24+    | Conteinerização               |
| **Docker Compose** | 2.x    | Orquestração de containers    |
| **Nginx**          | alpine | Load balancer e reverse proxy |

---

## 📦 Pré-requisitos

Antes de executar o projeto, certifique-se de ter instalado as seguintes ferramentas:

### Obrigatórios

- **Docker**: Versão 24 ou superior
  - Necessário para executar a aplicação e banco de dados
  - Download: https://www.docker.com/get-started
- **Docker Compose**: Versão 2.x ou superior
  - Geralmente incluído com Docker Desktop
  - Necessário para orquestrar múltiplos containers

### Opcionais (para desenvolvimento local)

- **Maven**: 3.9 ou superior
  - Necessário apenas para executar testes localmente
  - Download: https://maven.apache.org/download.cgi
- **Java 21**: (opcional, já incluído no Docker)
  - Necessário apenas para desenvolvimento sem Docker

### Verificar Instalação

Execute os seguintes comandos para verificar se as ferramentas estão instaladas:

```bash
# Verificar Docker
docker --version
# Saída esperada: Docker version 24.x.x ou superior

# Verificar Docker Compose
docker-compose --version
# Saída esperada: Docker Compose version v2.x.x ou superior

# Verificar Maven (opcional)
mvn --version
# Saída esperada: Apache Maven 3.9.x ou superior
```

### Requisitos de Sistema

- **Sistema Operacional**: Windows 10+, Linux ou macOS
- **RAM**: Mínimo 4GB (recomendado 8GB)
- **Espaço em Disco**: Mínimo 2GB livres
- **Portas Disponíveis**: 80, 5432, 8081, 8082, 8083

---

## 🚀 Como Executar Localmente com Docker

### Passo 1: Clone o repositório

```bash
git clone <url-do-repositorio>
cd User-Story
```

### Passo 2: Execute o Docker Compose

Na raiz do projeto, execute:

```bash
docker-compose up -d
```

Este comando irá:

1. **Criar a rede Docker** (`app-network`) para comunicação entre containers
2. **Iniciar PostgreSQL 17** na porta 5432
3. **Construir a imagem da aplicação** (multi-stage build)
4. **Iniciar 3 instâncias da aplicação**:
   - App 1 na porta 8081
   - App 2 na porta 8082
   - App 3 na porta 8083
5. **Iniciar Nginx** como load balancer na porta 80
6. **Executar migrations Flyway** automaticamente

### Passo 3: Aguarde a inicialização

Aguarde aproximadamente **30-60 segundos** para que todos os serviços iniciem completamente.

**Verificar status dos containers:**

```bash
docker-compose ps
```

**Acompanhar logs em tempo real:**

```bash
docker-compose logs -f
```

**Verificar logs de um serviço específico:**

```bash
docker-compose logs -f app1
docker-compose logs -f postgres
```

### Passo 4: Verificar se está funcionando

**Health Check:**

```bash
curl http://localhost/actuator/health
```

**Resposta esperada:**

```json
{"status": "UP"}
```

### Passo 5: Acesse a aplicação

| Serviço                 | URL                                    | Descrição                      |
| ----------------------- | -------------------------------------- | ------------------------------ |
| **API (Load Balancer)** | http://localhost                       | Acesso principal via Nginx     |
| **Swagger UI**          | http://localhost/swagger-ui/index.html | Documentação interativa da API |
| **App 1 (direto)**      | http://localhost:8081                  | Instância 1 da aplicação       |
| **App 2 (direto)**      | http://localhost:8082                  | Instância 2 da aplicação       |
| **App 3 (direto)**      | http://localhost:8083                  | Instância 3 da aplicação       |
| **Health Check**        | http://localhost/actuator/health       | Status de saúde da aplicação   |

### Passo 6: Parar os containers

**Parar mantendo os dados:**

```bash
docker-compose down
```

**Parar e remover volumes (limpar dados do banco):**

```bash
docker-compose down -v
```

**Parar e remover imagens:**

```bash
docker-compose down --rmi all
```

### Troubleshooting

**Problema: Porta já em uso**

```bash
# Verificar qual processo está usando a porta
netstat -ano | findstr :80

# Parar containers conflitantes
docker-compose down
```

**Problema: Containers não iniciam**

```bash
# Verificar logs de erro
docker-compose logs

# Reconstruir imagens
docker-compose up -d --build
```

**Problema: Banco de dados não conecta**

```bash
# Verificar se PostgreSQL está healthy
docker ps | grep postgres

# Verificar logs do banco
docker logs access-request-db
```

---

## 🧪 Como Executar os Testes

### Executar todos os testes

```bash
mvn test
```

**Resultado esperado:**

```
Tests run: 56, Failures: 0, Errors: 0, Skipped: 0
```

### Executar testes com cobertura JaCoCo

```bash
mvn clean test jacoco:report
```

Este comando:

- Limpa o diretório `target`
- Executa todos os testes
- Gera relatório de cobertura em `target/site/jacoco/index.html`

### Executar apenas testes unitários

```bash
mvn test -Dtest=*Test
```

Executa apenas classes de teste que terminam com `Test` (exclui `IntegrationTest`).

### Executar apenas testes de integração

```bash
mvn test -Dtest=*IntegrationTest
```

Executa apenas classes de teste que terminam com `IntegrationTest`.

### Executar teste específico

```bash
mvn test -Dtest=SolicitacaoServiceTest
```

### Validar cobertura mínima

```bash
mvn clean verify
```

Este comando:

- Executa todos os testes
- Gera relatório de cobertura
- **Falha o build** se cobertura < 80% (linhas) ou < 75% (branches)

### Estrutura de Testes

```
src/test/java/com/supera/accessrequest/
├── controller/
│   ├── AuthControllerIntegrationTest.java
│   └── ModuloControllerIntegrationTest.java
├── service/
│   ├── AuthServiceTest.java
│   ├── ModuloServiceTest.java
│   └── SolicitacaoServiceTest.java
└── util/
    └── ProtocoloGeneratorTest.java
```

**Total:** 56 testes cobrindo todos os cenários de negócio.

---

## 📊 Como Visualizar Relatório de Cobertura

### 1️⃣ Relatório JaCoCo Básico (HTML)

Gera relatório HTML interativo com gráficos de cobertura:

```bash
mvn clean test jacoco:report
```

**Arquivos gerados:**

- `target/site/jacoco/index.html` - Relatório HTML interativo (abrir no navegador)
- `target/site/jacoco/jacoco.xml` - Relatório XML (para integração com CI/CD)
- `target/site/jacoco/jacoco.csv` - Relatório CSV (para análise em planilhas)

**Abrir no navegador:**

```bash
# Windows
start target\site\jacoco\index.html

# Linux
xdg-open target/site/jacoco/index.html

# macOS
open target/site/jacoco/index.html
```

### 2️⃣ Relatório Completo com Site Maven

Gera site completo com relatórios detalhados e gráficos:

```bash
mvn clean verify site -Prelatorio-completo
```

**Arquivos gerados:**

- `target/site/index.html` - Página principal do site Maven
- `target/site/jacoco/index.html` - Relatório JaCoCo com gráficos visuais
- `target/site/surefire-report.html` - Relatório consolidado de testes
- `target/surefire-reports/` - Relatórios detalhados em XML/TXT por classe
- Múltiplos formatos: HTML, XML, CSV

**Abrir site completo:**

```bash
# Windows
start target\site\index.html

# Linux/Mac
open target/site/index.html
```

### 3️⃣ Gerar PDF do Relatório JaCoCo

Para gerar o relatório em PDF, siga os passos abaixo:

**1. Gere o relatório HTML primeiro:**

```bash
mvn clean test jacoco:report
```

**2. Abra o relatório no navegador:**

```bash
# Windows
start target\site\jacoco\index.html

# Linux/Mac
open target/site/jacoco/index.html
```

**3. No navegador, pressione `Ctrl + P` (Imprimir)**

**4. Configure a impressão:**

- **Destino:** Selecione **"Salvar como PDF"** ou **"Microsoft Print to PDF"**
- **Layout:** **Paisagem** (recomendado para tabelas)
- **Páginas:** Todas
- **Margens:** Padrão ou Mínimas
- **Opções adicionais:**
  - ✅ Marque **"Gráficos de fundo"** (para manter as cores dos gráficos)
  - ✅ Marque **"Cabeçalhos e rodapés"** (para data/hora)

**5. Clique em "Salvar"**

**Nome sugerido:** `relatorio-jacoco-cobertura-AAAA-MM-DD.pdf`

### 4️⃣ Visualizando os Gráficos

Os relatórios incluem gráficos visuais de:

- ✅ **Cobertura de Instruções** (linha por linha)
- ✅ **Cobertura de Branches** (condicionais)
- ✅ **Cobertura de Métodos**
- ✅ **Cobertura de Classes**
- ✅ **Complexidade Ciclomática**

Todos com indicadores visuais de cores:

- 🟢 Verde: Alta cobertura (≥ 80%)
- 🟡 Amarelo: Cobertura média (50-79%)
- 🔴 Vermelho: Baixa cobertura (< 50%)

---

## 🔐 Credenciais para Teste

O sistema vem pré-configurado com os seguintes usuários para testes. Todos possuem a senha padrão: `senha123`

| Nome         | Email                    | Departamento | Senha    | Permissões de Módulos                |
| ------------ | ------------------------ | ------------ | -------- | ------------------------------------ |
| João Silva   | joao.silva@empresa.com   | TI           | senha123 | Todos os módulos                     |
| Maria Santos | maria.santos@empresa.com | FINANCEIRO   | senha123 | Financeiro, Relatórios, Portal       |
| Pedro Costa  | pedro.costa@empresa.com  | RH           | senha123 | RH, Relatórios, Portal               |
| Ana Oliveira | ana.oliveira@empresa.com | OPERACOES    | senha123 | Estoque, Compras, Relatórios, Portal |
| Carlos Souza | carlos.souza@empresa.com | OUTROS       | senha123 | Portal, Relatórios                   |

**Importante**:

- As senhas são criptografadas com **BCrypt (strength 12)** no banco de dados
- Os usuários são criados automaticamente via migrations Flyway
- Todos os usuários estão ativos e prontos para uso

---

## 📝 Exemplos de Requisições

### 1. Autenticação (Login)

```bash
curl -X POST http://localhost/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao.silva@empresa.com",
    "senha": "senha123"
  }'
```

**Resposta:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tipo": "Bearer",
  "usuarioId": 1,
  "nome": "João Silva",
  "email": "joao.silva@empresa.com",
  "departamento": "TI"
}
```

### 2. Listar Módulos Disponíveis

```bash
curl -X GET http://localhost/api/modulos \
  -H "Authorization: Bearer {seu-token-jwt}"
```

### 3. Criar Solicitação de Acesso

```bash
curl -X POST http://localhost/api/solicitacoes \
  -H "Authorization: Bearer {seu-token-jwt}" \
  -H "Content-Type: application/json" \
  -d '{
    "modulosIds": [1, 2],
    "justificativa": "Preciso acessar o Portal do Colaborador e Relatórios Gerenciais para realizar minhas atividades diárias de análise e acompanhamento de métricas.",
    "urgente": false
  }'
```

**Resposta (Aprovado):**

```json
{
  "mensagem": "Solicitação criada com sucesso! Protocolo: SOL-20241124-0001. Seus acessos já estão disponíveis!",
  "dados": {
    "id": 1,
    "protocolo": "SOL-20241124-0001",
    "status": "ATIVO",
    "modulos": [...],
    "justificativa": "...",
    "dataSolicitacao": "2024-11-24T10:00:00",
    "dataExpiracao": "2025-05-23T10:00:00"
  }
}
```

### 4. Listar Minhas Solicitações

```bash
curl -X GET "http://localhost/api/solicitacoes?page=0&size=10" \
  -H "Authorization: Bearer {seu-token-jwt}"
```

### 5. Buscar Solicitação por ID

```bash
curl -X GET http://localhost/api/solicitacoes/1 \
  -H "Authorization: Bearer {seu-token-jwt}"
```

### 6. Renovar Solicitação

```bash
curl -X PUT http://localhost/api/solicitacoes/1/renovar \
  -H "Authorization: Bearer {seu-token-jwt}"
```

### 7. Cancelar Solicitação

```bash
curl -X PUT http://localhost/api/solicitacoes/1/cancelar \
  -H "Authorization: Bearer {seu-token-jwt}" \
  -H "Content-Type: application/json" \
  -d '{
    "motivo": "Não preciso mais deste acesso pois mudei de departamento."
  }'
```

---

## 🏗️ Arquitetura da Solução

### Visão Geral da Infraestrutura

O sistema foi desenvolvido seguindo uma arquitetura em camadas (Layered Architecture) com balanceamento de carga e alta disponibilidade:

```
┌─────────────────────────────────────────────────────────┐
│              Cliente / Navegador / Postman               │
└────────────────────┬────────────────────────────────────┘
                     │ HTTP/HTTPS
                     ▼
┌─────────────────────────────────────────────────────────┐
│              Nginx (Load Balancer)                      │
│              Porta: 80                                  │
│              Estratégia: least_conn                     │
└────────────────────┬────────────────────────────────────┘
                     │
        ┌────────────┼────────────┐
        │            │            │
        ▼            ▼            ▼
┌───────────┐ ┌───────────┐ ┌───────────┐
│   App 1   │ │   App 2   │ │   App 3   │
│  :8081    │ │  :8082    │ │  :8083    │
│ (healthy) │ │ (healthy) │ │ (healthy) │
└─────┬─────┘ └─────┬─────┘ └─────┬─────┘
      │             │             │
      └─────────────┼─────────────┘
                    │ JDBC
                    ▼
        ┌───────────────────────┐
        │   PostgreSQL 17       │
        │   Porta: 5432         │
        │   (healthy)           │
        └───────────────────────┘

    ┌───────────────────────────────┐
    │   Rede Docker: app-network    │
    │   Driver: bridge              │
    │   DNS Interno: app1, app2...  │
    └───────────────────────────────┘
```

### Camadas da Aplicação

A aplicação segue o padrão de arquitetura em camadas:

```
┌─────────────────────────────────────────┐
│  Controller Layer (REST API)           │
│  - AuthController                       │
│  - SolicitacaoController                │
│  - ModuloController                     │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│  Service Layer (Lógica de Negócio)      │
│  - AuthService                          │
│  - SolicitacaoService                   │
│  - ModuloService                        │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│  Repository Layer (Acesso a Dados)      │
│  - JPA Repositories                     │
│  - Queries customizadas                 │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│  Entity Layer (Modelo de Domínio)       │
│  - Usuario, Solicitacao, Modulo, etc.   │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│  Database Layer (PostgreSQL)           │
│  - Tabelas relacionais                  │
│  - Índices e constraints               │
└─────────────────────────────────────────┘
```

**Camadas de Suporte:**

- **Security Layer**: Autenticação JWT e autorização
- **Exception Handling**: Tratamento global de exceções
- **Validation Layer**: Validação de dados de entrada
- **DTO Layer**: Transferência de dados (Records)

### Fluxo de Autenticação

```
┌────────┐
│Cliente │
└───┬────┘
    │ POST /api/auth/login
    │ { email, senha }
    ▼
┌─────────────────────┐
│  AuthController     │
└───────┬─────────────┘
        │
        ▼
┌─────────────────────┐
│  AuthService        │
│  - Valida credenciais│
│  - Gera token JWT   │
└───────┬─────────────┘
        │
        ▼
┌─────────────────────┐
│  JwtTokenProvider   │
│  - Expiração: 15min │
└───────┬─────────────┘
        │
        ▼
┌─────────────────────┐
│  Retorna:           │
│  { token, tipo,     │
│    usuarioId, ... } │
└─────────────────────┘
```

### Fluxo de Solicitação de Acesso

```
┌────────┐
│Cliente │
└───┬────┘
    │ POST /api/solicitacoes
    │ Authorization: Bearer {token}
    │ { modulosIds, justificativa, urgente }
    ▼
┌─────────────────────┐
│ SolicitacaoController│
└───────┬─────────────┘
        │
        ▼
┌─────────────────────┐
│ SolicitacaoService  │
│                     │
│ 1. Validações:      │
│    - Usuário auth   │
│    - Módulos válidos│
│    - Sem duplicatas │
│                     │
│ 2. Regras Negócio:  │
│    - Depto permitido│
│    - Sem conflitos  │
│    - Limite módulos │
│    - Justificativa  │
│                     │
│ 3. Decisão:         │
│    - Aprovado → ATIVO│
│    - Negado → NEGADO│
└───────┬─────────────┘
        │
        ├─ Aprovado ──────► Cria AcessoUsuarioModulo
        │                    Status: ATIVO
        │                    Expiração: +180 dias
        │
        └─ Negado ─────────► Status: NEGADO
                             Motivo: {razão}
```

### Balanceamento de Carga

**Estratégia:** `least_conn` (menor número de conexões)

**Configuração Nginx:**

```nginx
upstream backend {
    least_conn;
    server app1:8080 max_fails=3 fail_timeout=30s;
    server app2:8080 max_fails=3 fail_timeout=30s;
    server app3:8080 max_fails=3 fail_timeout=30s;
}
```

**Características:**

- ✅ **Stateless**: Não mantém sessão fixa
- ✅ **Failover**: Remove instâncias com falha automaticamente
- ✅ **Health Checks**: Verifica saúde das instâncias
- ✅ **Distribuição Equilibrada**: Balanceia por menor número de conexões

---

## 🔧 Decisões Técnicas Relevantes

### 1. Uso de Flyway para Migrations

**Decisão**: Utilizar Flyway ao invés de `ddl-auto: create-drop` para garantir versionamento e controle das mudanças no banco de dados.

**Justificativa**:

- Controle de versão do schema permite rastreabilidade
- Facilita deploy em diferentes ambientes (dev, staging, prod)
- Histórico completo de mudanças no banco

**Benefícios**:

- ✅ Versionamento do schema do banco de dados
- ✅ Migrations idempotentes e reversíveis
- ✅ Facilita rollback em caso de problemas
- ✅ Histórico completo de mudanças

**Implementação**: Migrations em `src/main/resources/db/migration/`

---

### 2. Multi-stage Docker Build

**Decisão**: Dockerfile com multi-stage build para otimizar o tamanho da imagem final.

**Justificativa**:

- Reduz tamanho da imagem final (de ~500MB para ~200MB)
- Remove ferramentas de build (Maven) da imagem de produção
- Melhora segurança (menos superfície de ataque)

**Benefícios**:

- ✅ Imagem final menor (apenas JRE 21, sem Maven)
- ✅ Build mais rápido em produção
- ✅ Separação clara entre build e runtime
- ✅ Melhor segurança (menos dependências)

**Estrutura**:

```dockerfile
# Stage 1: Build (Maven + JDK)
FROM maven:3.9-eclipse-temurin-21 AS build
# ... compila aplicação ...

# Stage 2: Runtime (apenas JRE)
FROM eclipse-temurin:21-jre-alpine
# ... copia apenas o JAR ...
```

---

### 3. JWT com Expiração de 15 minutos

**Decisão**: Tokens JWT com expiração curta (15 minutos) conforme requisito de segurança.

**Justificativa**:

- Reduz janela de ataque em caso de token comprometido
- Força renovação frequente de autenticação
- Alinha com boas práticas de segurança

**Benefícios**:

- ✅ Maior segurança (tokens expiram rapidamente)
- ✅ Reduz risco de uso indevido de tokens comprometidos
- ✅ Conformidade com requisitos de segurança

**Implementação**: Configurado em `JwtTokenProvider` com expiração de 15 minutos.

---

### 4. BCrypt com Strength 12

**Decisão**: Criptografia de senhas com BCrypt usando strength 12.

**Justificativa**:

- Strength 12 oferece excelente segurança
- Resistente a ataques de força bruta
- Padrão da indústria para aplicações corporativas

**Benefícios**:

- ✅ Alta segurança (resistente a ataques de força bruta)
- ✅ Padrão da indústria para aplicações corporativas
- ✅ Balanceamento entre segurança e performance

**Implementação**: Configurado no `SecurityConfig` com `BCryptPasswordEncoder`.

---

### 5. Testes sem uso de `any()` do Mockito

**Decisão**: Proibição do uso de `any()`, `anyString()`, etc. nos testes, exigindo valores específicos.

**Justificativa**:

- Testes mais explícitos são mais confiáveis
- Facilita identificação de problemas
- Melhora manutenibilidade

**Benefícios**:

- ✅ Testes mais explícitos e claros
- ✅ Maior confiabilidade (testa exatamente o que deve ser testado)
- ✅ Facilita manutenção e debugging
- ✅ Melhor documentação do comportamento esperado

**Exemplo**:

```java
// ❌ Evitado
when(repository.findById(any())).thenReturn(optional);

// ✅ Preferido
when(repository.findById(eq(1L))).thenReturn(optional);
```

---

### 6. JaCoCo com Regras de Cobertura

**Decisão**: Configuração do JaCoCo para falhar o build se cobertura < 80% (linhas) e < 75% (branches).

**Justificativa**:

- Garante qualidade mínima do código
- Força escrita de testes adequados
- Previne regressões

**Benefícios**:

- ✅ Garante qualidade mínima do código
- ✅ Força escrita de testes adequados
- ✅ Previne deploy de código sem testes

**Configuração**: Regras definidas no `pom.xml` com falha automática do build.

---

### 7. Três Instâncias da Aplicação

**Decisão**: Executar 3 instâncias da aplicação para demonstrar balanceamento de carga.

**Justificativa**:

- Demonstra capacidade de alta disponibilidade
- Permite distribuição de carga
- Facilita escalabilidade horizontal

**Benefícios**:

- ✅ Alta disponibilidade (se uma instância cair, outras continuam)
- ✅ Distribuição de carga
- ✅ Demonstração prática de load balancing
- ✅ Facilita escalabilidade futura

**Implementação**: 3 containers Docker (app1, app2, app3) com Nginx como load balancer.

---

### 8. Nginx como Load Balancer

**Decisão**: Utilizar Nginx ao invés de outras soluções para balanceamento de carga.

**Justificativa**:

- Leve e performático
- Fácil configuração
- Amplamente utilizado e testado na indústria

**Benefícios**:

- ✅ Leve e performático
- ✅ Fácil configuração
- ✅ Amplamente utilizado na indústria
- ✅ Suporte a múltiplas estratégias de balanceamento

**Estratégia**: `least_conn` (menor número de conexões) para distribuição equilibrada.

---

### 9. Records do Java para DTOs

**Decisão**: Utilizar Records (Java 14+) para DTOs ao invés de classes tradicionais.

**Justificativa**:

- Código mais conciso e legível
- Imutabilidade por padrão
- Menos boilerplate

**Benefícios**:

- ✅ Código mais conciso (menos linhas)
- ✅ Imutabilidade por padrão (mais seguro)
- ✅ Menos boilerplate (equals, hashCode, toString automáticos)
- ✅ Melhor performance (menos overhead)

**Exemplo**:

```java
// Record (Java 14+)
public record SolicitacaoRequest(
    List<Long> modulosIds,
    String justificativa,
    Boolean urgente
) {}
```

---

### 10. Spring Profiles

**Decisão**: Configuração de profiles (dev, test, prod) para diferentes ambientes.

**Justificativa**:

- Permite configurações específicas por ambiente
- Facilita testes e desenvolvimento
- Melhor organização

**Benefícios**:

- ✅ Configurações específicas por ambiente
- ✅ Facilita testes e desenvolvimento
- ✅ Melhor organização
- ✅ Facilita deploy em diferentes ambientes

**Profiles configurados**:

- `test`: H2 em memória para testes
- `prod`: PostgreSQL para produção
- `dev`: Configurações de desenvolvimento

---
