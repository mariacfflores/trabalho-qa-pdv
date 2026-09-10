# pdv
Sistema de ERP web desenvolvido em Java com Spring Framework 

# Recursos
- Cadastro produtos/clientes/fornecedor
- Controle de estoque
- Gerenciar comandas
- Realizar venda
- Controle de fluxo de caixa
- Controle de pagar e receber
- Venda com cartões
- Gerenciar permissões de usuários por grupos
- Cadastrar novas formas de pagamentos
- Relatórios

# Instalação
Para instalar o sistema, você deve criar o banco de dado "pdv" no mysql e configurar o arquivo application.properties
com os dados do seu usuário root do mysql e rodar o projeto pelo Eclipse ou gerar o jar do mesmo e execultar.

# Logando no sistema
Para logar no sistema, use o usuário "gerente" e a senha "123".

# Tecnologias utilizadas
- Spring Framework 5
- Thymeleaf 3
- MySQL
- Hibernate
- FlyWay

# Execução com Docker
Para executar a aplicação utilizando o docker, utilize o seguinte comando na raiz do projeto:
```sh
docker compose up -d
```

## Projeto original

Este trabalho utiliza como base o projeto PDV, disponível em:

https://github.com/repo-software-testing-courses/pdv

O código-fonte original foi clonado do repositório acima e seu histórico
Git foi preservado.

Para permitir a execução do sistema no ambiente local utilizado pelo grupo,
foram realizados ajustes de configuração nos arquivos `Dockerfile` e
`docker-compose.yml`. As alterações realizadas podem ser consultadas no
histórico de commits deste repositório.


# Evidências de Complexidade Ciclomática

A complexidade ciclomática das classes candidatas foi medida com o SonarQube.

| Classe | Complexidade ciclomática |
|---|---:|
| CaixaService.java | 34 |
| VendaService.java | 29 |
| NotaFiscalItemService.java | 24 |
| RecebimentoService.java | 20 |
| CartaoLancamentoService.java | 14 |

Todas as classes selecionadas apresentam complexidade ciclomática maior ou igual a 10.


## Análise de Complexidade

A complexidade ciclomática das classes candidatas foi medida utilizando o SonarQube.

Classes selecionadas:

- `CaixaService.java` — 34
- `VendaService.java` — 29
- `NotaFiscalItemService.java` — 24
- `RecebimentoService.java` — 20
- `CartaoLancamentoService.java` — 14

Evidências:
[docs/evidencias/complexidade](docs/evidencias/complexidade)