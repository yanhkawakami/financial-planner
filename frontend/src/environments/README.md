# Arquivos de Variáveis de Ambiente

Este diretório contém os arquivos de configuração para diferentes ambientes.

## Arquivos

- `environment.ts` - Arquivo base (fallback)
- `environment.development.ts` - Ambiente de desenvolvimento (localhost)
- `environment.homolog.ts` - Ambiente de homologação
- `environment.production.ts` - Ambiente de produção

## Como usar

### Desenvolvimento (padrão)
```bash
ng serve
# ou
ng serve --configuration development
```

### Homologação
```bash
ng serve --configuration homolog
# ou
ng build --configuration homolog
```

### Produção
```bash
ng build --configuration production
# ou simplesmente
ng build
```

## Configuração

Antes de fazer deploy, atualize as URLs nos arquivos:
- `environment.homolog.ts` - Substitua `https://api-homolog.seudominio.com` pela URL da API de homologação
- `environment.production.ts` - Substitua `https://api.seudominio.com` pela URL da API de produção
