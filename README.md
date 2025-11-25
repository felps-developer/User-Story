![Supera Cover](imgs/supera_cover.jpeg)

# Sistema de Solicitação de Acesso a Módulos

## 📋 Descrição do Projeto

Sistema corporativo desenvolvido em Java/Spring Boot para gerenciamento de solicitações de acesso a módulos do sistema. O sistema permite que usuários autenticados solicitem acesso a diferentes módulos, com validação automática de regras de negócio e concessão imediata de acesso quando aprovado.

### Principais Funcionalidades

- **Autenticação JWT**: Sistema de autenticação seguro com tokens JWT
- **Solicitação de Acesso**: Criação de solicitações com validação automática de regras
- **Concessão Automática**: Aprovação/negação automática baseada em regras de negócio
- **Gestão de Solicitações**: Consulta, renovação e cancelamento de solicitações
- **Consulta de Módulos**: Listagem de módulos disponíveis com informações de compatibilidade

---

## 🛠️ Tecnologias Utilizadas e Versões

### Backend

- **Java**: 21
- **Spring Boot**: 3.3.5
- **Spring Data JPA**: 3.3.5
- **Spring Security**: 3.3.5
- **Spring Validation**: 3.3.5
- **SpringDoc OpenAPI (Swagger)**: 2.6.0
- **JWT (jjwt)**: 0.12.6
- **Lombok**: (incluído no Spring Boot)
- **Maven**: 3.9+

### Banco de Dados

- **PostgreSQL**: 17 (produção)
- **H2**: (apenas para testes)
- **Flyway**: (migrations)

### Testes

- **JUnit 5**: (incluído no Spring Boot)
- **Mockito**: (incluído no Spring Boot)
- **MockMvc**: (incluído no Spring Boot)
- **Spring Security Test**: (incluído no Spring Boot)
- **JaCoCo**: 0.8.12
- **Instancio**: 5.0.1

### Infraestrutura

- **Docker**: 24+
- **Docker Compose**: 2.x
- **Nginx**: alpine (load balancer)

---

## 📦 Pré-requisitos

Antes de executar o projeto, certifique-se de ter instalado:

- **Docker**: Versão 24 ou superior
- **Docker Compose**: Versão 2.x ou superior
- **Maven**: 3.9 ou superior (para execução local dos testes)

### Verificar Instalação

```bash
docker --version
docker-compose --version
mvn --version
```

---

## 🚀 Como Executar Localmente com Docker

### 1. Clone o repositório (se aplicável)

```bash
git clone <url-do-repositorio>
cd User-Story
```

### 2. Execute o Docker Compose

Na raiz do projeto, execute:

```bash
docker-compose up -d
```

Este comando irá:

- Criar e iniciar o container do PostgreSQL 17
- Construir e iniciar 3 instâncias da aplicação (app1, app2, app3)
- Iniciar o Nginx como load balancer
- Configurar a rede Docker para comunicação entre containers

### 3. Aguarde a inicialização

Aguarde alguns segundos para que todos os serviços iniciem completamente. Você pode verificar os logs:

```bash
docker-compose logs -f
```

### 4. Acesse a aplicação

- **API via Load Balancer**: http://localhost
- **Swagger UI**: http://localhost/swagger-ui.html
- **App 1 (direto)**: http://localhost:8081
- **App 2 (direto)**: http://localhost:8082
- **App 3 (direto)**: http://localhost:8083
- **Health Check**: http://localhost/actuator/health

### 5. Parar os containers

```bash
docker-compose down
```

Para remover também os volumes (dados do banco):

```bash
docker-compose down -v
```

---

## 🧪 Como Executar os Testes

### Executar todos os testes

```bash
mvn test
```

### Executar testes com cobertura JaCoCo

```bash
mvn clean test jacoco:report
```

### Executar apenas testes unitários

```bash
mvn test -Dtest=*Test
```

### Executar apenas testes de integração

```bash
mvn test -Dtest=*IntegrationTest
```

---

## 📊 Como Visualizar Relatório de Cobertura

### 1️⃣ Relatório JaCoCo Básico (HTML)

Gera relatório HTML com gráficos de cobertura:

```bash
mvn clean test jacoco:report
```

**Arquivos gerados:**
- `target/site/jacoco/index.html` - Relatório HTML interativo
- `target/site/jacoco/jacoco.xml` - Relatório XML
- `target/site/jacoco/jacoco.csv` - Relatório CSV

