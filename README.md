# 🚗 Sistema de Aluguel de Carros

> Sistema web para gestão de aluguéis de automóveis: clientes efetuam, modificam, consultam e cancelam pedidos pela Internet; agentes (empresas e bancos) analisam financeiramente, avaliam e executam os contratos.

*Laboratório 02 — Desenvolvimento e Integração de Aplicações Web (DIAW), Engenharia de Software, PUC Minas. Prof. João Paulo Carneiro Aramuni.*

## 🚧 Status do Projeto
![Java](https://img.shields.io/badge/Java-25-007ec6?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.x-007ec6?style=for-the-badge&logo=springboot&logoColor=white)
![React](https://img.shields.io/badge/React-19-007ec6?style=for-the-badge&logo=react&logoColor=white)
![Vite](https://img.shields.io/badge/Vite-7-007ec6?style=for-the-badge&logo=vite&logoColor=white)
![Supabase](https://img.shields.io/badge/Supabase-PostgreSQL-007ec6?style=for-the-badge&logo=supabase&logoColor=white)

## 📚 Índice
- [Links Úteis](#-links-úteis)
- [Sobre o Projeto](#-sobre-o-projeto)
- [Funcionalidades Principais](#-funcionalidades-principais)
- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Arquitetura](#-arquitetura)
- [Instalação e Execução](#-instalação-e-execução)
- [Deploy](#-deploy)
- [Estrutura de Pastas](#-estrutura-de-pastas)
- [Demonstração](#-demonstração)
- [Documentações utilizadas](#-documentações-utilizadas)
- [Autores](#-autores)
- [Agradecimentos](#-agradecimentos)
- [Licença](#-licença)

## 🔗 Links Úteis
- 🌐 **Demo Online:** _(adicione o link, se houver deploy)_
- 📖 **Modelagem UML:** [`docs/modelagem.md`](docs/modelagem.md) e [`docs/casos-de-uso.puml`](docs/casos-de-uso.puml)

## 📝 Sobre o Projeto
Gerir aluguéis de automóveis por telefone ou papel é lento e sujeito a erros. Este sistema centraliza o processo: o cliente registra o pedido, o agente (empresa ou banco) faz a análise financeira e, com parecer positivo, o pedido segue para execução do contrato, podendo envolver um contrato de crédito concedido por um banco. O projeto nasceu como laboratório acadêmico e demonstra um fluxo completo com autenticação, CRUD e arquitetura MVC, com o modelo UML alinhado ao código.

## ✨ Funcionalidades Principais
- 🔐 **Autenticação:** cadastro e login (usuário e senha, BCrypt) com perfis Cliente, Empresa e Banco; todo o sistema exige login.
- ⚙️ **CRUD de Cliente:** RG, CPF, nome, endereço, profissão e até 3 rendimentos (entidade empregadora e valor).
- 📄 **Pedidos de aluguel:** criar, consultar, modificar e cancelar (cliente); modificar e avaliar (agentes).
- 🚘 **Automóvel:** matrícula, ano, marca, modelo, placa e proprietário (cliente, empresa ou banco).
- 🏦 **Contrato:** execução do contrato após parecer positivo, com contrato de crédito opcional concedido por banco.
- 📊 **Status do pedido:** `PENDENTE → APROVADO/REJEITADO → CONTRATADO` (ou `CANCELADO`).

## 🛠 Tecnologias Utilizadas
### 💻 Front-end
- **Biblioteca:** React 19 · **Linguagem:** JavaScript (ES6+) · **Build Tool:** Vite 7 · **Estado:** hooks (`useState`/`useEffect`)
### 🖥️ Back-end
- **Linguagem:** Java 25 (JDK) · **Framework:** Spring Boot 4.0.x (Spring MVC) · **Build:** Apache Maven
- **Banco de Dados:** Supabase (PostgreSQL) · **ORM:** Hibernate/JPA · **Autenticação:** token Bearer com senhas em BCrypt (`spring-security-crypto`)

## 🏗 Arquitetura
Arquitetura **MVC** em camadas no back-end (`controller` → `service` → `repository` → `model`), com API REST em JSON consumida pelo front-end React. O sistema é dividido em dois subsistemas: **gestão de pedidos e contratos** (back-end) e **construção dinâmica das páginas web** (front-end). Um interceptor em `config` valida o token de todas as rotas `/api/**` (exceto `/api/auth/**`).

Diagramas de casos de uso, classes e pacotes em [`docs/modelagem.md`](docs/modelagem.md).

### Endpoints
| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/auth/register`, `/api/auth/login` | Cadastro e login |
| GET/POST | `/api/clientes` | Listar / criar cliente (criar: agentes) |
| GET/PUT/DELETE | `/api/clientes/{id}` | Consultar / atualizar / excluir cliente |
| GET/POST | `/api/pedidos` | Listar / criar pedido |
| PUT/DELETE | `/api/pedidos/{id}` | Modificar / cancelar pedido pendente |
| PATCH | `/api/pedidos/{id}/avaliar` | Agente aprova ou reprova |
| PATCH | `/api/pedidos/{id}/contratar` | Agente executa o contrato (banco pode informar crédito) |

## 🔧 Instalação e Execução
### Pré-requisitos
- **Java JDK 25** e **Maven** (ou uma IDE como o IntelliJ IDEA, que já traz o Maven)
- **Node.js** LTS e npm
- Um projeto no **Supabase**

### 🔑 Variáveis de Ambiente
**Back-end** (variáveis do sistema ou da IDE; modelo em `backend/.env.example`):

| Variável | Descrição | Exemplo |
|---|---|---|
| `DB_URL` | URL JDBC do Supabase | `jdbc:postgresql://db.SEU-PROJETO.supabase.co:5432/postgres` |
| `DB_USER` | Usuário do banco | `postgres` |
| `DB_PASSWORD` | Senha definida ao criar o projeto | `sua-senha` |
| `SERVER_PORT` | Porta do back-end (opcional) | `8080` |

**Front-end** (`frontend/.env.local`, modelo em `frontend/.env.example`):

| Variável | Descrição | Exemplo |
|---|---|---|
| `VITE_API_URL` | URL base da API | `http://localhost:8080/api` |

> ⚠️ Nunca versione senhas. Se a conexão direta falhar (IPv6), use o *Session pooler* do Supabase (usuário `postgres.SEU-PROJETO`).

### 📦 Instalação de Dependências
```bash
git clone <URL_DO_SEU_REPOSITÓRIO>
cd aluguel-carros
cd frontend && npm install && cd ..
cd backend && mvn clean install && cd ..
```

### 💾 Banco de Dados
O Hibernate (`ddl-auto=update`) cria as tabelas automaticamente no Supabase na primeira execução. Confira em *Table Editor*.

### ⚡ Como Executar
**Terminal 1 — Back-end:**
```bash
cd backend
mvn spring-boot:run
```
🚀 Disponível em http://localhost:8080

**Terminal 2 — Front-end:**
```bash
cd frontend
npm run dev
```
🎨 Disponível em http://localhost:5173

## 🚀 Deploy
```bash
cd frontend && npm run build        # gera frontend/dist
cd ../backend && mvn clean package   # gera backend/target/aluguel-0.0.1.jar
java -jar backend/target/aluguel-0.0.1.jar
```
Configure `DB_URL`, `DB_USER` e `DB_PASSWORD` no provedor, e `VITE_API_URL` apontando para a API de produção. Ajuste também a origem permitida do CORS em `WebConfig`.

## 📂 Estrutura de Pastas
```
.
├── README.md
├── .gitignore
├── /docs                        # 📚 Modelagem UML (casos de uso, classes, pacotes, histórias)
├── /frontend                    # 📁 React + Vite
│   ├── .env.example
│   ├── package.json
│   └── /src                     # App.jsx (telas), api.js (chamadas HTTP), main.jsx
└── /backend                     # 📁 Spring Boot (MVC)
    ├── .env.example
    ├── pom.xml
    └── /src/main
        ├── /java/com/pucminas/aluguel
        │   ├── /controller      # 🎮 Endpoints REST
        │   ├── /service         # ⚙️ Regras de negócio
        │   ├── /repository      # 🗄️ Repositórios JPA
        │   ├── /model           # 🧬 Entidades
        │   └── /config          # 🔧 CORS e autenticação por token
        └── /resources           # application.properties
```

## 🎥 Demonstração
_Adicione aqui prints ou GIFs das telas: login/cadastro, lista de pedidos, criação de pedido, avaliação pelo agente e execução do contrato._

## 🔗 Documentações utilizadas
- 📖 [React](https://react.dev/reference/react) · [Vite](https://vitejs.dev/config/) · [Spring Boot](https://docs.spring.io/spring-boot/) · [Supabase](https://supabase.com/docs)
- 📖 [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/)

## 👥 Autores
| 👤 Nome | GitHub | LinkedIn |
|---|---|---|
| Henrique Ignacio Ferreira Souza | _(link)_ | _(link)_ |

## 🙏 Agradecimentos
- [**Engenharia de Software PUC Minas**](https://www.instagram.com/engsoftwarepucminas/)
- [**Prof. Dr. João Paulo Aramuni**](https://github.com/joaopauloaramuni) — pelos ensinamentos de Arquitetura de Software e desenvolvimento web.

## 📄 Licença
Projeto acadêmico distribuído sob a **Licença MIT**.
