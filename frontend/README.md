# Financial Planner - Frontend

Frontend Angular para o sistema de planejamento financeiro.

## Pré-requisitos

- Node.js (versão 18 ou superior)
- npm (geralmente vem com Node.js)

## Instalação

1. Navegue até a pasta do frontend:
```bash
cd frontend
```

2. Instale as dependências:
```bash
npm install
```

## Executando o Projeto

Para iniciar o servidor de desenvolvimento:

```bash
npm start
```

A aplicação estará disponível em `http://localhost:4200/`

## Funcionalidades Implementadas

### Autenticação
- **Tela de Login**: Interface moderna para autenticação de usuários
- **Integração OAuth2**: Conecta com o endpoint `/oauth2/token` do backend
- **Gerenciamento de Token**: Armazena e utiliza automaticamente o access token
- **Logout**: Funcionalidade para deslogar e limpar dados de sessão
- **Proteção de Rotas**: Todas as rotas de despesas são protegidas por autenticação

### Gerenciamento de Despesas

- **Listar Despesas**: Visualize todas as despesas do usuário logado com paginação
- **Criar Despesa**: Adicione novas despesas ao sistema
- **Editar Despesa**: Atualize informações de despesas existentes
- **Excluir Despesa**: Remova despesas do sistema

## Estrutura do Projeto

```
frontend/
├── src/
│   ├── app/
│   │   ├── components/
│   │   │   ├── login/                # Componente de login
│   │   │   ├── spend-list/          # Componente de listagem de despesas
│   │   │   └── spend-form/          # Componente de formulário de despesas
│   │   ├── models/
│   │   │   ├── auth.model.ts        # Modelos de autenticação
│   │   │   ├── spend.model.ts       # Modelos de dados de despesas
│   │   │   └── category.model.ts    # Modelo de categoria
│   │   ├── services/
│   │   │   ├── auth.service.ts      # Serviço de autenticação
│   │   │   ├── spend.service.ts     # Serviço para API de despesas
│   │   │   └── category.service.ts  # Serviço para API de categorias
│   │   ├── interceptors/
│   │   │   └── auth.interceptor.ts  # Interceptor para adicionar token
│   │   ├── guards/
│   │   │   └── auth.guard.ts        # Guard para proteger rotas
│   │   ├── app.component.ts         # Componente raiz
│   │   ├── app.routes.ts            # Configuração de rotas
│   │   └── app.config.ts            # Configuração da aplicação
│   ├── styles.css                   # Estilos globais
│   └── index.html                   # HTML principal
### Configuração OAuth2
No serviço de autenticação (`auth.service.ts`), você precisa ajustar:
- **Client ID e Client Secret**: Na linha do Basic Auth, altere `myclientid:myclientsecret` pelos valores configurados no seu backend
- **Grant Type**: Está configurado como `password` para OAuth2 Resource Owner Password Credentials

### Credenciais de Teste
Para testar, utilize as credenciais que estão configuradas no seu backend (conforme `data.sql` ou configuração de usuários).
## Configuração da API

A aplicação está configurada para se conectar ao backend na URL `http://localhost:8080`.

Se o seu backend estiver em outra porta ou endereço, atualize os arquivos:
- `src/app/services/spend.service.ts`
- `src/app/services/category.service.ts`

## Scripts Disponíveis

- `npm start` - Inicia o servidor de desenvolvimento
- `npm run build` - Cria a build de produção
- `npm run watch` - Cria a build em modo watch
- `npm test` - Executa os testes

## Próximos Passos

- Implementar autenticação e autorização
- Adicionar filtros avançados na listagem
- Criar gráficos e relatórios
- IComo Usar

1. **Instale as dependências**: `npm install`
2. **Certifique-se de que o backend está rodando** em `http://localhost:8080`
3. **Inicie o frontend**: `npm start`
4. **Acesse**: `http://localhost:4200`
5. **Faça login** com as credenciais configuradas no backend
6. **Gerencie suas despesas** através da interface

### Fluxo de Uso
1. A aplicação redireciona automaticamente para `/login` se não estiver autenticado
2. Após o login bem-sucedido, você será direcionado para a lista de despesas
3. Todas as requisições incluem automaticamente o token de autorização
4. As despesas são filtradas automaticamente pel

1. **Certifique-se de que o backend está rodando** antes de iniciar o frontend
2. **CORS**: O backend precisa estar configurado para aceitar requisições do frontend
3. **Categoria e Usuário**: Por enquanto, o userId está fixo como 1. Você precisará implementar autenticação para usar o usuário logado