### 2️⃣ Relatório Completo com Site Maven

Gera site completo com relatórios detalhados e gráficos:

```bash
mvn clean verify site -Prelatorio-completo
```

**Arquivos gerados:**
- `target/site/index.html` - Site completo com todos os relatórios
- `target/site/jacoco/index.html` - Relatório JaCoCo com gráficos
- `target/surefire-reports/` - Relatórios detalhados dos testes
- Múltiplos formatos: HTML, XML, CSV

### 3️⃣ Scripts Automatizados (Opcional)

**Windows (PowerShell):**

```powershell
.\open-jacoco-report.ps1
```

**Windows (Batch):**

```cmd
open-jacoco-report.bat
```

### 4️⃣ Gerar PDF do Relatório JaCoCo

#### **📝 PASSO A PASSO COMPLETO:**

**1. Gere o relatório primeiro:**

```bash
mvn clean verify site -Prelatorio-completo
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

**6. Nome sugerido:** `relatorio-jacoco-cobertura-AAAA-MM-DD.pdf`

#### **🎨 Dicas para PDF Profissional:**

**Para incluir detalhes específicos:**
```
1. Abra o relatório principal (index.html)
2. Clique no pacote/classe que deseja detalhar
3. Gere PDF individual de cada seção importante
4. Use ferramentas para mesclar PDFs se necessário
```

**Ajustes de visualização antes de salvar:**
- **Zoom:** 80-90% (para caber mais informação por página)
- **Imprimir em:** Cores (recomendado para ver status verde/vermelho)
- **Qualidade:** Alta

#### **🔧 Método Alternativo - Linha de Comando:**

Se tiver Chrome instalado, pode usar linha de comando:

**Windows:**
```powershell
# Caminho padrão do Chrome
$chrome = "C:\Program Files\Google\Chrome\Application\chrome.exe"
& $chrome --headless --disable-gpu --print-to-pdf="relatorio-jacoco.pdf" "$PWD\target\site\jacoco\index.html"
```

**Linux/Mac:**
```bash
google-chrome --headless --disable-gpu --print-to-pdf=relatorio-jacoco.pdf target/site/jacoco/index.html
```

#### **📦 Ferramentas de Conversão Profissionais:**

Para PDFs mais sofisticados, você pode usar:

| Ferramenta | Descrição | Comando |
|------------|-----------|---------|
| [wkhtmltopdf](https://wkhtmltopdf.org/) | Conversor HTML para PDF | `wkhtmltopdf --enable-local-file-access target/site/jacoco/index.html relatorio.pdf` |
| [Puppeteer](https://pptr.dev/) | Automação Chrome/Node.js | Requer script Node.js |
| [WeasyPrint](https://weasyprint.org/) | Python para PDF | `weasyprint target/site/jacoco/index.html relatorio.pdf` |

#### **📊 Conteúdo do PDF Gerado:**

O PDF incluirá:
- ✅ Tabela de cobertura geral (% de instruções, branches, métodos)
- ✅ Gráficos de barras coloridos por pacote
- ✅ Lista de todas as classes com percentuais
- ✅ Indicadores visuais (verde/amarelo/vermelho)
- ✅ Total de linhas testadas vs não testadas
- ✅ Complexidade ciclomática

### 📈 Visualizando os Gráficos

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

## ⚙️ Comandos Maven Disponíveis

### Comandos Básicos

```bash
# Compilar o projeto
mvn compile

# Limpar o projeto
mvn clean

# Executar testes
mvn test

# Gerar JAR
mvn package

# Instalar no repositório local
mvn install
```

### Comandos de Testes e Cobertura

```bash
# Testes com cobertura básica
mvn clean test jacoco:report

# Testes com cobertura e validação (verifica se atingiu 80%)
mvn clean verify

# Apenas relatório JaCoCo (sem executar testes novamente)
mvn jacoco:report

# Relatório completo com site Maven
mvn clean verify site -Prelatorio-completo
```

### Comandos de Análise

```bash
# Verificar dependências desatualizadas
mvn versions:display-dependency-updates

# Verificar plugins desatualizados
mvn versions:display-plugin-updates

# Análise de dependências
mvn dependency:tree

