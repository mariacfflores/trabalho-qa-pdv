# Registro de Uso de Inteligência Artificial

Este documento registra as interações com ferramentas de Inteligência Artificial Generativa que contribuíram de forma relevante para os artefatos do trabalho.

A IA foi utilizada como ferramenta de apoio ao planejamento, implementação e revisão dos testes. As decisões sobre escopo, regras a serem testadas, critérios de aceitação e adequação às entregas foram realizadas pela responsável pela atividade. As respostas produzidas pela IA foram analisadas e validadas antes de serem incorporadas ao projeto.

Interações relacionadas apenas a configuração de ambiente, execução de ferramentas ou ajustes operacionais não foram incluídas neste registro, por não contribuírem diretamente para a elaboração dos artefatos de teste.

---

## Registro — Planejamento, implementação e validação dos testes de cartões

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

## Registro 4 — Revisão e correção dos testes unitários da `RecebimentoService`

**Responsável:** Caio de Souza Lima

**Atividade:** Revisão, correção e conserto do ambiente de execução dos testes unitários do método `abrirRecebimento` da classe `RecebimentoService`

**Ferramenta:** Claude (Anthropic)

### Prompt/instrução

Os cenários de teste do método `abrirRecebimento` (caminho feliz, parcela já quitada, parcela de outro cliente, cliente inexistente e cálculo do valor total) haviam sido escritos manualmente antes da sessão. Foi solicitado à IA que revisasse o código já escrito, apontando erros de compilação, más práticas com Mockito e lacunas de cobertura, e que ajudasse a diagnosticar por que os testes não executavam no Eclipse.

### Resultado

A IA identificou uma série de problemas em cadeia:

- imports estáticos conflitantes (`org.junit.Assert.assertEquals` e `org.junit.jupiter.api.Assertions.assertEquals` ao mesmo tempo), impedindo a compilação;
- incompatibilidade de versões no `pom.xml`: o `spring-boot-starter-parent 2.0.2.RELEASE` fixava versões antigas de `junit-jupiter-api` (5.1.1), `mockito-core` (2.15.0) e `byte-buddy` (1.7.11) que não eram compatíveis entre si nem com Java 17, causando `NoSuchMethodError` em cadeia;
- o `maven-surefire-plugin` herdado do parent (2.21.0) não sabia executar testes JUnit 5;
- stubs desnecessários (`UnnecessaryStubbingException`) em métodos mockados cujo retorno não era usado no cenário testado;
- no teste `deveCalcularValorTotalDasParcelas`, a chamada ao método de produção era feita sem nenhuma asserção sobre o valor calculado, não provando de fato o comportamento pretendido;
- os testes `deveRecusarParcelaDeOutroCliente` e `deveRecusarClienteInexistente` verificavam apenas o tipo da exceção lançada (`RuntimeException`), sem checar a mensagem nem confirmar que `recebimentos.save(...)` não havia sido chamado — o que poderia mascarar um `NullPointerException` como se fosse a validação de negócio esperada;
- ausência de um cenário cobrindo o array de parcelas vazio.

### Decisão

Foram aceitas e aplicadas as correções de ambiente (`pom.xml`: fixação explícita de `maven-surefire-plugin` 2.22.2, `junit-jupiter-api/params/engine` 5.8.2, `mockito-core`/`mockito-junit-jupiter` 3.12.4 e `byte-buddy`/`byte-buddy-agent` 1.11.13) e a remoção do import duplicado, por serem correções técnicas objetivas de compatibilidade, sem impacto na lógica dos testes.

Quanto ao conteúdo dos testes, foram aceitas as sugestões de reforço:

- adição de `assertEquals` sobre a mensagem da exceção e `verify(recebimentos, never()).save(any())` em `deveRecusarParcelaDeOutroCliente` e `deveRecusarClienteInexistente`;
- adição de `ArgumentCaptor<Recebimento>` em `deveCalcularValorTotalDasParcelas` para verificar `getValor_total() == 110.0`, já que o método não retorna esse valor diretamente;
- criação do teste `deveCriarRecebimentoComArrayDeParcelasVazio`, documentando o comportamento real do sistema (cria um recebimento com valor total `0.0` sem lançar exceção).

A sugestão de remover a duplicidade entre `deveCriarRecebimentoComParcelasValidas` e `deveCalcularValorTotalDasParcelas` (por testarem o mesmo caminho de código) foi discutida, mas optou-se por manter os dois testes separados nesta entrega, por já estarem implementados e cobrindo focos de asserção diferentes.

### Validação

Após as correções, a suíte foi executada via terminal com Maven (`mvn test -Dtest=RecebimentoServiceTest`), fora do Eclipse, para eliminar variáveis de configuração da IDE:

```text
Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Cada correção sugerida pela IA foi conferida manualmente contra o código-fonte de `RecebimentoService.java` antes de ser aceita — por exemplo, a mensagem de exceção `"A parcela " + parcela.getCodigo() + " não pertence ao cliente selecionado"` foi conferida linha a linha antes de ser usada na asserção do teste `deveRecusarParcelaDeOutroCliente`.

A versão final está implementada em:

`src/test/java/net/originmobi/pdv/service/RecebimentoServiceTest.java`

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
