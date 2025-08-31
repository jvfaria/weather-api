# Conventional Commits (guia rápido)

Utilizo [Conventional Commits](https://www.conventionalcommits.org/) para permitir que o `semantic-release` calcule a versão automaticamente.

## Tipos principais
- `feat:` → **MINOR** (nova feature, compatível)
- `fix:` → **PATCH** (bugfix)
- `docs:`, `chore:`, `build:`, `ci:`, `test:`, `refactor:` → não mudam versão (a menos que tenham `!`)

## Breaking change
- `feat!:` ou `fix!:` **ou** adicione no corpo: