![Supera Cover](imgs/supera_cover.jpeg)
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

- Usuário pode renovar  acesso a módulos quando:
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

- [ ]  Todos os testes passam
- [ ]  Cobertura de testes ≥ 80%
- [ ]  `docker-compose up -d` funciona sem erros
- [ ]  Consegue fazer login via Postman/CURL
- [ ]  Consegue criar uma solicitação
- [ ]  Nginx está balanceando entre app1, app2 e app3
- [ ]  README.md está completo
- [ ]  Código compila sem erros
- [ ]  Swagger está acessível
- [ ]  Dados iniciais estão populados
- [ ]  Arquivo GIT para ignore está configurado (sem arquivos de IDE, target/, etc)

---

## Prazo e Entrega

**Prazo de entrega:**  8 dias corridos

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
