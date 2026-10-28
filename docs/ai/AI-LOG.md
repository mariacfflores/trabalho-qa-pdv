# Registro de Uso de Inteligência Artificial

Este documento registra as interações com ferramentas de Inteligência Artificial Generativa que contribuíram de forma relevante para os artefatos do trabalho.

A IA foi utilizada como ferramenta de apoio ao planejamento, implementação e revisão dos testes. As decisões sobre escopo, regras a serem testadas, critérios de aceitação e adequação às entregas foram realizadas pela responsável pela atividade. As respostas produzidas pela IA foram analisadas e validadas antes de serem incorporadas ao projeto.

Interações relacionadas apenas a configuração de ambiente, execução de ferramentas ou ajustes operacionais não foram incluídas neste registro, por não contribuírem diretamente para a elaboração dos artefatos de teste.

---

## Registro — Planejamento, implementação e validação dos testes de cartões - entrega 1

**Responsável:** Maria Clara Flores

**Atividade:** Planejamento e execução dos testes unitários da `CartaoLancamentoService` e do teste manual de antecipação de cartão.

**Ferramenta:** ChatGPT

### Prompt/instrução

Solicitado apoio para elaborar e revisar testes unitários da CartaoLancamentoService, com foco nos métodos lancamento(), listar(), processar() e antecipar(). Foram indicados como cenários de interesse os lançamentos de débito e crédito, a listagem com e sem filtros e as restrições de processamento e antecipação conforme a situação do cartão. A IA foi orientada a utilizar JUnit e Mockito, evitar dependência direta do banco de dados e auxiliar na análise das falhas encontradas durante a execução. Também foi solicitado apoio para estruturar o teste manual de antecipação de cartão no TestLink.

### Resultado

A IA auxiliou na elaboração de uma proposta inicial de 14 testes unitários, envolvendo lançamento, listagem, processamento e antecipação de cartões. Também apoiou a estruturação do caso manual `CT-CART-01 — Antecipar lançamento de cartão com sucesso` e a preparação da massa de teste necessária à sua execução.

### Decisão

Após a primeira execução, foram mantidos oito testes unitários e adiados seis cenários que exigiam maior isolamento das dependências da aplicação. O teste manual foi mantido por complementar os testes unitários, verificando pela interface a antecipação de um lançamento elegível.

### Validação

As sugestões da IA foram avaliadas quanto à correspondência com as regras da classe, à viabilidade de execução e ao alcance das verificações propostas.

A primeira versão dos testes unitários apresentou oito aprovações, quatro falhas e dois erros. Esse resultado levou ao questionamento da solução inicial: os seis casos problemáticos dependiam de componentes que não estavam suficientemente isolados. Em vez de alterar os testes apenas para obter aprovação, decidiu-se adiá-los para a Entrega 2.

Também foi reconhecida uma limitação dos testes de lançamento: a chamada ao `save()` é verificada, mas os atributos e cálculos do objeto salvo não são conferidos individualmente.

A versão final foi reexecutada com **8 testes aprovados, 0 falhas, 0 erros e `BUILD SUCCESS`**.

No teste manual, as dificuldades de preparação dos dados foram investigadas separadamente do comportamento de antecipação. Após atender às pré-condições, o lançamento passou de `A Processar` para `Antecipado`, e o caso foi registrado como **Passou** no TestLink.

Assim, as propostas da IA foram analisadas, questionadas e ajustadas conforme os resultados observados, sem considerar a aprovação dos testes como comprovação de cobertura completa ou ausência de defeitos.

## Registro — Planejamento, implementação e validação dos testes de cartões - entrega 2

**Responsável:** Maria Clara Flores

**Ferramenta:** ChatGPT

### Prompt/instrução

Foi solicitado apoio para evoluir os 6 testes que haviam sido adiados na Entrega 1 por falta de isolamento adequado das dependências. Nesta etapa, eles foram retomados após o uso de mocks e ajustes na preparação dos testes.

