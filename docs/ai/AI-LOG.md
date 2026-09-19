# Registro de Uso de Inteligência Artificial

Este documento registra as interações com ferramentas de Inteligência Artificial Generativa que contribuíram de forma relevante para os artefatos do trabalho.

A IA foi utilizada como ferramenta de apoio ao planejamento, implementação e revisão dos testes. As decisões sobre escopo, regras a serem testadas, critérios de aceitação e adequação às entregas foram realizadas pela responsável pela atividade. As respostas produzidas pela IA foram analisadas e validadas antes de serem incorporadas ao projeto.

Interações relacionadas apenas a configuração de ambiente, execução de ferramentas ou ajustes operacionais não foram incluídas neste registro, por não contribuírem diretamente para a elaboração dos artefatos de teste.

---

## Registro 1 — Planejamento dos testes unitários da `CartaoLancamentoService`

**Responsável:** Maria Clara Flores

**Atividade:** Planejamento dos casos de teste unitário da classe `CartaoLancamentoService`

**Ferramenta:** ChatGPT

### Prompt/instrução

Foi fornecido à IA o código-fonte completo da classe `CartaoLancamentoService`.

Antes da implementação dos testes, foram definidas as seguintes diretrizes:

- considerar somente o escopo da Entrega 1;
- analisar as regras presentes nos métodos `lancamento()`, `listar()`, `processar()` e `antecipar()`;
- identificar desvios condicionais e diferentes estados possíveis dos lançamentos de cartão;
- priorizar regras de negócio presentes diretamente na classe;
- utilizar JUnit e Mockito para a posterior implementação dos testes;
- não incluir, neste momento, requisitos de cobertura estrutural, teste de mutação ou isolamento completo das dependências, pois esses pontos fazem parte da evolução prevista para a Entrega 2.

A IA foi utilizada para auxiliar na identificação e organização dos possíveis cenários a partir dessas diretrizes.

### Resultado

Foram identificados cenários relacionados aos quatro métodos da classe.

Para `lancamento()`:

- lançamento de cartão de débito;
- lançamento de cartão de crédito.

Para `listar()`:

- listagem sem filtros;
- listagem com filtros preenchidos.

Para `processar()`:

- tentativa de processar lançamento já processado;
- tentativa de processar lançamento já antecipado;
- processamento válido;
- falhas durante as operações internas de processamento.

Para `antecipar()`:

- tentativa de antecipar lançamento já processado;
- tentativa de antecipar lançamento já antecipado;
- antecipação válida;
- falhas durante as operações internas de antecipação.

### Decisão

Os cenários sugeridos foram comparados com o código-fonte da classe antes da implementação.

Foi decidido utilizar como base os casos que correspondiam diretamente aos comportamentos identificados nos métodos e manter o conjunto inicialmente mais amplo para verificar, na prática, quais cenários poderiam ser executados adequadamente no nível de isolamento utilizado na Entrega 1.

Também foi definido que requisitos específicos da Entrega 2, como aumento de cobertura, testes estruturais, mutação e isolamento mais profundo de dependências, não seriam antecipados apenas para aumentar artificialmente a quantidade de testes nesta etapa.

### Validação

Os cenários foram conferidos diretamente com a implementação original da `CartaoLancamentoService`.

Por exemplo, no método `processar()` foi identificada a seguinte regra:

```java
if (cartaoLancamento.getSituacao().equals(CartaoSituacao.PROCESSADO))
    throw new RuntimeException("Registro já processado");
```

A partir dessa condição foi validada a necessidade de um cenário em que um lançamento com situação `PROCESSADO` fosse submetido novamente ao método `processar()` e a operação fosse rejeitada com a mensagem correspondente.

O mesmo procedimento foi utilizado para conferir os demais cenários propostos.

---

## Registro 2 — Implementação e revisão dos testes unitários da `CartaoLancamentoService`

**Responsável:** Maria Clara Flores

**Atividade:** Implementação, execução e revisão dos testes unitários da classe `CartaoLancamentoService`

**Ferramenta:** ChatGPT

### Prompt/instrução

Após a identificação dos cenários, a IA foi orientada a transformar as regras selecionadas em código de teste.

Foram fornecidas as seguintes diretrizes de implementação:

- utilizar JUnit;
- utilizar Mockito para criação de mocks;
- evitar a inicialização completa do contexto Spring;
- evitar dependência direta do banco de dados;
- utilizar mocks somente para representar as dependências necessárias ao comportamento analisado;
- verificar tanto o resultado das operações quanto as interações relevantes com objetos mockados;
- testar os caminhos de débito e crédito do método `lancamento()`;
- verificar o tratamento de filtros do método `listar()`;
- testar as restrições de estado existentes em `processar()` e `antecipar()`;
- inicialmente incluir também os caminhos completos de sucesso e erro desses métodos para avaliar a viabilidade de execução dentro do escopo da Entrega 1.

A IA foi utilizada principalmente para transformar essas regras e orientações em código Java de teste.

