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

| Classe                       | Complexidade ciclomática |
| ---------------------------- | -----------------------: |
| RecebimentoService.java      |                       20 |
| NotaFiscalService.java       |                       16 |
| CartaoLancamentoService.java |                       14 |
| UsuarioService.java          |                       11 |

Todas as classes selecionadas apresentam complexidade ciclomática maior ou igual a 10.

## Análise de Complexidade

A complexidade ciclomática das classes candidatas foi medida utilizando o SonarQube.

Classes selecionadas:

- `RecebimentoService.java` — 20
- `NotaFiscalService.java` — 16
- `CartaoLancamentoService.java` — 14
- `UsuarioService.java` — 11

Evidências:
[docs/evidencias/complexidade](docs/evidencias/complexidade)

# Testes Manuais

Os testes manuais do sistema são documentados e executados utilizando o TestLink.

Para executar a aplicação localmente, utilize:

```sh
docker compose up -d
```

A aplicação pode ser acessada em:

```text
http://localhost:8080
```

Credenciais padrão:

- Usuário: `gerente`
- Senha: `123`

## Preparação da massa de teste

Alguns casos de teste exigem dados previamente cadastrados no sistema.

Para a execução do caso de teste `CT-CART-01 - Antecipar lançamento de cartão com sucesso`, foi necessário preparar a seguinte massa de teste:

- banco aberto do tipo `BANCO`;
- máquina de cartão associada ao banco;
- título do tipo `Cartão Crédito`;
- produto com estoque disponível;
- venda realizada utilizando cartão de crédito;
- lançamento de cartão com situação `A Processar`.

## CT-CART-01 - Antecipar lançamento de cartão com sucesso

O objetivo do teste é verificar se um lançamento de cartão com situação `A Processar` pode ser antecipado corretamente.

Fluxo executado:

1. acessar a área `Gerenciar Cartões`;
2. localizar um lançamento com situação `A Processar`;
3. selecionar a opção `Antecipar`;
4. confirmar a antecipação;
5. verificar a situação final do lançamento.

Resultado obtido:

```text
A Processar -> Antecipado
```

O caso de teste foi executado com sucesso e registrado como **Passou** no TestLink.

## Evidências

As evidências relacionadas à execução dos testes manuais são armazenadas no diretório:

```text
docs/evidencias/
```

Durante a preparação da massa de teste, também foi identificado um comportamento anômalo no ajuste de estoque: embora o ajuste fosse apresentado pelo sistema como `Processado`, a quantidade disponível do produto permanecia igual a zero. Esse comportamento deve ser tratado separadamente como um possível defeito do sistema e não faz parte do escopo do `CT-CART-01`.

# Classe UsuarioService

Esta seção reúne os artefatos de teste produzidos pelo integrante Paulo, referentes à funcionalidade de cadastro e gerenciamento de usuários (`UsuarioService`).

## Plano de Teste

[Arquivo Google Docs](https://docs.google.com/document/d/1PfXhP2IU5gOzz4gJFks5v7bhcU9vVkbyapyMUr6iKgM/edit?tab=t.0)

## Testes Unitários

Testes da classe `UsuarioService`, casos CT01–CT05, cobrindo cadastro de usuário novo, atualização, listagem, adição de grupo e busca por username:

[`src/test/java/net/originmobi/pdv/service/UsuarioServiceTest.java`](src/test/java/net/originmobi/pdv/service/UsuarioServiceTest.java)

Para executar:

```sh
mvn test -DforkCount=0 -Dtest=UsuarioServiceTest
```

## Testes Manuais — CT-USU-01 a CT-USU-03 (Cadastro de Usuário)

Casos de teste da funcionalidade de cadastro de usuário, cobrindo o fluxo de sucesso e as duas validações de duplicidade implementadas em `UsuarioService.cadastrar()`.

| ID        | Cenário                                         | Resultado |
| --------- | ----------------------------------------------- | --------- |
| CT-USU-01 | Cadastro de usuário com sucesso                 | Passou    |
| CT-USU-02 | Cadastro com `user` já existente                | Passou    |
| CT-USU-03 | Cadastro de pessoa já vinculada a outro usuário | Passou    |

Durante a execução, foi identificado um defeito no cadastro de pessoa: o campo `Número` do endereço aceita entrada maior do que o limite da coluna no banco (`VARCHAR(6)`), causando erro 500 mascarado por tratamento de exceção genérico. Detalhes na issue: [Github Issue](https://github.com/mariacfflores/trabalho-qa-pdv/issues/6).

# Classe RecebimentoService

Esta seção reúne os artefatos de teste produzidos pelo integrante Caio, referentes à funcionalidade de recebimento de títulos (`RecebimentoService`).

## Plano de Teste

[Arquivo Google Docs](https://docs.google.com/document/d/1HouMVtqafT6ep99kNRwE5euOWJaCqn8d_pxWfyq-WeM/edit?usp=sharing)

## Testes Unitários

Testes do método `abrirRecebimento` da classe `RecebimentoService`, cobrindo o caminho feliz, validações de negócio (parcela quitada, parcela de outro cliente, cliente inexistente), cálculo do valor total e array de parcelas vazio:

[`src/test/java/net/originmobi/pdv/service/RecebimentoServiceTest.java`](src/test/java/net/originmobi/pdv/service/RecebimentoServiceTest.java)

Para executar:

```sh
mvn test -Dtest=RecebimentoServiceTest
```

## Testes Manuais — PDV (Recebimento de Título)

Caso de teste manual executado no TestLink, cobrindo o fluxo de criação de pedido, geração de venda e recebimento de título com desconto.

| ID       | Cenário                                              | Resultado |
| -------- | ----------------------------------------------------- | --------- |
| PDVQA-1  | Realizar recebimento de título com desconto           | Falhado   |

Durante a execução, foi identificado um defeito na tela de pagamento da venda: o campo "Titulo" não é preenchido com nenhuma opção após a seleção da Forma de Pagamento, impedindo a conclusão do pagamento e gerando um erro não tratado ("Zero length string"). Detalhes na issue: [Github Issue](https://github.com/mariacfflores/trabalho-qa-pdv/issues/9).

Evidência da execução: [`docs/testes_manuais/TesteLink_RecebimentoTitulo.pdf`](docs/testes_manuais/TesteLink_RecebimentoTitulo.pdf)

## Uso de Inteligência Artificial

As interações com IA foram registradas em [`docs/ai/AI-LOG.md`](docs/ai/AI-LOG.md)