Ao longo das interações, foram definidos critérios para orientar o uso da IA, como preservar as regras existentes da classe, isolar dependências com mocks quando adequado, validar os cálculos e efeitos dos métodos (e não apenas se uma chamada ocorreu) e não considerar cobertura, mutação ou correções como concluídas antes da execução das respectivas ferramentas.

Também foram fornecidos à IA detalhes do fluxo real da aplicação, principalmente para o Selenium, para que os testes refletissem as pré-condições e etapas efetivamente necessárias no PDV.

### Resultado

A IA apoiou a ampliação da suíte unitária de 8 para 14 testes, a implementação de 3 testes de integração e dos testes Selenium funcional e de desempenho.

Também auxiliou na interpretação dos relatórios de JaCoCo e PIT, na análise das issues apontadas pelo SonarQube e na organização da avaliação segundo a ISO 25010.

A versão final apresentou **14 testes unitários e 3 testes de integração aprovados, além de 98% de cobertura de instruções, 90% de branches e 91% de mutation coverage**.

### Decisão

As sugestões da IA não foram aplicadas automaticamente. Durante a implementação do Selenium, a responsável identificou que a solução proposta não considerava corretamente a preparação do ambiente e da massa de teste. O teste funcional dependia da aplicação em execução e da existência prévia de um lançamento em situação `A Processar`. Essa condição foi percebida durante a execução e levou à revisão das instruções e da preparação do cenário.

Também foi sugerido o uso de WebDriverManager para tornar a configuração do ChromeDriver mais portátil. A alternativa chegou a ser implementada, mas apresentou comportamento anômalo durante a execução do projeto. A mudança foi revertida e foi mantida a configuração por caminho relativo, posteriormente apresentada à professora e aprovada.

Na avaliação da ISO 25010, foi rejeitada a criação de faixas numéricas arbitrárias para justificar cada nota, pois esses limites não faziam parte dos requisitos do sistema. Optou-se por uma escala geral sustentada pelas evidências realmente produzidas.

No teste de mutação, 20 dos 22 mutantes foram eliminados, resultando em 91% de mutation coverage. Como o resultado já superava o mínimo de 80% exigido pelo trabalho, decidiu-se encerrar essa etapa sem realizar novas alterações apenas para elevar o percentual.

Na análise do SonarQube, algumas correções exigiram alterações no código de produção. Essas mudanças foram aceitas apenas quando não alteravam as regras de negócio da `CartaoLancamentoService`. Após as etapas de refatoração, os testes unitários e de integração foram reexecutados para verificar se o comportamento esperado havia sido preservado.

### Validação

As correções do SonarQube foram realizadas em etapas. A análise inicial apresentou 25 issues. Após uma primeira rodada de correções, os testes foram reexecutados antes de uma nova análise, que reduziu o resultado para 8 issues. Depois da segunda rodada, os testes foram novamente executados com sucesso e o SonarQube apresentou 0 issues.

Problemas de ambiente também foram tratados separadamente de falhas dos testes. Quando `mvn test` apresentou erro em `PdvApplicationTests.contextLoads`, os logs e o histórico do projeto foram verificados antes de qualquer alteração, constatando-se que a falha relacionada ao hostname `pdv-db` já existia anteriormente e não havia sido introduzida pelos novos testes.

Assim, a IA foi utilizada como apoio para propor e revisar soluções, enquanto a preparação do ambiente, a interpretação dos resultados, a rejeição de alternativas inadequadas e a validação final permaneceram sob responsabilidade da integrante.

## Registro — Planejamento, implementação e validação dos testes de recebimento

**Responsável:** Caio de Souza Lima

**Atividade:** Revisão e correção dos testes unitários da `RecebimentoService` e auxílio no teste manual de recebimento de título.

**Ferramenta:** Claude (Anthropic)

### Prompt/instrução

Os cenários de teste do método `abrirRecebimento` (caminho feliz, parcela já quitada, parcela de outro cliente, cliente inexistente e cálculo do valor total) haviam sido escritos manualmente antes da sessão. Foi solicitado à IA que revisasse o código já escrito, apontando más práticas com Mockito e lacunas de cobertura. Também foi solicitado apoio para estruturar o caso de teste manual de recebimento de título no TestLink e para investigar um erro encontrado durante sua execução.