### Resultado

A primeira solução produzida com apoio da IA continha **14 testes unitários**, distribuídos entre os métodos `lancamento()`, `listar()`, `processar()` e `antecipar()`.

A versão inicial contemplava:

- lançamento de cartão de débito;
- lançamento de cartão de crédito;
- listagem sem filtros;
- listagem com filtros preenchidos;
- tentativa de processar cartão já processado;
- tentativa de processar cartão já antecipado;
- processamento realizado com sucesso;
- falha no lançamento no caixa durante o processamento;
- falha ao alterar a situação durante o processamento;
- tentativa de antecipar cartão já processado;
- tentativa de antecipar cartão já antecipado;
- antecipação realizada com sucesso;
- falha no lançamento no caixa durante a antecipação;
- falha ao salvar o lançamento após a antecipação.

A primeira execução apresentou o seguinte resultado:

```text
Tests run: 14
8 testes aprovados
4 testes com falha
2 testes com erro
```

Os seis casos problemáticos eram justamente os cenários que avançavam para partes mais acopladas da aplicação.

Foram identificadas dependências como:

- `Aplicacao`;
- `UsuarioService`;
- `CaixaLancamentoService`;
- usuário atualmente autenticado;
- dados associados ao caixa e ao banco.

Esses caminhos exigiam um nível maior de isolamento das dependências para serem testados adequadamente como testes unitários.

### Decisão

Os resultados da primeira execução foram analisados antes de qualquer alteração na solução.

Foi decidido **não modificar os testes apenas com o objetivo de fazer os 14 casos passarem**.

A análise mostrou que os seis testes problemáticos exigiam justamente um isolamento mais profundo das dependências da classe. Como a Entrega 2 prevê a melhoria dos testes unitários e o isolamento dessas dependências, foi decidido deixar esses cenários para a próxima etapa do trabalho.

Assim, a solução final da Entrega 1 foi reduzida para os **8 testes que representavam corretamente regras da classe e podiam ser executados adequadamente no nível de isolamento atual**:

1. lançamento de cartão de débito;
2. lançamento de cartão de crédito;
3. listagem sem filtros;
4. listagem com filtros preenchidos;
5. tentativa de processar cartão já processado;
6. tentativa de processar cartão já antecipado;
7. tentativa de antecipar cartão já processado;
8. tentativa de antecipar cartão já antecipado.

Foram adiados para a Entrega 2:

- processamento completo com sucesso;
- tratamento de falha no lançamento de caixa durante o processamento;
- tratamento de falha ao alterar a situação durante o processamento;
- antecipação completa com sucesso;
- tratamento de falha no lançamento de caixa durante a antecipação;
- tratamento de falha ao salvar a antecipação.

Dessa forma, a solução inicialmente produzida com auxílio da IA não foi simplesmente aceita. Ela foi executada, analisada e reduzida de acordo com os resultados observados e com o escopo definido para cada entrega.

A versão final está implementada em:

`src/test/java/net/originmobi/pdv/service/cartao/CartaoLancamentoServiceTest.java`

### Validação

Após a revisão, o conjunto final foi executado novamente.

O resultado obtido foi:

