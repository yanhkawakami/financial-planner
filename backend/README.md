# Financial Planner API

Sistema de planejamento financeiro pessoal desenvolvido com Spring Boot, oferecendo controle de gastos por categorias com autenticação e autorização OAuth2.

## 📋 Índice

- [Tecnologias](#tecnologias)
- [Configuração do Ambiente](#configuração-do-ambiente)
- [Autenticação](#autenticação)
- [Perfis de Usuário](#perfis-de-usuário)
- [Endpoints](#endpoints)
  - [Authentication](#authentication)
  - [Users](#users)
  - [Categories](#categories)
  - [Spends](#spends)
- [Modelos de Dados](#modelos-de-dados)
- [Códigos de Status](#códigos-de-status)
- [Exemplos de Uso](#exemplos-de-uso)

## 🛠 Tecnologias

- **Java 21**
- **Spring Boot 3.5.7**
- **Spring Security com OAuth2**
- **Spring Data JPA**
- **H2 Database** (desenvolvimento)
- **Maven**

## ⚙️ Configuração do Ambiente

### Pré-requisitos

- Java 21
- Maven 3.6+

### Executando o Projeto

```bash
# Clone o repositório
git clone <repository-url>

# Navegue até o diretório do backend
cd financial-planner/backend

# Execute o projeto
./mvnw spring-boot:run
```

### URLs Importantes

- **API Base URL:** `http://localhost:8080`
- **H2 Console:** `http://localhost:8080/h2-console`
  - **URL:** `jdbc:h2:mem:testdb`
  - **Username:** `sa`
  - **Password:** (vazio)

### Configuração OAuth2

```properties
security.client-id=myclientid
security.client-secret=myclientsecret
security.jwt.duration=86400
```

## 🔐 Autenticação

A API utiliza OAuth2 com Resource Owner Password Credentials Grant.

### Obter Token de Acesso

```http
POST /oauth2/token
Content-Type: application/x-www-form-urlencoded
Authorization: Basic bXljbGllbnRpZDpteWNsaWVudHNlY3JldA==

grant_type=password&username={email}&password={password}
```

**Exemplo de resposta:**
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 86399,
  "scope": "read write"
}
```

### Usando o Token

Inclua o token no cabeçalho Authorization:

```http
Authorization: Bearer {access_token}
```

## 👥 Perfis de Usuário

### ROLE_USER
- Criar, editar e excluir suas próprias despesas
- Visualizar todas as categorias
- Editar seu próprio perfil

### ROLE_ADMIN
- Todas as permissões de ROLE_USER
- Gerenciar categorias (criar, editar, excluir)
- Visualizar todos os usuários
- Visualizar todas as despesas (de todos os usuários)

### Usuários Padrão

```
Admin:
- Email: admin@email.com
- Senha: 123456
- Roles: ROLE_ADMIN, ROLE_USER

User:
- Email: user@email.com
- Senha: 123456
- Roles: ROLE_USER
```

## 📡 Endpoints

### Authentication

#### POST /oauth2/token
Obter token de acesso OAuth2.

**Cabeçalhos:**
```
Content-Type: application/x-www-form-urlencoded
Authorization: Basic bXljbGllbnRpZDpteWNsaWVudHNlY3JldA==
```

**Body:**
```
grant_type=password
username=user@email.com
password=123456
```

**Resposta de sucesso (200):**
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIs...",
  "token_type": "Bearer",
  "expires_in": 86399,
  "scope": "read write"
}
```

---

### Users

#### GET /users
📋 **Listar todos os usuários**

**Autorização:** ROLE_ADMIN

**Parâmetros de consulta:**
- `page` (opcional): Número da página (padrão: 0)
- `size` (opcional): Tamanho da página (padrão: 20)
- `sort` (opcional): Campo de ordenação (ex: name,asc)

**Exemplo:**
```http
GET /users?page=0&size=10&sort=name,asc
Authorization: Bearer {token}
```

**Resposta de sucesso (200):**
```json
{
  "content": [
    {
      "id": 1,
      "name": "Admin",
      "email": "admin@email.com",
      "phone": "11999999999"
    },
    {
      "id": 2,
      "name": "User",
      "email": "user@email.com",
      "phone": "11888888888"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 2,
  "totalPages": 1
}
```

#### POST /users
👤 **Criar novo usuário**

**Autorização:** Público (não requer token)

**Body:**
```json
{
  "name": "João Silva",
  "email": "joao@email.com",
  "phone": "11999888777",
  "password": "123456",
  "roles": ["ROLE_USER"]
}
```

**Resposta de sucesso (201):**
```json
{
  "id": 3,
  "name": "João Silva",
  "email": "joao@email.com",
  "phone": "11999888777"
}
```

#### PUT /users/{id}
✏️ **Atualizar usuário**

**Autorização:** ROLE_ADMIN ou ROLE_USER (próprio usuário)

**Parâmetros de rota:**
- `id` (obrigatório): ID do usuário

**Body:**
```json
{
  "name": "João Silva Santos",
  "email": "joao.santos@email.com",
  "phone": "11999888777",
  "password": "novaSenha123"
}
```

**Resposta de sucesso (200):**
```json
{
  "id": 3,
  "name": "João Silva Santos",
  "email": "joao.santos@email.com",
  "phone": "11999888777"
}
```

---

### Categories

#### GET /categories
📂 **Listar todas as categorias**

**Autorização:** Público ou qualquer usuário autenticado

**Parâmetros de consulta:**
- `page` (opcional): Número da página (padrão: 0)
- `size` (opcional): Tamanho da página (padrão: 20)
- `sort` (opcional): Campo de ordenação (ex: name,asc)

**Exemplo:**
```http
GET /categories?page=0&size=10
```

**Resposta de sucesso (200):**
```json
{
  "content": [
    {
      "id": 1,
      "name": "Alimentação"
    },
    {
      "id": 2,
      "name": "Transporte"
    },
    {
      "id": 3,
      "name": "Moradia"
    },
    {
      "id": 4,
      "name": "Entretenimento"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 4,
  "totalPages": 1
}
```

#### POST /categories
➕ **Criar nova categoria**

**Autorização:** ROLE_ADMIN

**Body:**
```json
{
  "name": "Saúde"
}
```

**Resposta de sucesso (200):**
```json
{
  "id": 5,
  "name": "Saúde"
}
```

#### PUT /categories/{categoryId}
✏️ **Atualizar categoria**

**Autorização:** ROLE_ADMIN

**Parâmetros de rota:**
- `categoryId` (obrigatório): ID da categoria

**Body:**
```json
{
  "name": "Saúde e Medicina"
}
```

**Resposta de sucesso (200):**
```json
{
  "id": 5,
  "name": "Saúde e Medicina"
}
```

#### DELETE /categories/{categoryId}
🗑️ **Excluir categoria**

**Autorização:** ROLE_ADMIN

**Parâmetros de rota:**
- `categoryId` (obrigatório): ID da categoria

**Resposta de sucesso (204):** Sem conteúdo

---

### Spends

#### GET /spends
💰 **Listar despesas**

**Autorização:** 
- **ROLE_USER**: Visualiza apenas suas próprias despesas
- **ROLE_ADMIN**: Pode visualizar despesas de todos os usuários

**Parâmetros de consulta:**
- `page` (opcional): Número da página (padrão: 0)
- `size` (opcional): Tamanho da página (padrão: 20)
- `userId` (opcional): ID do usuário (apenas ADMIN)
- `startDate` (opcional): Data de início (YYYY-MM-DD)
- `finalDate` (opcional): Data final (YYYY-MM-DD)
- `categoryId` (opcional): ID da categoria

**Exemplos:**

**Para ROLE_USER:**
```http
GET /spends?startDate=2025-01-01&finalDate=2025-12-31&categoryId=1
Authorization: Bearer {user_token}
```

**Para ROLE_ADMIN:**
```http
GET /spends?userId=2&startDate=2025-01-01&categoryId=1
Authorization: Bearer {admin_token}
```

**Resposta de sucesso (200):**
```json
{
  "content": [
    {
      "id": 1,
      "spendDate": "2025-12-23",
      "spendValue": 150.50,
      "description": "Compras no supermercado",
      "categoryId": 1,
      "userId": 2
    },
    {
      "id": 2,
      "spendDate": "2025-12-22",
      "spendValue": 45.00,
      "description": "Combustível",
      "categoryId": 2,
      "userId": 2
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 2,
  "totalPages": 1
}
```

#### POST /spends
➕ **Criar nova despesa**

**Autorização:** ROLE_USER ou ROLE_ADMIN

**Body:**
```json
{
  "spendDate": "2025-12-23",
  "spendValue": 89.90,
  "description": "Jantar no restaurante",
  "categoryId": 4,
  "userId": 2
}
```

**Resposta de sucesso (201):**
```json
{
  "id": 3,
  "spendDate": "2025-12-23",
  "spendValue": 89.90,
  "description": "Jantar no restaurante",
  "categoryId": 4,
  "userId": 2
}
```

#### PUT /spends/{spendId}
✏️ **Atualizar despesa**

**Autorização:** ROLE_USER (própria despesa) ou ROLE_ADMIN

**Parâmetros de rota:**
- `spendId` (obrigatório): ID da despesa

**Body:**
```json
{
  "spendDate": "2025-12-23",
  "spendValue": 95.00,
  "description": "Jantar no restaurante - atualizado",
  "categoryId": 4
}
```

**Resposta de sucesso (200):**
```json
{
  "id": 3,
  "spendDate": "2025-12-23",
  "spendValue": 95.00,
  "description": "Jantar no restaurante - atualizado",
  "categoryId": 4,
  "userId": 2
}
```

#### DELETE /spends/{spendId}
🗑️ **Excluir despesa**

**Autorização:** ROLE_USER (própria despesa) ou ROLE_ADMIN

**Parâmetros de rota:**
- `spendId` (obrigatório): ID da despesa

**Resposta de sucesso (204):** Sem conteúdo

---

## 📊 Modelos de Dados

### UserDTO
```json
{
  "id": "Long",
  "name": "String (obrigatório)",
  "email": "String (email válido, obrigatório)",
  "phone": "String (formato: ^\\+?[0-9. ()-]{7,25}$)",
  "password": "String (obrigatório)",
  "roles": ["String"] // Ex: ["ROLE_USER", "ROLE_ADMIN"]
}
```

### UserMinDTO (Response)
```json
{
  "id": "Long",
  "name": "String",
  "email": "String",
  "phone": "String"
}
```

### CategoryDTO
```json
{
  "id": "Long",
  "name": "String (obrigatório)"
}
```

### SpendDTO
```json
{
  "id": "Long",
  "spendDate": "LocalDate (YYYY-MM-DD)",
  "spendValue": "Double",
  "description": "String",
  "categoryId": "Long (obrigatório)",
  "userId": "Long (obrigatório)"
}
```

### SpendUpdateDTO
```json
{
  "spendDate": "LocalDate (YYYY-MM-DD)",
  "spendValue": "Double",
  "description": "String",
  "categoryId": "Long"
}
```

---

## 🔢 Códigos de Status

| Código | Descrição |
|--------|-----------|
| 200 | OK - Requisição bem-sucedida |
| 201 | Created - Recurso criado com sucesso |
| 204 | No Content - Requisição bem-sucedida sem conteúdo |
| 400 | Bad Request - Dados inválidos |
| 401 | Unauthorized - Token inválido ou ausente |
| 403 | Forbidden - Sem permissão para o recurso |
| 404 | Not Found - Recurso não encontrado |
| 409 | Conflict - Conflito de dados (ex: email duplicado) |
| 500 | Internal Server Error - Erro interno do servidor |

---

## 💡 Exemplos de Uso

### Fluxo Completo de Autenticação

1. **Obter Token:**
```bash
curl -X POST http://localhost:8080/oauth2/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -H "Authorization: Basic bXljbGllbnRpZDpteWNsaWVudHNlY3JldA==" \
  -d "grant_type=password&username=user@email.com&password=123456"
```

2. **Usar Token para Criar Despesa:**
```bash
curl -X POST http://localhost:8080/spends \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {seu_access_token}" \
  -d '{
    "spendDate": "2025-12-23",
    "spendValue": 50.00,
    "description": "Almoço",
    "categoryId": 1,
    "userId": 2
  }'
```

### Cenários Comuns

#### Usuário Comum (ROLE_USER)
```bash
# Listar suas próprias despesas
curl -X GET "http://localhost:8080/spends" \
  -H "Authorization: Bearer {user_token}"

# Criar nova despesa
curl -X POST http://localhost:8080/spends \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {user_token}" \
  -d '{
    "spendDate": "2025-12-23",
    "spendValue": 25.90,
    "description": "Lanche",
    "categoryId": 1,
    "userId": 2
  }'

# Filtrar despesas por período e categoria
curl -X GET "http://localhost:8080/spends?startDate=2025-12-01&finalDate=2025-12-31&categoryId=1" \
  -H "Authorization: Bearer {user_token}"
```

#### Administrador (ROLE_ADMIN)
```bash
# Listar todos os usuários
curl -X GET http://localhost:8080/users \
  -H "Authorization: Bearer {admin_token}"

# Criar nova categoria
curl -X POST http://localhost:8080/categories \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {admin_token}" \
  -d '{
    "name": "Educação"
  }'

# Visualizar despesas de um usuário específico
curl -X GET "http://localhost:8080/spends?userId=2" \
  -H "Authorization: Bearer {admin_token}"

# Visualizar todas as despesas
curl -X GET http://localhost:8080/spends \
  -H "Authorization: Bearer {admin_token}"
```

---

## 🔧 Configurações Adicionais

### Variáveis de Ambiente

```bash
# Cliente OAuth2
CLIENT_ID=myclientid
CLIENT_SECRET=myclientsecret

# JWT
JWT_DURATION=86400

# CORS
CORS_ORIGINS=http://localhost:3000,http://localhost:5173
```

### Profiles

- **test**: Perfil de desenvolvimento com H2 em memória
- **prod**: Perfil de produção (configurar banco de dados apropriado)

### Console H2

Durante o desenvolvimento, acesse `http://localhost:8080/h2-console` para visualizar e gerenciar o banco de dados em memória.

**Configurações:**
- Driver Class: `org.h2.Driver`
- JDBC URL: `jdbc:h2:mem:testdb`
- User Name: `sa`
- Password: (deixe em branco)

---

## 📝 Notas

- Todas as datas devem estar no formato ISO (YYYY-MM-DD)
- Valores monetários são representados como Double
- A paginação é baseada em zero (primeira página = 0)
- Tokens JWT têm validade de 24 horas por padrão
- Usuários ROLE_USER só podem gerenciar seus próprios recursos
- Administradores têm acesso total a todos os recursos

## 🚀 Deploy

Para ambiente de produção, configure um banco de dados adequado e atualize as variáveis de ambiente correspondentes no `application-prod.properties`.

---

**Desenvolvido com Spring Boot** 🍃