### Resultado

A IA identificou problemas de conteúdo dos testes: stubs desnecessários, ausência de asserção sobre o valor calculado em `deveCalcularValorTotalDasParcelas`, verificação apenas do tipo da exceção (não da mensagem) em dois cenários de rejeição, e ausência de um cenário para array de parcelas vazio. No teste manual, apoiou a estruturação do caso `PDVQA: PDV - QA — Realizar recebimento de título com desconto` e a investigação do erro "Zero length string" encontrado ao tentar concluir o pagamento.

### Decisão

Foram aceitas as sugestões de reforçar as duas verificações de exceção com a mensagem exata e `verify(..., never())`, adicionar `ArgumentCaptor` ao teste de cálculo do valor total, e criar o teste do array de parcelas vazio. A investigação de causa raiz do bug encontrado no teste manual (uma possível condição de corrida no JavaScript da tela de pagamento) foi descartada do escopo desta entrega, por não ser exigida — optou-se por registrar o defeito em nível de comportamento observado, sem análise técnica da causa.

### Validação

As sugestões da IA foram avaliadas quanto à correspondência com as regras da classe `RecebimentoService`, conferindo cada mensagem de exceção linha a linha contra o código-fonte antes de aceitar a asserção correspondente.

A suíte final de testes unitários foi executada via terminal com Maven (`mvn test -Dtest=RecebimentoServiceTest`), fora do Eclipse, para eliminar variáveis de configuração da IDE, resultando em **6 testes aprovados, 0 falhas, 0 erros e `BUILD SUCCESS`**.

Além do resultado quantitativo, foi avaliado se os cenários cobrem de fato os caminhos de decisão relevantes do método `abrirRecebimento`: são cobertos o caminho feliz, as três validações de negócio (parcela quitada, parcela de outro cliente, cliente inexistente) e o cálculo do valor total, este último verificado via `ArgumentCaptor` sobre o objeto salvo — e não apenas pela ausência de erro na execução. Reconhece-se como limitação que os métodos `receber()` e `remover()` da mesma classe ficaram fora do escopo desta entrega, não fazendo parte da cobertura atual.

No teste manual, o caso `PDVQA: PDV - QA` foi executado na aplicação rodando localmente via Docker Compose. Os três primeiros passos (criação de pedido e abertura do modal de pagamento) passaram; os dois últimos falharam, pois o campo "Titulo" não é preenchido com nenhuma opção, impedindo a conclusão do pagamento e gerando um erro não tratado. O caso foi registrado como **Falhado** no TestLink, e o defeito foi documentado na Issue #9 do repositório.

Assim, as propostas da IA foram analisadas, questionadas e ajustadas conforme os resultados observados, sem considerar a aprovação dos testes unitários como comprovação de cobertura completa ou ausência de defeitos — o próprio teste manual, aliás, evidenciou um defeito que os testes unitários (isolados do front-end) não seriam capazes de capturar.

---

## Registro 5 - Testes unitários da UsuarioService

- **Responsável:** Paulo Carrano
- **Atividade:** Projetar casos de testes unitários (classe `UsuarioService`)
- **Ferramenta:** Claude (Anthropic)
- **Prompt/instrução:** Os 5 testes unitários (`UsuarioServiceTest`, casos CT01–CT05, cobrindo `cadastrar` novo, `cadastrar` atualização, `lista`, `addGrupo` e `buscaUsuario`) foram feitos pelo integrante. A IA foi usada apenas como auxílio e para revisar o resultado após a primeira execução, que apresentou uma falha.
- **Resultado:** Na primeira execução (`mvn -Dtest=UsuarioServiceTest test`), o caso CT01 (`deveCadastrarUsuarioComSucesso`) falhou: esperava "Usuário salvo com sucesso" mas obteve "Usuário atualizado com sucesso". A IA ajudou a diagnosticar a causa: o método `getCodigo()` do mock `Usuario` não havia sido estubado, e o Mockito retorna `0L` (não `null`) por padrão para tipos numéricos encaixotados (`Long`), diferente do que ocorre com `String` ou objetos. Como `UsuarioService.cadastrar()` decide o fluxo com `usuario.getCodigo() == null`, o teste caiu no caminho de atualização em vez do de novo cadastro.
- **Decisão:** Corrigida a estubagem, adicionando `when(usuario.getCodigo()).thenReturn(null);` ao caso CT01, para representar explicitamente um usuário novo (sem código). Nenhuma outra alteração foi feita nos testes.
- **Validação:** Reexecutado `mvn -Dtest=UsuarioServiceTest test` após a correção — os 5 testes passaram (`Tests run: 5, Failures: 0, Errors: 0`).