```text
Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Além da execução com sucesso, cada teste mantido foi novamente confrontado com o código original da `CartaoLancamentoService` para confirmar que correspondia a uma regra efetivamente implementada na classe.

Por exemplo, os testes relativos aos estados `PROCESSADO` e `ANTECIPADO` foram comparados diretamente com as condições existentes nos métodos `processar()` e `antecipar()`.

O resultado da execução foi preservado como evidência do trabalho.

---

## Registro 3 — Planejamento e execução do teste manual no TestLink

**Responsável:** Maria Clara Flores

**Atividade:** Planejamento, cadastro, preparação da massa de teste e execução de caso de teste manual relacionado à funcionalidade de cartões

**Ferramenta:** ChatGPT

### Prompt/instrução

Após a realização dos testes unitários, foi definida uma funcionalidade relacionada à mesma área do sistema para realização do teste manual.

Foi escolhida a funcionalidade de **antecipação de lançamento de cartão**.

A IA foi orientada a auxiliar na organização do caso de teste no formato necessário para cadastro no TestLink, contemplando:

- objetivo;
- pré-condições;
- ações de cada passo;
- resultado esperado de cada passo;
- resultado esperado final.

A definição da funcionalidade a ser testada e do comportamento que deveria ser validado foi realizada antes do cadastro no TestLink.

Posteriormente, a IA também foi utilizada como apoio durante a preparação da massa de teste necessária para que as pré-condições do caso pudessem ser atendidas no ambiente local.

### Resultado

Foi elaborado o seguinte caso de teste:

**`CT-CART-01 - Antecipar lançamento de cartão com sucesso`**

**Objetivo:**

Verificar se um lançamento de cartão com situação `APROCESSAR` pode ser antecipado corretamente.

**Pré-condições:**

- usuário autenticado no sistema;
- existência de um lançamento de cartão com situação `APROCESSAR`;
- lançamento associado a uma máquina de cartão e banco válidos.

Foram definidos cinco passos:

1. acessar a área de lançamentos de cartão;
2. localizar um lançamento com situação `APROCESSAR`;
3. selecionar a opção de antecipação;
4. confirmar a antecipação;
5. verificar a situação final do lançamento.

O resultado esperado final foi definido como:

- alteração da situação para `ANTECIPADO`;
- conclusão da operação;
- apresentação da confirmação de sucesso pelo sistema.

O caso foi cadastrado no TestLink com o identificador:

`QAPDV-1: CT-CART-01 - Antecipar lançamento de cartão com sucesso`

### Decisão

O cenário foi mantido por representar uma regra de negócio diretamente relacionada à classe `CartaoLancamentoService` utilizada nos testes unitários.

Também foi decidido utilizar esse cenário no TestLink para que a mesma funcionalidade pudesse ser observada em dois níveis diferentes:

- teste unitário das regras presentes na classe;
- teste manual do comportamento disponibilizado pelo sistema.

O caso foi associado:

- ao projeto `PDV - Qualidade e Teste`;
- à suíte `Cartões`;
- ao Plano de Testes `Entrega 1 - PDV`;
- à build `Entrega 1`.

### Preparação da massa de teste

Antes da execução do caso, foi necessário preparar o ambiente para atender às pré-condições definidas no TestLink.

Inicialmente, a área `Gerenciar Cartões` não possuía lançamentos disponíveis e não existia nenhum registro com situação `APROCESSAR`.

Durante a tentativa de gerar uma venda com cartão, também foi identificado que o campo `Título` do pagamento não apresentava nenhuma opção.

A requisição realizada pela aplicação para o endpoint `/venda/titulos` foi verificada por meio da aba `Network` das ferramentas de desenvolvedor do navegador. A requisição retornava status `200 OK`, porém com resposta vazia (`[]`).

A consulta ao banco confirmou que os tipos de título já existiam:

- `DIN` — Dinheiro;
- `CARTDEB` — Cartão Débito;
- `CARTCRED` — Cartão Crédito.

Entretanto, as tabelas de títulos e máquinas de cartão ainda não possuíam registros.

Para preparar a massa de teste, foi realizado o seguinte fluxo pela aplicação:

1. abertura de um banco do tipo `BANCO`;
2. cadastro de uma máquina de cartão associada ao banco;
3. cadastro do título `Cartão Crédito QA`, do tipo `Cartão Crédito`, associado à máquina criada;
4. preparação de um produto com estoque disponível;
5. criação de um pedido;
6. geração da venda;
7. pagamento da venda utilizando o título `Cartão Crédito QA`;
8. geração de um lançamento de cartão com situação `A Processar`.

Durante a preparação do estoque, foi identificado um comportamento anômalo no sistema: o ajuste era apresentado como `Processado` e indicava o novo estoque esperado, porém a quantidade armazenada na tabela `produto_estoque` permanecia igual a zero.

Para permitir a continuidade da preparação da massa de teste, o estoque do produto utilizado foi ajustado diretamente no banco de dados.

Após a conclusão desse fluxo, a tela `Gerenciar Cartões` passou a apresentar um lançamento de cartão do tipo crédito com situação `A Processar`, atendendo às pré-condições necessárias para a execução do `CT-CART-01`.

### Validação

Com as pré-condições atendidas, o teste foi executado manualmente no sistema seguindo os cinco passos cadastrados no TestLink.

Na área `Gerenciar Cartões`, foi localizado o lançamento criado durante a preparação da massa de teste com situação:

`A Processar`

Foi selecionada a opção `Antecipar` e a operação foi confirmada.

Após a execução, o sistema alterou a situação do lançamento para:

`Antecipado`

Dessa forma, o comportamento observado correspondeu ao resultado esperado definido para o caso de teste.

Todos os passos foram registrados no TestLink com o estado:

`Passou`

O resultado final da execução também foi:

`Passou`

O TestLink registrou:

- tipo de execução: Manual;
- testador: `mcfflores`;
- build: `Entrega 1`;
- resultado da execução: `Passou`.

Após a execução, foi gerado o relatório do Plano de Testes pelo próprio TestLink.

A evidência está armazenada no repositório em:

`docs/evidencias/TestLink_AntecLancCartao.pdf`

O relatório contém o caso de teste, pré-condições, passos, resultados esperados, estado de cada passo, responsável pela execução e resultado final.

### Observação adicional

Durante a preparação da massa de teste, foi identificado um possível defeito na funcionalidade de ajuste de estoque, pois o sistema registrou o ajuste como `Processado`, mas não atualizou a quantidade disponível do produto na tabela `produto_estoque`.

Esse comportamento foi identificado durante a preparação do ambiente e não faz parte do escopo do `CT-CART-01`, que tem como objetivo validar exclusivamente a antecipação de um lançamento de cartão.

---

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
