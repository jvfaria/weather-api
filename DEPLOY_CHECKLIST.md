# Checklist de Deploy (DEV → STAGING → PROD)

## Geral
- [ ] CI verificado (build + testes OK)
- [ ] Commits no padrão Conventional
- [ ] `fetch-depth: 0` no checkout dos workflows

## DEV (branch develop)
- [ ] semantic-release gerou versão `*-dev.N`
- [ ] Deploy no Railway DEV ok
- [ ] APP_VERSION setado no service DEV
- [ ] Healthcheck `/actuator/health` ok

## STAGING (branch release/*)
- [ ] semantic-release gerou `*-rc.N`
- [ ] Deploy no Railway STAGING ok
- [ ] Testes manuais/QA validados
- [ ] Logs/erros sem regressões

## PROD (tag vX.Y.Z)
- [ ] Auto Deploy do service PROD **desligado** no Railway
- [ ] Tag `vX.Y.Z` criada pela release da `main`
- [ ] Workflow de `tags` executou deploy no service PROD
- [ ] APP_VERSION = `X.Y.Z` no PROD
- [ ] Monitoramento (UptimeRobot) verde
- [ ] Rollback documentado (tag anterior)