## Registro 6 - Achados de inspeção de código (leitura de UsuarioService)

- **Responsável:** Paulo Carrano
- **Atividade:** Identificação preliminar de problemas de qualidade
- **Ferramenta:** Claude (Anthropic)
- **Prompt/instrução:** Pedido de leitura crítica do código de `UsuarioService` em busca de problemas.
- **Resultado:** A IA apontou 3 problemas potenciais só pela leitura do código:
  1. Re-criptografia (BCrypt) da senha em toda atualização de usuário, mesmo quando a senha não é alterada.
  2. Comparação de `Long` com `==` (em vez de `.equals()`) em `removeGrupo`.
  3. A classe `GrupoUsuario` não sobrescreve `equals()`/`hashCode()`, afetando o `contains()` usado em `addGrupo`.
- **Decisão:** Achados registrados para inclusão no relatório de inspeção/qualidade da Entrega 2, não impactam os testes elaborados.
- **Validação:** Ainda não validado com testes específicos; fica como ponto de atenção para a etapa estrutural/de defeitos.

## Registro 7 - Casos de teste manuais (funcionalidade "Cadastro de Usuário")

- **Responsável:** Paulo Carrano
- **Atividade:** Projetar e executar casos de teste manuais (funcionalidade individual)
- **Ferramenta:** Claude (Anthropic)
- **Prompt/instrução:** Solicitado apoio para desenhar casos de teste manuais para a funcionalidade de cadastro de usuário, com base nos caminhos de decisão do método `cadastrar()`, e para depurar erros encontrados durante a execução no ambiente local (Docker).
- **Resultado:** Foram desenhados 3 casos de teste manuais:
  - **CT-USU-01** — Cadastro de usuário com sucesso
  - **CT-USU-02** — Cadastro com "user" já existente
  - **CT-USU-03** — Cadastro de pessoa já vinculada a outro usuário

  Durante a execução do CT-USU-01, foi encontrado um erro 500 real no cadastro de **pessoa** (pré-requisito do teste). Com apoio da IA, a causa raiz foi investigada (inspeção de logs do Docker, DevTools do navegador e leitura de `PessoaService.java`) e identificada: o campo "Número" do endereço aceita entrada maior do que o limite da coluna no banco (`VARCHAR(6)`), causando truncamento de dados; o erro real fica mascarado porque `PessoaService.cadastrar()` captura a exceção genericamente e retorna sempre a mesma mensagem ("chame o suporte"), sem repassar a causa.

- **Decisão:** Os 3 casos de teste foram executados manualmente após contornar o bug (preenchendo o campo "Número" com até 6 caracteres). Uma alteração temporária de depuração (`e.printStackTrace()`) foi adicionada a `PessoaService.java` para expor a exceção real durante a investigação. O bug do campo "Número" foi registrado como issue no GitHub (ver README).
- **Validação:** Os 3 casos de teste foram executados na aplicação rodando localmente via Docker Compose (`http://localhost:8080`), com os seguintes resultados: CT-USU-01 (Passou), CT-USU-02 (Passou), CT-USU-03 (Passou). Evidências: mensagens de retorno da aplicação conferidas na tela após cada execução.

---

## Registro 8 — Revisar os testes e explorar outros métodos de `NotaFiscalServiceTest.java`

