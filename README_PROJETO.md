# 🚀 Sistema de Solicitação de Acesso a Módulos

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue)
![Docker](https://img.shields.io/badge/Docker-Compose-blue)
![Coverage](https://img.shields.io/badge/Coverage-%E2%89%A580%25-brightgreen)

Sistema corporativo para solicitação e gerenciamento de acessos a módulos, desenvolvido com Spring Boot 3, Java 21, PostgreSQL 17, com autenticação JWT e balanceamento de carga via Nginx.

---

## 📋 Índice

- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Pré-requisitos](#-pré-requisitos)
- [Como Executar com Docker](#-como-executar-com-docker)
- [Como Executar os Testes](#-como-executar-os-testes)
- [Credenciais de Teste](#-credenciais-de-teste)
- [Exemplos de Requisições](#-exemplos-de-requisições)
- [Arquitetura da Solução](#-arquitetura-da-solução)
- [Regras de Negócio](#-regras-de-negócio)
- [Documentação da API](#-documentação-da-api)
- [Decisões Técnicas](#-decisões-técnicas)

---

## 🛠 Tecnologias Utilizadas

### Backend
- **Java 21** (LTS)
- **Spring Boot 3.3.5**
- **Spring Data JPA** - Persistência de dados
- **Spring Security** - Autenticação e autorização
- **Spring Validation** - Validação de dados
- **PostgreSQL 17** - Banco de dados principal
- **H2** - Banco de dados para testes
- **Flyway** - Migrations de banco de dados
- **JWT (jjwt 0.12.6)** - Tokens de autenticação
- **Lombok** - Redução de boilerplate
- **SpringDoc OpenAPI 2.6.0** - Documentação Swagger

### DevOps
- **Docker** - Containerização
- **Docker Compose** - Orquestração de containers
- **Nginx** - Load Balancer e Reverse Proxy
- **Maven 3.9+** - Gerenciamento de dependências e build

### Testes
- **JUnit 5** - Framework de testes
- **Mockito** - Mocks (sem uso de `any()`)
- **MockMvc** - Testes de integração de controllers
- **Spring Security Test** - Testes de segurança
- **JaCoCo** - Cobertura de testes (≥80%)
- **Instancio** - Geração de dados de teste

---

## 📦 Pré-requisitos

Certifique-se de ter instalado:

- **Docker**: versão 24.0 ou superior
- **Docker Compose**: versão 2.0 ou superior

> ⚠️ **Nota**: Não é necessário ter Java ou Maven instalados localmente, pois o build é feito dentro do container Docker.

Para verificar as versões instaladas:

```bash
docker --version
docker-compose --version
```

---

## 🚀 Como Executar com Docker

### 1. Clone o repositório

```bash
git clone <repository-url>
cd access-request-system
```

### 2. Execute o docker-compose

```bash
docker-compose up -d
```

Este comando irá:
1. Construir a imagem da aplicação (multi-stage build)
2. Iniciar o PostgreSQL 17
3. Iniciar 3 instâncias da aplicação (app1, app2, app3)
4. Iniciar o Nginx como Load Balancer
5. Executar as migrations do Flyway
6. Popular o banco com dados iniciais

### 3. Aguarde os containers iniciarem

```bash
# Verificar status dos containers
docker-compose ps

# Visualizar logs
docker-compose logs -f app1
```

### 4. Acessar a aplicação

A aplicação estará disponível em:

- **API via Nginx (Load Balanced)**: http://localhost
- **Swagger UI**: http://localhost/swagger-ui.html
- **App Instance 1**: http://localhost:8081
- **App Instance 2**: http://localhost:8082
- **App Instance 3**: http://localhost:8083

### Comandos Úteis

```bash
# Parar os containers
docker-compose down

# Parar e remover volumes (limpa o banco de dados)
docker-compose down -v

# Rebuild da aplicação
docker-compose build --no-cache

# Ver logs de um serviço específico
docker-compose logs -f nginx
docker-compose logs -f app1

# Executar comando dentro do container do postgres
docker-compose exec postgres psql -U postgres -d accessrequest
```

---

## 🧪 Como Executar os Testes

### Executar Testes Localmente (Requer Java 21 e Maven)

```bash
# Executar todos os testes
mvn test

# Executar testes com relatório de cobertura
mvn clean verify

# Executar apenas testes unitários
mvn test -Dgroups="unit"

# Executar apenas testes de integração
mvn test -Dgroups="integration"
```

### Visualizar Relatório de Cobertura JaCoCo

Após executar `mvn clean verify`, o relatório será gerado em:

```
target/site/jacoco/index.html
```

Abra o arquivo no navegador para visualizar a cobertura detalhada.

### Verificação de Cobertura Mínima

O build falhará automaticamente se a cobertura for inferior a 80% (configurado no pom.xml):

```xml
<limit>
    <counter>LINE</counter>
    <value>COVEREDRATIO</value>
    <minimum>0.80</minimum>
</limit>
```

---

## 🔑 Credenciais de Teste

O sistema vem com 5 usuários pré-cadastrados:

| Nome | Email | Senha | Departamento |
|------|-------|-------|--------------|
| João Silva | joao.silva@empresa.com | senha123 | TI |
| Maria Santos | maria.santos@empresa.com | senha123 | FINANCEIRO |
| Pedro Costa | pedro.costa@empresa.com | senha123 | RH |
| Ana Oliveira | ana.oliveira@empresa.com | senha123 | OPERACOES |
| Carlos Souza | carlos.souza@empresa.com | senha123 | OUTROS |

---

## 📡 Exemplos de Requisições

### 1. Fazer Login

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
  "token": "eyJhbGciOiJIUzUxMiJ9...",
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
    "justificativa": "Preciso acessar o portal e relatórios para realizar minhas atividades diárias de gestão",
    "urgente": false
  }'
```

**Resposta (Aprovado):**
```json
{
  "mensagem": "Solicitação criada com sucesso! Protocolo: SOL-20251119-0001. Seus acessos já estão disponíveis!",
  "dados": {
    "id": 1,
    "protocolo": "SOL-20251119-0001",
    "status": "ATIVO",
    "modulos": [
      {"id": 1, "nome": "Portal do Colaborador"},
      {"id": 2, "nome": "Relatórios Gerenciais"}
    ],
    "justificativa": "Preciso acessar o portal...",
    "urgente": false,
    "dataSolicitacao": "2025-11-19T10:30:00",
    "dataExpiracao": "2026-05-18T10:30:00"
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
    "motivo": "Não preciso mais deste acesso no momento"
  }'
```

---

## 🏗️ Arquitetura da Solução

### Diagrama de Infraestrutura

```
┌─────────────────────────────────────────┐
│      Nginx (Load Balancer: 80)         │
│          least_conn strategy            │
└─────────────────┬───────────────────────┘
                  │
        ┌─────────┴─────────┐
        │                   │
┌───────▼────────┐ ┌────────▼────────┐ ┌────────────────┐
│   App1:8081    │ │   App2:8082     │ │   App3:8083    │
│  Spring Boot   │ │  Spring Boot    │ │  Spring Boot   │
│  (Stateless)   │ │  (Stateless)    │ │  (Stateless)   │
└───────┬────────┘ └────────┬────────┘ └────────┬────────┘
        │                   │                    │
        └───────────────────┴────────────────────┘
                            │
                   ┌────────▼─────────┐
                   │  PostgreSQL 17   │
                   │    Port 5432     │
                   │  (Single Source) │
                   └──────────────────┘
```

### Camadas da Aplicação

```
┌─────────────────────────────────────┐
│         Controller Layer            │  ← REST endpoints
├─────────────────────────────────────┤
│          Service Layer              │  ← Business Logic
├─────────────────────────────────────┤
│        Repository Layer             │  ← Data Access (JPA)
├─────────────────────────────────────┤
│          Entity Layer               │  ← Domain Models
└─────────────────────────────────────┘
```

### Fluxo de Autenticação JWT

```
1. Cliente → POST /api/auth/login
2. AuthService → Valida credenciais
3. JwtTokenProvider → Gera token JWT
4. Token válido por 15 minutos
5. Cliente envia token no header: Authorization: Bearer {token}
6. JwtAuthenticationFilter → Valida token em cada requisição
7. Acesso concedido/negado
```

---

## ⚙️ Regras de Negócio

### 1. Validações de Solicitação

- ✅ Mínimo 1, máximo 3 módulos por solicitação
- ✅ Justificativa: 20-500 caracteres
- ✅ Justificativa não pode ser genérica (ex: "teste", "aaa", "preciso")
- ✅ Módulos devem estar ativos
- ✅ Usuário não pode ter solicitação ativa para o mesmo módulo
- ✅ Usuário não pode solicitar módulo que já possui acesso ativo

### 2. Compatibilidade de Departamento

| Departamento | Módulos Permitidos |
|--------------|-------------------|
| TI | **Todos os módulos** |
| Financeiro | Financeiro, Relatórios, Portal |
| RH | RH, Relatórios, Portal |
| Operações | Estoque, Compras, Relatórios, Portal |
| Outros | Portal, Relatórios |

### 3. Módulos Mutuamente Exclusivos

- ⚔️ **Aprovador Financeiro** ↔️ **Solicitante Financeiro**
- ⚔️ **Administrador RH** ↔️ **Colaborador RH**

### 4. Limite de Módulos por Usuário

- 👤 **Usuário TI**: máximo 10 módulos ativos
- 👤 **Outros usuários**: máximo 5 módulos ativos

### 5. Geração de Protocolo

- 📋 Formato: `SOL-YYYYMMDD-NNNN`
- 📋 Exemplo: `SOL-20251119-0001`
- 📋 Sequencial diário

### 6. Prazo de Validade

- ⏰ **Acessos**: 180 dias após concessão
- ⏰ **Renovação**: Permitida quando faltarem menos de 30 dias para expiração

### 7. Motivos de Negação Automática

- ❌ "Departamento sem permissão para acessar este módulo"
- ❌ "Módulo incompatível com outro módulo já ativo em seu perfil"
- ❌ "Limite de módulos ativos atingido"
- ❌ "Justificativa insuficiente ou genérica"

---

## 📚 Documentação da API

### Swagger UI

A documentação interativa completa da API está disponível em:

**http://localhost/swagger-ui.html**

Através do Swagger você pode:
- ✅ Ver todos os endpoints disponíveis
- ✅ Testar as requisições diretamente no navegador
- ✅ Ver os schemas de request/response
- ✅ Autenticar usando o botão "Authorize"

### OpenAPI Specification

O arquivo JSON da especificação OpenAPI está em:

**http://localhost/v3/api-docs**

---

## 🎯 Decisões Técnicas

### 1. Arquitetura Stateless com JWT

**Por quê?**
- ✅ Facilita escalaballidade horizontal (múltiplas instâncias)
- ✅ Não requer armazenamento de sessão centralizado (Redis)
- ✅ Cada instância valida tokens independentemente
- ✅ Ideal para arquitetura distribuída

### 2. PostgreSQL Único (Não replicado)

**Por quê?**
- ✅ Simplicidade para ambiente de teste/desenvolvimento
- ✅ Suficiente para a carga esperada
- ✅ Facilita execução local
- ✅ ACID garantido para todas as transações

**Em produção considerar**: Master-Slave replication para alta disponibilidade

### 3. Flyway para Migrations

**Por quê?**
- ✅ Versionamento do schema do banco
- ✅ Controle de mudanças rastreável
- ✅ Migrations podem ser revisadas em code review
- ✅ Rollback facilitado

### 4. BCrypt com Strength 12

**Por quê?**
- ✅ Algoritmo robusto e amplamente testado
- ✅ Salt automático
- ✅ Strength 12 equilibra segurança e performance
- ✅ Resistente a ataques de força bruta

### 5. Nginx com least_conn

**Por quê?**
- ✅ Distribui requisições para instância com menos conexões ativas
- ✅ Melhor performance que round-robin
- ✅ Health checks integrados
- ✅ Failover automático

### 6. DTOs ao invés de Entities nos Endpoints

**Por quê?**
- ✅ Evita exposição de dados sensíveis
- ✅ Controle sobre dados retornados
- ✅ Evita problemas com lazy loading
- ✅ Melhor versionamento da API

### 7. Testes Rigorosos sem any()

**Por quê?**
- ✅ Garante que valores exatos são testados
- ✅ Detecta mudanças não intencionais
- ✅ Testes mais confiáveis e explícitos
- ✅ Facilita refatoração com segurança

### 8. JaCoCo com 80% de Cobertura Mínima

**Por quê?**
- ✅ Garante qualidade do código
- ✅ Build falha se cobertura < 80%
- ✅ Relatórios visuais detalhados
- ✅ Integração com CI/CD

---

## 🐳 Estrutura Docker

### Dockerfile (Multi-stage Build)

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

**Vantagens:**
- ✅ Imagem final 70% menor (sem Maven e dependências de build)
- ✅ Build reproduzível
- ✅ Camadas em cache otimizadas

---

## 📊 Módulos do Sistema

O sistema possui 10 módulos pré-configurados:

| ID | Nome | Descrição | Departamentos |
|----|------|-----------|---------------|
| 1 | Portal do Colaborador | Portal geral | Todos |
| 2 | Relatórios Gerenciais | Dashboards | Todos |
| 3 | Gestão Financeira | Sistema financeiro | Financeiro, TI |
| 4 | Aprovador Financeiro | Aprovações | Financeiro, TI |
| 5 | Solicitante Financeiro | Solicitações | Financeiro, TI |
| 6 | Administrador RH | Gestão RH | RH, TI |
| 7 | Colaborador RH | Acesso RH | RH, TI |
| 8 | Gestão de Estoque | Inventário | Operações, TI |
| 9 | Compras | Fornecedores | Operações, TI |
| 10 | Auditoria | Compliance | Apenas TI |

---

## 🔍 Monitoramento e Logs

### Health Check

```bash
curl http://localhost/actuator/health
```

### Logs da Aplicação

```bash
# Ver logs de todas as instâncias
docker-compose logs -f app1 app2 app3

# Ver logs do Nginx
docker-compose logs -f nginx
```

### Verificar Balanceamento de Carga

Faça múltiplas requisições e observe que elas são distribuídas entre as 3 instâncias:

```bash
for i in {1..10}; do
  curl -s http://localhost/actuator/health | grep -o "app[0-9]"
done
```

---

## 🛠️ Troubleshooting

### Porta 80 já está em uso

```bash
# Alterar porta do Nginx no docker-compose.yml
ports:
  - "8080:80"  # Acesse via http://localhost:8080
```

### Containers não sobem

```bash
# Verificar logs
docker-compose logs

# Rebuild completo
docker-compose down -v
docker-compose build --no-cache
docker-compose up -d
```

### Banco de dados não inicializa

```bash
# Conectar no PostgreSQL
docker-compose exec postgres psql -U postgres -d accessrequest

# Verificar tabelas
\dt
```

---

## 👥 Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/NovaFuncionalidade`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/NovaFuncionalidade`)
5. Abra um Pull Request

---

## 📄 Licença

Este projeto foi desenvolvido como teste técnico para a **Supera Tecnologia**.

---

## 📞 Suporte

Em caso de dúvidas ou problemas:

1. Consulte a documentação do Swagger
2. Verifique os logs dos containers
3. Revise este README

---

**Desenvolvido com ❤️ usando Java 21 + Spring Boot 3**