# Análise de dependências conflitantes
mvn dependency:analyze
```

### Perfis Disponíveis

O projeto possui os seguintes perfis Maven:

| Profile | Descrição | Comando |
|---------|-----------|---------|
| `relatorio-completo` | Gera site completo com relatórios detalhados | `mvn verify -Prelatorio-completo` |

### Estrutura de Relatórios Gerados

```
target/
├── site/
│   ├── index.html              # Página principal do site
│   ├── jacoco/
│   │   ├── index.html          # Relatório JaCoCo visual
│   │   ├── jacoco.xml          # Relatório XML
│   │   └── jacoco.csv          # Relatório CSV
│   └── surefire-report.html    # Relatório de testes
├── surefire-reports/           # Relatórios detalhados em XML/TXT
└── jacoco.exec                 # Arquivo binário de cobertura
```

---

## 🔐 Credenciais para Teste

O sistema vem pré-configurado com os seguintes usuários (senha padrão: `senha123`):

| Nome         | Email                    | Departamento | Senha    |
| ------------ | ------------------------ | ------------ | -------- |
| João Silva   | joao.silva@empresa.com   | TI           | senha123 |
| Maria Santos | maria.santos@empresa.com | FINANCEIRO   | senha123 |
| Pedro Costa  | pedro.costa@empresa.com  | RH           | senha123 |
| Ana Oliveira | ana.oliveira@empresa.com | OPERACOES    | senha123 |
| Carlos Souza | carlos.souza@empresa.com | OUTROS       | senha123 |

**Importante**: As senhas são criptografadas com BCrypt (strength 12) no banco de dados.

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

### Visão Geral

O sistema foi desenvolvido seguindo uma arquitetura em camadas (Layered Architecture) com os seguintes componentes:

```
┌─────────────────────────────────────────────────────────┐
│                    Nginx (Load Balancer)                │
│                    Porta: 80                            │
└────────────────────┬────────────────────────────────────┘
                     │
        ┌────────────┼────────────┐
        │            │            │
┌───────▼───┐ ┌──────▼──────┐ ┌──▼───────┐
│   App 1   │ │    App 2    │ │   App 3  │
│  :8081    │ │    :8082     │ │  :8083   │
└───────┬───┘ └──────┬──────┘ └──┬───────┘
        │            │            │
        └────────────┼────────────┘
                     │
        ┌────────────▼────────────┐
        │   PostgreSQL 17         │
        │   Porta: 5432           │
        └─────────────────────────┘
```

### Camadas da Aplicação

1. **Controller Layer**: Endpoints REST (`AuthController`, `SolicitacaoController`, `ModuloController`)
2. **Service Layer**: Lógica de negócio (`AuthService`, `SolicitacaoService`, `ModuloService`)
3. **Repository Layer**: Acesso a dados (JPA Repositories)
4. **Entity Layer**: Modelos de domínio (JPA Entities)
5. **Security Layer**: Autenticação e autorização JWT
6. **Exception Handling**: Tratamento global de exceções

### Fluxo de Autenticação

```
Cliente → POST /api/auth/login
         ↓
    AuthService valida credenciais
         ↓
    Gera token JWT (expira em 15 min)
         ↓
    Retorna token para cliente
         ↓
Cliente usa token em requisições: Authorization: Bearer {token}
```

### Fluxo de Solicitação de Acesso

```
Cliente → POST /api/solicitacoes (com token JWT)
         ↓
    SolicitacaoService valida:
    - Usuário autenticado
    - Módulos válidos
    - Regras de negócio
         ↓
    Aplica regras:
    - Compatibilidade de departamento
    - Módulos mutuamente exclusivos
    - Limite de módulos
    - Validação de justificativa
         ↓
    Se aprovado: Cria acesso e retorna ATIVO
    Se negado: Retorna NEGADO com motivo
