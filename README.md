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

## Projeto original - QA UFF 2026.2

### Grupo: Caio Lima, Maria Clara Flores, Paulo Cesar Carrano e Rafael Valverde

Este trabalho utiliza como base o projeto PDV, disponível em:

https://github.com/repo-software-testing-courses/pdv

O código-fonte original foi clonado do repositório acima e seu histórico
Git foi preservado.

Para permitir a execução do sistema no ambiente local utilizado pelo grupo,
foram realizados ajustes de configuração nos arquivos `Dockerfile` e
`docker-compose.yml`. As alterações realizadas podem ser consultadas no
histórico de commits deste repositório.

# Plano de Teste

O Plano de Teste do grupo, cobrindo escopo, metodologia, papéis e responsabilidades, critérios de completude, entregáveis e recursos utilizados na Entrega 1, está disponível em:

[Plano de Teste Grupo](https://docs.google.com/document/d/1N3Xuw2kyrPlRuP0aiL2iPkg-wxku48xo6Q0gTgvUDxU/edit?usp=sharing)

[Plano de Teste Caio](https://docs.google.com/document/d/1HouMVtqafT6ep99kNRwE5euOWJaCqn8d_pxWfyq-WeM/edit?usp=drivesdk)

[Plano de Teste Maria e Paulo (entrega 1)](https://docs.google.com/document/d/1PfXhP2IU5gOzz4gJFks5v7bhcU9vVkbyapyMUr6iKgM/edit?tab=t.0) 

[Plano de Teste Maria (entrega 2)](https://docs.google.com/document/d/10NS--6N_uLYLpVfLzLJAnMINwDydRKOCB775BK9Z8uE/edit?usp=sharing) 

[Plano de Teste Paulo (entrega 2)](https://docs.google.com/document/d/1_vq40IAiF3ems6j0cztAtJ9YXxmOqY2h3qoynlZDJsU/edit?tab=t.0)

[Plano de Teste Rafael](https://docs.google.com/document/d/14oNxYhkQSSI2wKQV0KaiXTLX1JOkNECBWBm7FbJdrw0/edit?tab=t.0#heading=h.1voh1nmhiv9s)

# Evidências de Complexidade Ciclomática

A complexidade ciclomática das classes candidatas foi medida com o SonarQube.

| Classe                       | Complexidade ciclomática | Responsável          |
| ---------------------------- | -----------------------: | -------------------- |
| RecebimentoService.java      |                       20 | Caio Lima            |
| NotaFiscalService.java       |                       16 | Rafael Valverde      |
| CartaoLancamentoService.java |                       14 | Maria Clara Flores   |
| UsuarioService.java          |                       11 | Paulo Cesar Carrano  |

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

# Classe CartaoLancamentoService

Esta seção reúne os artefatos de teste produzidos pela integrante Maria, referentes à funcionalidade de lançamento de cartão (`CartaoLancamentoService`).

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

O arquivo gerado no Testlink relativo ao caso de teste 01 foi adicionado na pasta de [testes_manuais](https://github.com/mariacfflores/trabalho-qa-pdv/tree/master/docs/testes_manuais) 

Durante a execução, foi identificado um defeito no cadastro de pessoa: o campo `Número` do endereço aceita entrada maior do que o limite da coluna no banco (`VARCHAR(6)`), causando erro 500 mascarado por tratamento de exceção genérico. Detalhes na issue: [Github Issue](https://github.com/mariacfflores/trabalho-qa-pdv/issues/6).

# Classe RecebimentoService

Esta seção reúne os artefatos de teste produzidos pelo integrante Caio, referentes à funcionalidade de recebimento de títulos (`RecebimentoService`).

## Testes Unitários

Testes do método `abrirRecebimento` da classe `RecebimentoService`, cobrindo o caminho feliz, validações de negócio (parcela quitada, parcela de outro cliente, cliente inexistente), cálculo do valor total e array de parcelas vazio:

[`src/test/java/net/originmobi/pdv/service/RecebimentoServiceTest.java`](src/test/java/net/originmobi/pdv/service/RecebimentoServiceTest.java)

Para executar:

```sh
mvn test -Dtest=RecebimentoServiceTest
```

## Testes Manuais — PDV (Recebimento de Título)

Caso de teste manual executado no TestLink, cobrindo o fluxo de criação de pedido, geração de venda e recebimento de título com desconto.

| ID               | Cenário                                     | Resultado |
| ---------------- | -------------------------------------------- | --------- |
| PDVQA: PDV - QA  | Realizar recebimento de título com desconto | Falhado   |

Durante a execução, foi identificado um defeito na tela de pagamento da venda: o campo "Titulo" não é preenchido com nenhuma opção após a seleção da Forma de Pagamento, impedindo a conclusão do pagamento e gerando um erro não tratado ("Zero length string"). Detalhes na issue: [Github Issue](https://github.com/mariacfflores/trabalho-qa-pdv/issues/9).

Evidência da execução: [`docs/testes_manuais/TesteLink_RecebimentoTitulo.pdf`](docs/testes_manuais/TesteLink_RecebimentoTitulo.pdf)

# Classe NotaFiscalService.java

Esta seção reúne os artefatos de teste produzidos pelo integrante Rafael, referentes à funcionalidade de nota fiscal (`NotaFiscalService`).

## Testes Unitários

Testes dos métodos `lista(), busca(...), totalNotaFiscalEmitidas(), geraDV(...) e cadastrar(...)` da classe `NotaFiscalService.java`, cobrindo a listagem de notas fiscais, busca por determinada nota, recebimento do total de notas, geração do dígito verificador e cadastro de novas notas, tanto em seus caminhos positivos como negativos:

[`src/test/java/net/originmobi/pdv/service/notafiscal/NotaFiscalServiceTest.java`](src/test/java/net/originmobi/pdv/service/notafiscal/NotaFiscalServiceTest.java)

Para rodar os testes você pode rodar na IDE Eclipse ou executar:

```sh
mvn test -Dtest=NotaFiscalServiceTest
```

## Testes Manuais — PDV (Criação de Despesa)

Caso de teste manual executado no TestLink, cobrindo o fluxo de uma despesa.

| ID                                                                          | Cenário                           | Resultado |
| --------------------------------------------------------------------------- | --------------------------------- | --------- |
| Projeto de Teste: PDVQA:PDV-QA / Plano de Teste: Entrega 1 - PDV - despesas | Realizar a criação de uma despesa | Sucesso   |

Evidência da execução: [`docs/testes_manuais/TestLink_CriaDespesa.pdf`](docs/testes_manuais/TestLink_CriaDespesa.pdf)

## Uso de Inteligência Artificial

As interações com IA foram registradas em [`docs/ai/AI-LOG.md`](docs/ai/AI-LOG.md)

# Entrega 2 — Testes, Qualidade e Inspeção de Código

Esta seção reúne os artefatos desenvolvidos para a Entrega 2 do trabalho.

Nesta etapa, cada integrante evoluiu os testes da classe selecionada anteriormente com base na complexidade ciclomática, incluindo ampliação dos testes unitários, testes de integração, análise estrutural, teste baseado em defeitos, inspeção estática do código e, quando aplicável, testes de sistema.

## Testes unitários

Os testes unitários da `CartaoLancamentoService` foram ampliados de **8 para 14 casos de teste**.

As dependências externas da classe foram isoladas utilizando Mockito, incluindo:

- `CartaoLancamentoRepository`;
- `CaixaLancamentoService`;
- `UsuarioService`.

Também foi utilizado `ArgumentCaptor` nos testes de `lancamento()` para validar os valores do objeto enviado ao repositório, incluindo taxas, valores líquidos, tipo do cartão e demais atributos calculados.

Arquivo:

```text
src/test/java/net/originmobi/pdv/service/cartao/CartaoLancamentoServiceTest.java
```

Para executar:

```powershell
mvn test "-Dtest=CartaoLancamentoServiceTest"
```

Resultado obtido:

```text
Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## Testes de integração

Foram implementados **3 testes de integração** para verificar a interação da `CartaoLancamentoService` com os serviços envolvidos nos fluxos de processamento e antecipação.

Arquivo:

```text
src/test/java/net/originmobi/pdv/service/cartao/CartaoLancamentoServiceIntegrationTest.java
```

Para executar:

```powershell
mvn test "-Dtest=CartaoLancamentoServiceIntegrationTest"
```

Resultado obtido:

```text
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## Cobertura estrutural — JaCoCo

A cobertura estrutural da `CartaoLancamentoService` foi medida utilizando o JaCoCo.

Resultados obtidos:

| Métrica | Resultado |
| ------- | --------: |
| Cobertura de instruções | 98% |
| Cobertura de branches | 90% |

A cobertura de branches superou a meta mínima de **80%** estabelecida para o critério de todas-arestas.

As evidências estão armazenadas em:

```text
docs/evidencias/cobertura/
```

## Teste baseado em defeitos — PIT Mutation Testing

O PIT Mutation Testing foi executado sobre a mesma classe utilizada para a análise estrutural.

Resultados:

| Métrica | Resultado |
| ------- | --------: |
| Mutações geradas | 22 |
| Mutações eliminadas | 20 |
| Mutation Coverage | 91% |
| Test Strength | 95% |

O escore de mutação obtido superou a meta mínima de **80%**.

As evidências estão armazenadas em:

```text
docs/evidencias/mutacao/
```

## Testes de sistema com Selenium

Foram implementados testes de sistema utilizando Selenium WebDriver.

Os testes automatizam a utilização da aplicação pelo navegador e dependem da aplicação estar em execução.

Antes de executar os testes Selenium, suba o ambiente:

```powershell
docker compose up -d
```

A aplicação deve ficar disponível em:

```text
http://localhost:8080
```

### Configuração do ChromeDriver

Os testes Selenium utilizam o Google Chrome.

É necessário utilizar um ChromeDriver compatível:

- com o sistema operacional da máquina;
- com a versão instalada do Google Chrome.

Crie uma pasta chamada `drivers` na raiz do projeto:

```text
pdv/
├── drivers/
├── src/
├── docs/
├── pom.xml
└── ...
```

Coloque o executável do ChromeDriver dentro dessa pasta.

No ambiente Windows utilizado durante o desenvolvimento, o caminho esperado é:

```text
drivers/chromedriver.exe
```

A pasta `drivers/` está presente no `.gitignore`, portanto o executável não é versionado no repositório.

Cada ambiente de execução deve utilizar o ChromeDriver correspondente ao seu sistema operacional e à versão instalada do Chrome.

## Teste funcional Selenium — antecipação de cartão

Arquivo:

```text
src/test/java/net/originmobi/pdv/selenium/CartaoSeleniumTest.java
```

O teste automatiza o fluxo de antecipação de um lançamento de cartão.

O fluxo validado é:

```text
A Processar -> Antecipado
```

### Pré-condição

Antes de executar o teste, deve existir **pelo menos um lançamento de cartão com situação `A Processar`**.

Uma forma de preparar essa massa de teste pela aplicação é:

1. garantir que exista um produto com estoque disponível;
2. criar um pedido;
3. gerar a venda;
4. realizar o pagamento utilizando cartão de crédito.

Após o pagamento, deve existir um lançamento disponível na área de gerenciamento de cartões com situação:

```text
A Processar
```

O teste:

1. realiza o login;
2. acessa a área de gerenciamento de cartões;
3. localiza um lançamento disponível para antecipação;
4. seleciona a opção `Antecipar`;
5. confirma a operação;
6. verifica se a situação foi alterada para `Antecipado`.

Para executar:

```powershell
mvn test "-Dtest=CartaoSeleniumTest"
```

## Teste não funcional de desempenho

Também foi implementado um teste Selenium para avaliar o tempo de carregamento da tela de gerenciamento de cartões.

Arquivo:

```text
src/test/java/net/originmobi/pdv/selenium/CartaoDesempenhoSeleniumTest.java
```

O critério adotado foi:

```text
Tempo máximo para a tela ficar disponível para interação: 5 segundos
```

Na execução registrada durante o desenvolvimento, o tempo medido foi:

```text
986 ms
```

O resultado ficou abaixo do limite definido.

Este teste avalia o tempo de resposta observado no ambiente utilizado no trabalho e não corresponde a um teste de carga ou estresse.

Para executar:

```powershell
mvn test "-Dtest=CartaoDesempenhoSeleniumTest"
```

O teste de desempenho exige que a aplicação esteja em execução, mas não depende da existência de um lançamento com situação `A Processar`.

## Inspeção estática — SonarQube

A `CartaoLancamentoService` também foi analisada utilizando SonarQube.

### Antes das correções

A análise inicial apresentou:

```text
25 issues
```

sendo:

```text
1 Bug
24 Code Smells
```

Entre os problemas identificados estavam:

- acesso ao conteúdo de `Optional` sem validação prévia;
- inicializações e atribuições desnecessárias;
- nomes de variáveis fora da convenção;
- uso de `System.out`;
- uso de exceções genéricas;
- outros problemas de manutenibilidade apontados pela ferramenta.

### Depois das correções

A classe foi refatorada e uma exceção específica para lançamentos de cartão foi criada.

Após as correções, uma nova análise foi realizada:

```text
0 issues
0 effort
```

Os 14 testes unitários e os 3 testes de integração continuaram sendo executados com sucesso após as alterações.

Os resultados apresentados nas seções anteriores de JaCoCo e PIT correspondem à versão anterior às refatorações do SonarQube.

As evidências do SonarQube estão armazenadas em:

```text
docs/evidencias/sonar/
```

## Avaliação ISO/IEC 25010

A funcionalidade também foi avaliada com base nas oito características de qualidade do produto da **ISO/IEC 25010:2011**.

Foi utilizada uma escala de 1 a 5, considerando:

- a qualidade observada no escopo analisado;
- as medidas obtidas durante os testes;
- a quantidade e a força das evidências disponíveis.

A análise considera características como:

- adequação funcional;
- eficiência de desempenho;
- compatibilidade;
- usabilidade;
- confiabilidade;
- segurança;
- manutenibilidade;
- portabilidade.

A avaliação completa, incluindo medidas, notas e justificativas, está documentada no Plano de Teste.

## Observação sobre a execução de `mvn test`

A execução indiscriminada de:

```powershell
mvn test
```

pode apresentar erro no teste já existente:

```text
PdvApplicationTests.contextLoads
```

Esse teste utiliza `@SpringBootTest` e tenta inicializar o contexto completo da aplicação, incluindo a conexão com o banco de dados.

Quando o Maven é executado diretamente no host, o teste pode não conseguir resolver o hostname:

```text
pdv-db
```

utilizado pela rede configurada no Docker Compose.

Esse comportamento já existia anteriormente no projeto e não é causado pelos novos testes da Entrega 2.

Para validar os testes da `CartaoLancamentoService`, recomenda-se executar individualmente:

```powershell
mvn test "-Dtest=CartaoLancamentoServiceTest"
```

```powershell
mvn test "-Dtest=CartaoLancamentoServiceIntegrationTest"
```

```powershell
mvn test "-Dtest=CartaoDesempenhoSeleniumTest"
```

e, após preparar um lançamento com situação `A Processar`:

```powershell
mvn test "-Dtest=CartaoSeleniumTest"
```

---

# Classe RecebimentoService.java — Entrega 2

Seção destinada aos artefatos da Entrega 2 relacionados à `RecebimentoService`.

**Responsável:** Caio Lima.

---

# Classe NotaFiscalService.java — Entrega 2

Seção destinada aos artefatos da Entrega 2 relacionados à `NotaFiscalService`.

**Responsável:** Rafael Valverde.

---

# Classe UsuarioService.java — Entrega 2

Seção destinada aos artefatos da Entrega 2 relacionados à `UsuarioService`.

**Responsável:** Paulo Cesar Carrano.

## Testes unitários

Os testes unitários da `UsuarioService` foram ampliados de **5 para 18 casos de teste**.

As dependências externas da classe foram isoladas utilizando Mockito:

- `UsuarioRepository`;
- `GrupoUsuarioService`.

Os novos casos cobrem:

- cadastro com username já existente e com pessoa já vinculada a outro usuário;
- criptografia da senha (BCrypt) e preenchimento da data de cadastro;
- atualização sem verificação de duplicidade e tratamento de erro ao salvar;
- listagem vazia;
- adição de grupo já existente;
- remoção de grupo, incluindo grupo não vinculado e erro ao salvar;
- busca de usuário inexistente.

Nos novos casos foram utilizados objetos reais de `Usuario`, `Pessoa` e `GrupoUsuario`, permitindo verificar os valores gravados pelo serviço, como a senha criptografada e a lista de grupos, e não apenas se os métodos foram chamados.

O caso **CT17** (`deveRemoverGrupoComCodigoMaiorQue127`) evidenciou um defeito em `removeGrupo`: os códigos dos grupos eram comparados com `==`, que compara a referência dos objetos `Long` e não o valor. Como o Java só reaproveita objetos `Long` entre -128 e 127, grupos com código acima de 127 nunca eram removidos. O defeito foi corrigido com `.equals()` (ver seção do SonarQube).

Arquivo:

```text
src/test/java/net/originmobi/pdv/service/UsuarioServiceTest.java
```

Para executar:

```powershell
mvn test "-Dtest=UsuarioServiceTest"
```

Resultado obtido:

```text
Tests run: 18, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## Testes de integração

Foram implementados **3 testes de integração** para verificar a interação entre a `UsuarioService` e a `GrupoUsuarioService`, ambas reais. Apenas os repositórios foram substituídos por dublês de teste.

Os cenários são:

- adição de grupo ao usuário;
- adição de grupo já existente;
- remoção de grupo do usuário.

Arquivo:

```text
src/test/java/net/originmobi/pdv/service/UsuarioServiceIntegrationTest.java
```

Para executar:

```powershell
mvn test "-Dtest=UsuarioServiceIntegrationTest"
```

Resultado obtido:

```text
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## Cobertura estrutural — JaCoCo

A cobertura estrutural da `UsuarioService` foi medida utilizando o JaCoCo.

Resultados obtidos:

| Métrica | Entrega 1 | Entrega 2 |
| ------- | --------: | --------: |
| Cobertura de instruções | 60% | 100% |
| Cobertura de branches | 42% (5/12) | 100% (12/12) |

A cobertura de branches superou a meta mínima de **80%** estabelecida para o critério de todas-arestas.

Para gerar o relatório:

```powershell
mvn test "-Dtest=UsuarioServiceTest" jacoco:report
```

O relatório é gerado em `target/site/jacoco/index.html`.

Observação: com a opção `-DforkCount=0` os testes são executados, mas o JaCoCo não registra a cobertura. Para medir a cobertura, a opção não deve ser utilizada.

As evidências estão armazenadas em:

```text
docs/evidencias/cobertura/
```

## Teste baseado em defeitos — PIT Mutation Testing

O PIT Mutation Testing foi executado sobre a mesma classe utilizada para a análise estrutural.

Como os testes da `UsuarioService` utilizam JUnit 5, foi adicionado ao `pom.xml` o `pitest-junit5-plugin`. Sem ele, o PIT não executa testes JUnit 5. A execução do PIT da `CartaoLancamentoService` não foi alterada.

Resultados:

| Métrica | Entrega 1 | Entrega 2 |
| ------- | --------: | --------: |
| Mutações geradas | 18 | 18 |
| Mutações eliminadas | 8 | 18 |
| Mutation Coverage | 44% | 100% |
| Test Strength | — | 100% |

O escore de mutação obtido superou a meta mínima de **80%**.

Na Entrega 1, dois mutantes sobreviviam mesmo com as linhas cobertas: a remoção da chamada que preenche a data de cadastro e a remoção da chamada que grava a senha criptografada. Os testes passavam por essas linhas, mas não verificavam seus efeitos. O caso **CT09** passou a verificar os dois.

Como a configuração do PIT no `pom.xml` aponta para a `CartaoLancamentoService`, a classe e os testes são informados na linha de comando:

```powershell
mvn test-compile org.pitest:pitest-maven:mutationCoverage "-DtargetClasses=net.originmobi.pdv.service.UsuarioService" "-DtargetTests=net.originmobi.pdv.service.UsuarioServiceTest"
```

O relatório é gerado em `target/pit-reports/index.html`.

As evidências estão armazenadas em:

```text
docs/evidencias/mutacao/
```

## Teste funcional Selenium — cadastro de usuário

Arquivo:

```text
src/test/java/net/originmobi/pdv/selenium/UsuarioSeleniumTest.java
```

O teste automatiza o fluxo de cadastro de usuário pela interface.

A execução exige a aplicação em execução e o ChromeDriver configurado, conforme descrito em [Configuração do ChromeDriver](#configuração-do-chromedriver).

### Pré-condição

O cadastro de usuário exige uma pessoa ainda não vinculada a outro usuário. Por isso, o próprio teste cadastra uma nova pessoa antes de cadastrar o usuário.

Como o sistema não aceita CPF nem username repetidos, o teste gera dados únicos a cada execução. Não é necessário preparar massa de teste.

O campo "Número" do endereço é preenchido com até 6 caracteres, devido ao defeito registrado na [issue #6](https://github.com/mariacfflores/trabalho-qa-pdv/issues/6).

O teste:

1. realiza o login;
2. cadastra uma nova pessoa;
3. cadastra um usuário vinculado à pessoa criada;
4. verifica a mensagem `Usuário salvo com sucesso`;
5. verifica se o usuário aparece na listagem, vinculado à pessoa correta.

Para executar:

```powershell
mvn test "-Dtest=UsuarioSeleniumTest"
```

Resultado obtido:

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Durante a execução do teste, foi encontrado um defeito no cadastro de pessoa: a data de nascimento é gravada incorreta, pois o datepicker da tela e o servidor utilizam formatos de data diferentes. O defeito foi registrado na [issue #23](https://github.com/mariacfflores/trabalho-qa-pdv/issues/23).

## Inspeção estática — SonarQube

A `UsuarioService` foi analisada utilizando o SonarQube Community Build 26.10, com o perfil de regras padrão.

A análise confirmou a complexidade ciclomática da classe:

```text
11
```

### Antes da correção

A análise inicial apresentou:

```text
1 issue
```

sendo:

```text
1 Bug (Reliability)
```

A issue (`java:S4973`) apontou a comparação de objetos `Long` com `==` em `removeGrupo`. É o mesmo defeito evidenciado pelo caso de teste **CT17**, encontrado de forma independente pelo teste e pela inspeção estática.

### Depois da correção

A comparação foi alterada para `.equals()`.

Após a correção, uma nova análise foi realizada:

```text
0 issues
```

Os 18 testes unitários continuaram sendo executados com sucesso após a correção, incluindo o CT17, que antes falhava. Os resultados de JaCoCo e PIT apresentados acima correspondem à versão posterior à correção.

As evidências do SonarQube estão armazenadas em:

```text
docs/evidencias/sonar/
```

## Avaliação ISO/IEC 25010

A funcionalidade de cadastro e gerenciamento de usuários também foi avaliada com base nas oito características de qualidade do produto da **ISO/IEC 25010:2011**, utilizando uma escala de 1 a 5.

A avaliação completa, incluindo medidas, notas e justificativas, está documentada no [Plano de Teste Paulo (entrega 2)](https://docs.google.com/document/d/1_vq40IAiF3ems6j0cztAtJ9YXxmOqY2h3qoynlZDJsU/edit?tab=t.0).

## Observação sobre a execução de `mvn test`

Assim como descrito na seção da `CartaoLancamentoService`, a execução de `mvn test` sem filtro pode falhar em `PdvApplicationTests.contextLoads` quando o banco não está acessível. Os testes Selenium também dependem da aplicação em execução.

Para validar os testes da `UsuarioService`, recomenda-se executar individualmente:

```powershell
mvn test "-Dtest=UsuarioServiceTest"
```

```powershell
mvn test "-Dtest=UsuarioServiceIntegrationTest"
```

e, com a aplicação em execução:

```powershell
mvn test "-Dtest=UsuarioSeleniumTest"
```