- **Responsável:** Rafael Valverde Teixeira
- **Atividade:** Revisar os testes implementados sobre os métodos lista(), busca(...), totalNotaFiscalEmitidas() e geraDV(...) e identificar outros possíveis métodos de serem testados de forma unitária e simples.
- **Ferramenta:** ChatGPT 5.5 potência média
- **Prompt/instrução** Analise os testes unitários realizados sobre os métodos lista(), busca(...), totalNotaFiscalEmitidas() e geraDV(...) de NotaFiscalService.java em NotaFiscalServiceTest.java e proponha outros métodos da classe que também podem ser testados e diga qual é o nível de complexidade dos testes.
- **Resultado** Não foram identificados problemas nos testes já implementados e foram identificados os métodos cadastrar(...), tendo complexidade simples, e os métodos como salvaXML, removeXML e emitir com complexidade mais alta.
- **Decisão** Implementar, com a ajuda da IA, os testes no método cadastrar com complexidade simples e deixar as classes com complexidade maior para eventuais partes futuras do trabalho.
- **Validação** Os testes criados pela IA foram concisos e satisfatórios, não apresentando erros e também não sendo muito complexos, rodando os testes na IDE Eclipse houve o seguinte resultado: `Run: 11/11, Errors: 0, Failures: 0`

---

## Registro 9 - Ampliação dos testes unitários, cobertura, mutação e Sonar da UsuarioService (Entrega 2)

- **Responsável:** Paulo Carrano
- **Atividade:** Ampliar os testes unitários da `UsuarioService`, medir a cobertura (JaCoCo) e o escore de mutação (PIT), executar o SonarQube e corrigir a issue encontrada
- **Ferramenta:** Claude Code (Anthropic), modelo Claude Opus 5.5
- **Prompt/instrução:** Foram fornecidos à IA o guia da Entrega 2, o PDF com as instruções da professora e o Plano de Teste do grupo. Foi solicitado ampliar os testes de forma a cobrir todos os ramos da classe, isolando as dependências com Mockito, e seguir os padrões já usados no repositório. Também foi definido que nenhuma alteração seria commitada sem revisão manual, que mudanças no `pom.xml` não poderiam afetar a configuração da `CartaoLancamentoService` e que correções no código de produção ficariam em commit separado dos testes.
- **Resultado:** A IA propôs 13 novos casos (CT06 a CT18), cobrindo os ramos que os testes da Entrega 1 não alcançavam (Registro 5): duplicidade de username e de pessoa, criptografia da senha, erro na atualização, `addGrupo` com grupo existente e o método `removeGrupo` inteiro. O caso CT17 confirmou o problema da comparação de `Long` com `==` levantado no Registro 6: com códigos acima de 127, o grupo não era removido. Também identificou que o PIT executava 0 testes por falta do `pitest-junit5-plugin` e que, com `-DforkCount=0`, o JaCoCo não registra cobertura.
- **Decisão:**
  - Os testes propostos foram mantidos sem alterações após a revisão. Nos novos casos foram usados objetos reais em vez de mocks, para verificar os valores gravados pelo serviço.
  - O CT17 foi mantido falhando até a correção, como evidência do defeito.
  - O outro problema levantado no Registro 6 para `removeGrupo` (remoção de elementos dentro do `for` com índice) foi descartado, pois só ocorreria com dois grupos de mesmo código na lista, o que o banco não retorna.
  - A IA havia afirmado que a configuração do PIT poderia ser sobrescrita pela linha de comando; isso foi verificado antes de ser aceito.
  - O SonarQube apontou apenas 1 issue. Foi rejeitada a ideia de usar um perfil de regras mais rígido só para gerar mais issues; a correção se limitou à issue apontada (`==` substituído por `.equals()`).