```

### Balanceamento de Carga

O Nginx utiliza a estratégia `least_conn` (menor número de conexões) para distribuir as requisições entre as 3 instâncias da aplicação, garantindo melhor distribuição de carga.

---

## 🔧 Decisões Técnicas Relevantes

### 1. Uso de Flyway para Migrations

**Decisão**: Utilizar Flyway ao invés de `ddl-auto: create-drop` para garantir versionamento e controle das mudanças no banco de dados.

**Benefícios**:

- Controle de versão do schema
- Facilita deploy em diferentes ambientes
- Histórico de mudanças

### 2. Multi-stage Docker Build

**Decisão**: Dockerfile com multi-stage build para otimizar o tamanho da imagem final.

**Benefícios**:

- Imagem final menor (apenas JRE, sem Maven)
- Build mais rápido em produção
- Separação de responsabilidades

### 3. JWT com Expiração de 15 minutos

**Decisão**: Tokens JWT com expiração curta (15 minutos) conforme requisito de segurança.

**Benefícios**:

- Maior segurança (tokens expiram rapidamente)
- Reduz risco de uso indevido de tokens comprometidos

### 4. BCrypt com Strength 12

**Decisão**: Criptografia de senhas com BCrypt usando strength 12.

**Benefícios**:

- Alta segurança (resistente a ataques de força bruta)
- Padrão da indústria para aplicações corporativas

### 5. Testes sem uso de `any()` do Mockito

**Decisão**: Proibição do uso de `any()`, `anyString()`, etc. nos testes, exigindo valores específicos.

**Benefícios**:

- Testes mais explícitos e claros
- Maior confiabilidade (testa exatamente o que deve ser testado)
- Facilita manutenção

### 6. JaCoCo com Regras de Cobertura

**Decisão**: Configuração do JaCoCo para falhar o build se cobertura < 80% (linhas) e < 75% (branches).

**Benefícios**:

- Garante qualidade mínima do código
- Força escrita de testes adequados

### 7. Três Instâncias da Aplicação

**Decisão**: Executar 3 instâncias da aplicação para demonstrar balanceamento de carga.

**Benefícios**:

- Alta disponibilidade
- Distribuição de carga
- Demonstração prática de load balancing

### 8. Nginx como Load Balancer

**Decisão**: Utilizar Nginx ao invés de outras soluções para balanceamento de carga.

**Benefícios**:

- Leve e performático
- Fácil configuração
- Amplamente utilizado na indústria

### 9. Records do Java para DTOs

**Decisão**: Utilizar Records (Java 14+) para DTOs ao invés de classes tradicionais.

**Benefícios**:

- Código mais conciso
- Imutabilidade por padrão
- Menos boilerplate

### 10. Spring Profiles

**Decisão**: Configuração de profiles (dev, test, prod) para diferentes ambientes.

**Benefícios**:

- Configurações específicas por ambiente
- Facilita testes e desenvolvimento
- Melhor organização

---

# Teste técnico para Desenvolvedor Java Pleno

## Introdução

Este teste técnico é direcionado para profissionais que tenham interesse em atuar como Desenvolvedor Pleno Java, no desenvolvimento de softwares variados.

O processo seletivo prevê a contratação de 1 profissional para atuação imediata.

**O teste consiste nas seguintes partes:**

- Analise e entendimento dos requisitos em formato de User Story.
- Arquitetura da solução (API).
- Arquitetura de Infraestrutura em Containers/Docker.
- Apresentação Técnica: Reunião online para apresentação da solução desenvolvida.

## Uso de IA

Uso de IA (Cursor, Claude Code, Copilot, etc) é permitido e recomendado para execução do teste, desde que a IA seja apenas um assistente e você saiba responder os questionamentos na entrevista técnica após aprovação do teste.

O responsável pelo código entregue, qualidade, testabilidade e funcionamento é do desenvolvedor.

## Objetivos Técnicos

Este teste técnico tem como objetivo avaliar suas habilidades em:

- Desenvolvimento de APIs RESTful com Java/Spring Boot
- Implementação de autenticação e autorização
- Modelagem de dados e relacionamentos
- Implementação de regras de negócio
- Testes unitários e de integração com alta cobertura
- Conteinerização com Docker
- Configuração de balanceamento de carga

## Contexto do Teste

Você foi designado para desenvolver uma funcionalidade de **Solicitação de Acesso a Módulos** para um sistema corporativo. Usuários autenticados podem solicitar acesso a diferentes módulos do sistema, e o acesso é concedido automaticamente após validação das regras de negócio.

## Sumário

# User Story

## Solicitação de Acesso a Módulos

**Eu** como usuário autenticado no sistema

**Quero** solicitar acesso a módulos específicos

**Para** realizar minhas atividades profissionais de acordo com minhas necessidades e ter as funcionalidades liberadas automaticamente

## Critérios de Aceite

### Autenticação de Usuário

- O sistema deve permitir autenticar utilizando E-mail e Senha.
- Quando informado usuário ou senha inválidos o login deve ser impedido.

### Cadastro de Solicitação

- O sistema deve permitir que um usuário autenticado crie uma nova solicitação de acesso contendo:
  - **Módulos solicitados**: Multi-seleção, obrigatório (mínimo 1, máximo 3 módulos)
  - **Justificativa**: Campo texto, obrigatório (mínimo 20, máximo 500 caracteres)
  - **Urgente**: Campo que indica se a solicitação é urgente ou não.
- O sistema deve identificar automaticamente as informações do solicitante
  - ID do usuário solicitante
  - Departamento do usuário
- O sistema deve validar:
  - Usuário não pode ter solicitação ativa para o mesmo módulo
  - Usuário não pode solicitar módulo que já possui acesso ativo
  - Justificativa não pode conter apenas texto genérico (ex: "teste", "aaa", "preciso")
  - Cada módulo solicitado deve estar ativo e disponível
- Ao criar a solicitação com sucesso, o sistema deve:
  - Gerar um número único de protocolo (formato: SOL-YYYYMMDD-NNNN)
  - Registrar data/hora da solicitação
  - Validar regras de negócio e conceder acesso automaticamente se aprovado
  - Atribuir status: "ATIVO" (se aprovado) ou "NEGADO" (se reprovado por regra)
  - Retornar mensagem:
    - Se aprovado: "Solicitação criada com sucesso! Protocolo: {número}. Seus acessos já estão disponíveis!"
    - Se negado: "Solicitação negada. Motivo: {motivo da negação}"

### Concessão Automática

- **Compatibilidade de Departamento**:
  - Usuários do departamento "TI" podem acessar todos os módulos
  - Usuários do departamento "Financeiro" podem acessar: Financeiro, Relatórios, Portal
  - Usuários do departamento "RH" podem acessar: RH, Relatórios, Portal
  - Usuários do departamento "Operações" podem acessar: Estoque, Compras, Relatórios, Portal
  - Outros departamentos podem acessar apenas: Portal e Relatórios
- **Módulos Mutuamente Exclusivos**:
  - Não é permitido ter acesso simultâneo a "Aprovador Financeiro" e "Solicitante Financeiro"
  - Não é permitido ter acesso simultâneo a "Administrador RH" e "Colaborador RH"
- **Limite de Módulos por Usuário**:
  - Máximo de 5 módulos ativos simultaneamente por usuário
  - Usuários do departamento "TI" têm limite de 10 módulos
- **Motivos de Negação Automática**:
  - "Departamento sem permissão para acessar este módulo"
  - "Módulo incompatível com outro módulo já ativo em seu perfil"
  - "Limite de módulos ativos atingido"
  - "Justificativa insuficiente ou genérica"

### Consulta de Solicitações

- O sistema deve permitir ao usuário consultar apenas suas próprias solicitações
- Filtros disponíveis:
  - **Pesquisa por texto**: Busca por protocolo ou nome do módulo
  - **Status**: (Ativo, Negado, Cancelado)
  - **Período**: Data início e data fim
  - **Urgente**: Sim ou Não
- A consulta deve retornar uma lista paginada contendo:
  - Protocolo
  - Módulos solicitados
  - Status
  - Justificativa
  - Marcação de urgente (se aplicável)
  - Data da solicitação
  - Data de expiração (180 dias após concessão)
  - Motivo da negação (se aplicável)
- Paginação: 10 registros por página
- Ordenação padrão: Mais recentes primeiro

### Visualização de Detalhes

- O usuário pode visualizar detalhes completos de uma solicitação específica
- Apenas as suas próprias solicitações
- Deve exibir:
  - Todas as informações da listagem
  - Histórico de alterações (se houver)
  - Data de expiração do acesso

### Renovação de Acesso

- Usuário pode renovar acesso a módulos quando:
  - Apenas para seus próprios acessos
  - Faltarem menos de 30 dias para expiração
  - Status atual for "ATIVO"
- Ao renovar:
  - Criar nova solicitação vinculada à anterior
  - Reaplicar regras de negócio
  - Estender validade por mais 180 dias (se aprovado)
  - Criar novo protocolo de solicitação

### Cancelamento de Solicitação

- O usuário pode cancelar uma solicitação com status "ATIVO"
- Ao cancelar:
  - Campo obrigatório: Motivo do cancelamento (10-200 caracteres)
  - Status muda para "CANCELADO"
  - Acesso aos módulos é revogado imediatamente
  - Registrar motivo e data no histórico

### Consulta de Módulos Disponíveis

- O usuário deve conseguir listar todos os módulos disponíveis
- Retornar:
  - Nome do módulo
  - Descrição
  - Departamentos permitidos
  - Indicador se está ativo
  - Módulos incompatíveis (se houver)

# Requisitos para Validade do Teste

## Tecnologias Obrigatórias

- Java 21 (obrigatório)
- Sprint Boot 3.x
- Spring Data JPA
- Spring Validation
- Postgres SQL 17
- H2 (apenas para execução dos testes)

- Maven
- Docker
- Docker Compose
- Nginx (ou outra alterativa pra proxy)
- Lombok (sugestivo)

### Validações e Tratamento de Erros

- Implementar validações e tratamentos de erros personalizados.

### Segurança

- Senhas criptografadas devem ser seguras com hash e salt
- Token de acesso deve expirar com 15 minutos
- Validação de token em todos os endpoints protegidos
- Usuário só pode acessar suas próprias solicitações
- Implementar segurança de acesso aos endpoints

### Requisitos para os Testes

**COBERTURA MÍNIMA OBRIGATÓRIA: 80%**

**Regras Rigorosas para Testes Unitários:**

- **PROIBIDO** usar `any()`, `anyString()`, `anyLong()`, etc. do Mockito
- **OBRIGATÓRIO** usar valores específicos nos mocks: `eq()`, valores exatos
- **OBRIGATÓRIO** verificar com `verify()` as chamadas aos mocks
- Cobertura mínima de 90% do código (medida por JaCoCo)
- Todos os métodos de Service devem ter testes
- Todas as regras de negócio devem ser testadas
- Todos os cenários de exceção devem ser testados

**Configuração JaCoCo (pom.xml)**

Deve incluir configuração que falhe o build se cobertura < 80%

**Ferramentas Obrigatórias:** JUnit 5, Mockito (sem usar `any()`), MockMvc, Spring Security Test, JaCoCo (relatório de cobertura) e Instancio

## Entregáveis

### Código Fonte

- Link público para repositório Git (GitHub, GitLab ou Bitbucket) ou Zip contendo projeto e histórico do GIT.
- Commits organizados e descritivos
- Branch `main` funcionando
- `.gitignore` configurado adequadamente
- Dockerfile e Docker Compose funcional
- Monorepo com todo o código e arquivo necessário para a solução funcionar

### Documentação

**README.md** contendo:

- Descrição do projeto
- Tecnologias utilizadas e versões
- Pré-requisitos (Docker, Docker Compose)
- Como executar localmente com Docker
- Como executar os testes
- Como visualizar relatório de cobertura
- Credenciais para teste
- Exemplos de requisições
- Arquitetura da solução (diagrama ou explicação)
- Decisões técnicas relevantes

**Relatório de Tests (JaCoCo)**

- Relatório em PDF com resultado e cobertura dos testes.

**Swagger/OpenAPI**:

- Configurado e acessível via `/swagger-ui.html`
- Documentação completa de todos os endpoints

## Infraestrutura e Deploy

O projeto deve ser provisionado via docker-compose, necessário existir o arquivo `Dockerfile` e `docker-compose.yml` na raiz do projeto.

### Requisitos da infraestrutura

- Provisionar o PostgreSQL.
- Provisionar 3 Aplicações Java (app1, app2 e app3)
- Provisionar um LoadBalancer (ex: NGINX).
- Fornecer acesso ao swagger por meio do Proxy/LB.
- Balanceamento de carga funcional por meio do LB - pode ser stateless.
- Unica rede docker para todos os containers com comunicação interna onde aplicável
- Deve ser possível configurar ambiente por variáveis de ambiente

### Dados Iniciais

O `data.sql` ou migrations (Flyway/Liquibase) devem popular:

**Usuários - mínimo 4 usuários de departamento diferentes**

**Módulos:**

```
1. Portal do Colaborador (todos os departamentos)
2. Relatórios Gerenciais (todos os departamentos)
3. Gestão Financeira (Financeiro, TI)
4. Aprovador Financeiro (Financeiro, TI) *incompatível com #5
5. Solicitante Financeiro (Financeiro, TI) *incompatível com #4
6. Administrador RH (RH, TI) *incompatível com #7
7. Colaborador RH (RH, TI) *incompatível com #6
8. Gestão de Estoque (Operações, TI)
9. Compras (Operações, TI)
10. Auditoria (apenas TI)
```

## Critérios de Avaliação

A solução será avaliada como um todo, desde a documentação a facilidade de execução, deploy e testes.

- ✅ Autenticação JWT funcionando
- ✅ CRUD de solicitações completo
- ✅ Regras de negócio implementadas corretamente
- ✅ Validações funcionando
- ✅ Endpoints respondendo corretamente
- ✅ Código limpo e legível
- ✅ Princípios SOLID aplicados
- ✅ Nomenclatura adequada (português ou inglês consistente)
- ✅ Sem duplicação de código
- ✅ Uso adequado de Java 21 features
- ✅ Cobertura mínima de testes
- ✅ Nenhum uso de `any()` nos testes (obrigatório)
- ✅ Testes bem estruturados e legíveis
- ✅ Testes de cenários positivos e negativos
- ✅ Testes de integração funcionando
- ✅ Relatório JaCoCo gerado e acessível
- ✅ Dockerfile otimizado (multi-stage build)
- ✅ docker-compose.yml completo e funcional
- ✅ PostgreSQL 17 configurado corretamente
- ✅ Três instâncias da aplicação rodando
- ✅ Nginx fazendo balanceamento de carga
- ✅ Health checks configurados
- ✅ Rede Docker configurada
- ✅ Aplicação sobe com `docker-compose up` sem erros
- ✅ JWT implementado corretamente
- ✅ Endpoints protegidos adequadamente
- ✅ Senhas criptografadas e seguras
- ✅ Validação de autorização
- ✅ README completo e claro
- ✅ Swagger configurado
- ✅ Instruções de execução claras e funcional
- ✅ Documentação das decisões técnicas

### Diferenciais (não obrigatórios)

- ⭐ Migrations com Flyway ou Liquibase
- ⭐ Refresh token implementado
- ⭐ Logs estruturados com Logback/SLF4J
- ⭐ Profiles Spring bem configurados (dev/prod)
- ⭐ Frontend para aplicação React/Angular/Vue/JQuery/Etc.

## Diferenciais de alto impacto (não obrigatório)

- 🌟 Documentação e diagramas da arquitetura proposta (C4, ADR’s e etc)
- 🌟 Documentação auxiliar para ferramentas de IA (Claude Code, copilot etc)

## Checklist de Entrega

**Antes de enviar, verifique se:**

- [ ] Todos os testes passam
- [ ] Cobertura de testes ≥ 80%
- [ ] `docker-compose up -d` funciona sem erros
- [ ] Consegue fazer login via Postman/CURL
- [ ] Consegue criar uma solicitação
- [ ] Nginx está balanceando entre app1, app2 e app3
- [ ] README.md está completo
- [ ] Código compila sem erros
- [ ] Swagger está acessível
- [ ] Dados iniciais estão populados
- [ ] Arquivo GIT para ignore está configurado (sem arquivos de IDE, target/, etc)

---

## Prazo e Entrega

**Prazo de entrega:** 8 dias corridos

**Forma de entrega:**

- Link do repositório Git Público (GitLab, GitHub, Bitbucket, etc.)
- **Incluir no email**:
  - Link do repositório
  - Currículo
  - Se usou ou não usou IA para fazer o teste.

---

## Observações Finais

### O que será desclassificatório:

- ❌ Aplicação não sobe com Docker Compose
- ❌ Cobertura de testes abaixo de 80%
- ❌ Não usar Java 21
- ❌ Não usar tecnologias obrigatórias para o Teste
- ❌ Falta de balanceamento de carga (LB)

### Dicas importantes:

- ✅ Comece pela configuração do Docker e banco de dados
- ✅ Implemente os testes conforme desenvolve (TDD recomendado)
- ✅ Teste o balanceamento de carga fazendo várias requisições
- ✅ Use o JaCoCo desde o início para acompanhar cobertura
- ✅ Documente enquanto desenvolve
- ✅ Faça commits frequentes e descritivos
- ✅ Teste a aplicação do zero (clone em outra pasta e execute)

## Em caso de dúvidas:

Faça suposições razoáveis e documente o que for necessário da sua decisão.

Você não será avaliado negativamente por tomar decisões sobre o que não está descrito ou esteja claro na documentação.

O foco é nas habilidades técnicas, documentais, uso das ferramentas e entrega do projeto compilando e executando via docker-compose.

## **Boa sorte! 🍀**

**Aguardamos sua solução!**