- **Validação:**
  - Antes da correção: 17 testes aprovados e o CT17 falhando. Depois da correção: `Tests run: 18, Failures: 0, Errors: 0`.
  - JaCoCo: branches de 42% (5/12) na Entrega 1 para 100% (12/12). A primeira medição com `-DforkCount=0` apresentou um valor incorreto, o que levou à identificação do problema.
  - PIT: de 44% (8/18) na Entrega 1 para 100% (18/18). Os dois mutantes que sobreviviam na Entrega 1 (remoção da data de cadastro e da senha criptografada) mostraram que linhas cobertas não eram verificadas.
  - O PIT da `CartaoLancamentoService` foi executado com e sem o novo plugin, com o mesmo resultado (95%).
  - SonarQube: 1 issue (`java:S4973`) antes e 0 issues depois, com a complexidade ciclomática 11 confirmada.
  - Versão inicial e versão final dos testes unitários são idênticas.

## Registro 10 - Testes de integração da UsuarioService (Entrega 2)

- **Responsável:** Paulo Carrano
- **Atividade:** Implementar testes de integração da `UsuarioService`
- **Ferramenta:** Claude Code (Anthropic), modelo Claude Opus 5.5
- **Prompt/instrução:** Solicitado à IA implementar os testes de integração seguindo o padrão já adotado no grupo (`CartaoLancamentoServiceIntegrationTest`).
- **Resultado:** A IA apresentou duas abordagens: integrar a `UsuarioService` e a `GrupoUsuarioService` reais, com dublês apenas para os repositórios, ou executar os testes com o Spring e o banco MySQL real. Foram implementados 3 cenários: adição de grupo, adição de grupo já existente e remoção de grupo.
- **Decisão:** Escolhida a primeira abordagem, por seguir o padrão do grupo, não depender do Docker e ser mais simples de explicar. Os métodos `cadastrar`, `lista` e `buscaUsuario` ficaram de fora, pois não utilizam a `GrupoUsuarioService`.
- **Validação:** `Tests run: 3, Failures: 0, Errors: 0`, executado pela IA e depois pelo integrante. Versão inicial e versão final são idênticas.

## Registro 11 - Teste de sistema com Selenium (cadastro de usuário)

- **Responsável:** Paulo Carrano
- **Atividade:** Implementar teste funcional de sistema com Selenium
- **Ferramenta:** Claude Code (Anthropic), modelo Claude Opus 5.5
- **Prompt/instrução:** Solicitado à IA um teste Selenium do cadastro de usuário, reaproveitando a `LoginPage` existente e respeitando as pré-condições conhecidas da Entrega 1: o usuário precisa de uma pessoa ainda não vinculada e o campo "Número" do endereço aceita no máximo 6 caracteres (issue #6).
- **Resultado:** A IA mapeou as telas pelo código-fonte e pela aplicação em execução e criou o teste `UsuarioSeleniumTest` e as classes `PessoaPage` e `UsuarioPage`. O teste cadastra uma pessoa, cadastra o usuário vinculado e confere a mensagem de sucesso e a listagem. Gera dados únicos a cada execução, pois o sistema não aceita CPF nem username repetidos.
- **Decisão:** A primeira versão falhou e foi corrigida em duas etapas:
  1. O teste abria a tela de pessoa antes de o login terminar, voltando à tela de login. A primeira tentativa da IA (esperar o formulário carregar) não resolveu; com uma pausa temporária de diagnóstico, foi identificada a causa, e a solução adotada foi aguardar a URL sair de `/login`, sem alterar a `LoginPage` existente.
  2. O cadastro de pessoa falhou com `Unparseable date: ""`, pois a data de nascimento, opcional na tela, é obrigatória no servidor. O teste passou a preencher a data.

  Atualizar o Selenium para a versão 4 (que baixa o ChromeDriver automaticamente) foi descartado, por alterar a configuração usada pelos testes da Maria. O teste não funcional, opcional, não foi implementado.
- **Validação:** Executado 3 vezes seguidas com dados novos: `Tests run: 1, Failures: 0, Errors: 0`. Ao consultar o banco, verificou-se que a data `01/01/1990` foi gravada como `0013-06-06`: o datepicker e o servidor usam formatos de data diferentes. O defeito foi registrado na issue #23. As versões iniciais dos arquivos alterados estão em `docs/ai/paulo-entrega2/`.
