package net.originmobi.pdv.service.notafiscal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.mockito.InjectMocks;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import net.originmobi.pdv.enumerado.notafiscal.NotaFiscalTipo;
import net.originmobi.pdv.model.Empresa;
import net.originmobi.pdv.model.EmpresaParametro;
import net.originmobi.pdv.model.NotaFiscal;
import net.originmobi.pdv.model.NotaFiscalTotais;
import net.originmobi.pdv.model.Pessoa;
import net.originmobi.pdv.repository.notafiscal.NotaFiscalRepository;
import net.originmobi.pdv.service.EmpresaService;
import net.originmobi.pdv.service.PessoaService;
import net.originmobi.pdv.xml.nfe.GeraXmlNfe;

@ExtendWith(MockitoExtension.class)
public class NotaFiscalServiceTest {

	@Mock
	private NotaFiscalRepository notasFiscais;

	@Mock
	private EmpresaService empresas;

	@Mock
	private NotaFiscalTotaisServer notaTotais;

	@Mock
	private PessoaService pessoas;

	@Mock
	private RecursosXmlNfe recursosXml;

	@Mock
	private GeraXmlNfe gerador;

	@Mock
	private PrintWriter escritor;

	@Mock
	private File arquivo;

	@InjectMocks
	private NotaFiscalService notaFiscalService;

	@DisplayName("Teste que o método deve listar notas fiscais")
	@Test
	void deveListarNotasFiscais() {
		NotaFiscal nota1 = new NotaFiscal();
		NotaFiscal nota2 = new NotaFiscal();

		when(notasFiscais.findAll()).thenReturn(Arrays.asList(nota1, nota2));

		List<NotaFiscal> resultado = notaFiscalService.lista();

		assertEquals(2, resultado.size());
		assertEquals(nota1, resultado.get(0));
		assertEquals(nota2, resultado.get(1));
		verify(notasFiscais).findAll();
	}

	@DisplayName("Teste que tem que retornar lista vazia quando não existirem notas fiscais")
	@Test
	void deveRetornarListaVaziaQuandoNaoExistiremNotasFiscais() {
		when(notasFiscais.findAll()).thenReturn(Collections.emptyList());

		List<NotaFiscal> resultado = notaFiscalService.lista();

		assertEquals(0, resultado.size());
		verify(notasFiscais).findAll();
	}

	@DisplayName("Teste de buscar nota fiscal por código")
	@Test
	void deveBuscarNotaFiscalPorCodigo() {
		NotaFiscal nota = new NotaFiscal();
		nota.setCodigo(1L);

		when(notasFiscais.findById(1L)).thenReturn(Optional.of(nota));

		Optional<NotaFiscal> resultado = notaFiscalService.busca(1L);

		assertEquals(true, resultado.isPresent());
		assertEquals(1L, resultado.get().getCodigo());
		verify(notasFiscais).findById(1L);
	}

	@DisplayName("Teste de retornar vazio quando a nota fiscal não existir")
	@Test
	void deveRetornarVazioQuandoNotaFiscalNaoExistir() {
		when(notasFiscais.findById(1L)).thenReturn(Optional.empty());

		Optional<NotaFiscal> resultado = notaFiscalService.busca(1L);

		assertEquals(false, resultado.isPresent());
		verify(notasFiscais).findById(1L);
	}

	@DisplayName("Teste de retornar total de notas fiscais emitidas")
	@Test
	void deveRetornarTotalDeNotasFiscaisEmitidas() {
		when(notasFiscais.totalNotaFiscalEmitidas()).thenReturn(5);

		int resultado = notaFiscalService.totalNotaFiscalEmitidas();

		assertEquals(5, resultado);
		verify(notasFiscais).totalNotaFiscalEmitidas();
	}

	@DisplayName("Teste de retornar zero quando o código for inválido")
	@Test
	void deveRetornarZeroQuandoCodigoForInvalido() {
		Integer resultado = notaFiscalService.geraDV(null);

		assertEquals(0, resultado);
	}

	@DisplayName("Teste que deve lançar erro quando não existir empresa cadastrada")
	@Test
	void deveLancarErroQuandoNaoExistirEmpresaCadastrada() {
		when(empresas.verificaEmpresaCadastrada()).thenReturn(Optional.empty());

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			notaFiscalService.cadastrar(1L, "Venda", NotaFiscalTipo.SAIDA);
		});

		assertEquals("Nenhuma empresa cadastrada, verifique", exception.getMessage());
		verify(notaTotais, never()).cadastro(any(NotaFiscalTotais.class));
		verify(notasFiscais, never()).save(any(NotaFiscal.class));
	}

	@DisplayName("Teste que deve lançar erro quando não existir destinatário")
	@Test
	void deveLancarErroQuandoNaoExistirDestinatario() {
		Empresa empresa = new Empresa();
		when(empresas.verificaEmpresaCadastrada()).thenReturn(Optional.of(empresa));
		when(pessoas.buscaPessoa(1L)).thenReturn(Optional.empty());

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			notaFiscalService.cadastrar(1L, "Venda", NotaFiscalTipo.SAIDA);
		});

		assertEquals("Favor, selecione o destinatário", exception.getMessage());
		verify(notaTotais, never()).cadastro(any(NotaFiscalTotais.class));
		verify(notasFiscais, never()).save(any(NotaFiscal.class));
	}

	@DisplayName("Teste de lançar erro quando a série da NFe for zero")
	@Test
	void deveLancarErroQuandoSerieNfeForZero() {
		EmpresaParametro parametro = new EmpresaParametro();
		parametro.setSerie_nfe(0);

		Empresa empresa = new Empresa();
		empresa.setParametro(parametro);

		Pessoa pessoa = new Pessoa();

		when(empresas.verificaEmpresaCadastrada()).thenReturn(Optional.of(empresa));
		when(pessoas.buscaPessoa(1L)).thenReturn(Optional.of(pessoa));

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			notaFiscalService.cadastrar(1L, "Venda", NotaFiscalTipo.SAIDA);
		});

		assertEquals("Não existe série cadastrada para o modelo 55, verifique", exception.getMessage());
		verify(notaTotais, never()).cadastro(any(NotaFiscalTotais.class));
		verify(notasFiscais, never()).save(any(NotaFiscal.class));
	}

	@DisplayName("Calcula o dígito verificador nos limites dos pesos e restos")
	@ParameterizedTest(name = "Código {0} deve produzir DV {1}")
	@CsvSource({"0, 0", "6, 0", "12345678, 9", "123456789, 7"})
	void deveCalcularDigitoNosLimitesDosPesos(String codigo, int esperado) {
		// Somas independentes: 0, 12, 156 e 202; pesos aplicados da direita para a esquerda.
		assertEquals(Integer.valueOf(esperado), notaFiscalService.geraDV(codigo));
	}

	@DisplayName("Cadastra a nota com os dados informados e os totais zerados")
	@Test
	void deveCadastrarNotaComDadosETotaisIniciais() {
		Empresa empresa = prepararCadastroValido();
		Pessoa destinatario = pessoas.buscaPessoa(1L).get();
		when(notasFiscais.buscaUltimaNota(3)).thenReturn(42L);
		when(notasFiscais.save(any(NotaFiscal.class))).thenAnswer(invocacao -> {
			NotaFiscal nota = invocacao.getArgument(0);
			nota.setCodigo(100L);
			return nota;
		});

		LocalDate antes = LocalDate.now();
		String resultado = notaFiscalService.cadastrar(1L, "Venda", NotaFiscalTipo.SAIDA);
		LocalDate depois = LocalDate.now();

		assertEquals("100", resultado);
		ArgumentCaptor<NotaFiscal> captor = ArgumentCaptor.forClass(NotaFiscal.class);
		verify(notasFiscais).save(captor.capture());
		NotaFiscal nota = captor.getValue();
		assertEquals(Long.valueOf(42), nota.getNumero());
		assertEquals(55, nota.getModelo());
		assertEquals(3, nota.getSerie());
		assertEquals(NotaFiscalTipo.SAIDA, nota.getTipo());
		assertEquals("Venda", nota.getNatureza_operacao());
		assertSame(empresa, nota.getEmissor());
		assertSame(destinatario, nota.getDestinatario());
		assertEquals(1, nota.getTipo_emissao());
		assertEquals(2, nota.getTipo_ambiente());
		assertEquals("0.0.1-beta", nota.getVerProc());
		assertEquals(Long.valueOf(4), nota.getFreteTipo().getCodigo());
		assertEquals(Long.valueOf(1), nota.getFinalidade().getCodigo());
		// Aceita a virada de dia sem alterar ou simular o relógio global.
		LocalDate cadastro = nota.getData_cadastro().toLocalDate();
		assertTrue(!cadastro.isBefore(antes) && !cadastro.isAfter(depois));
		ArgumentCaptor<NotaFiscalTotais> totaisCaptor = ArgumentCaptor.forClass(NotaFiscalTotais.class);
		verify(notaTotais).cadastro(totaisCaptor.capture());
		NotaFiscalTotais totais = totaisCaptor.getValue();
		assertSame(totais, nota.getTotais());
		assertEquals(Collections.nCopies(13, 0.0), Arrays.asList(totais.getV_bc(), totais.getV_icms(),
				totais.getV_st(), totais.getV_prod(), totais.getV_frete(), totais.getV_seg(),
				totais.getV_desc(), totais.getV_ii(), totais.getV_ipi(), totais.getV_pis(),
				totais.getV_cofins(), totais.getV_outros(), totais.getV_nf()));
		verify(notasFiscais).buscaUltimaNota(3);
	}

	@DisplayName("Interrompe o cadastro quando a persistência dos totais falha")
	@Test
	void deveInterromperCadastroQuandoTotaisFalharem() {
		prepararCadastroValido();
		when(notaTotais.cadastro(any(NotaFiscalTotais.class))).thenThrow(new RuntimeException("Falha simulada"));
		verificarErroDeCadastro();
		verify(notasFiscais, never()).buscaUltimaNota(anyInt());
		verify(notasFiscais, never()).save(any(NotaFiscal.class));
	}

	@DisplayName("Informa o erro quando a persistência da nota falha")
	@Test
	void deveInformarErroQuandoPersistenciaDaNotaFalhar() {
		prepararCadastroValido();
		when(notasFiscais.buscaUltimaNota(3)).thenReturn(42L);
		when(notasFiscais.save(any(NotaFiscal.class))).thenThrow(new RuntimeException("Falha simulada"));
		verificarErroDeCadastro();
		verify(notaTotais).cadastro(any(NotaFiscalTotais.class));
		verify(notasFiscais).save(any(NotaFiscal.class));
	}

	@DisplayName("Emite a nota e atribui a chave antes de persistir")
	@Test
	void deveEmitirNotaComChaveGerada() {
		NotaFiscal nota = new NotaFiscal();
		when(recursosXml.criarGerador()).thenReturn(gerador);
		when(gerador.gerarXML(nota)).thenReturn("chave-nova");
		doAnswer(invocacao -> {
			assertSame(nota, invocacao.getArgument(0));
			assertEquals("chave-nova", nota.getChave_acesso());
			return nota;
		}).when(notasFiscais).save(nota);

		notaFiscalService.emitir(nota);

		assertEquals("chave-nova", nota.getChave_acesso());
		InOrder ordem = inOrder(gerador, notasFiscais);
		ordem.verify(gerador).gerarXML(nota);
		ordem.verify(notasFiscais).save(nota);
	}

	@DisplayName("Não persiste a nota quando a geração de XML falha")
	@Test
	void devePropagarFalhaDoGeradorSemSalvar() {
		NotaFiscal nota = new NotaFiscal();
		nota.setChave_acesso("chave-antiga");
		RuntimeException falha = new RuntimeException("Falha na geração");
		when(recursosXml.criarGerador()).thenReturn(gerador);
		when(gerador.gerarXML(nota)).thenThrow(falha);
		assertSame(falha, assertThrows(RuntimeException.class, () -> notaFiscalService.emitir(nota)));
		assertEquals("chave-antiga", nota.getChave_acesso());
		verifyNoInteractions(notasFiscais);
	}

	@DisplayName("Grava o conteúdo XML no destino esperado e fecha o escritor")
	@Test
	void deveGravarXmlEFecharEscritor() throws IOException {
		when(recursosXml.contexto()).thenReturn("projeto");
		String destino = Paths.get("projeto/src/main/resources/xmlNfe/").toString() + "/chave.xml";
		when(recursosXml.abrirEscritor(destino)).thenReturn(escritor);
		notaFiscalService.salvaXML("<nfe/>", "chave");
		InOrder ordem = inOrder(recursosXml, escritor);
		ordem.verify(recursosXml).abrirEscritor(destino);
		ordem.verify(escritor).write("<nfe/>");
		ordem.verify(escritor).close();
	}

	@DisplayName("Trata a falha de abertura do XML sem usar um escritor")
	@ResourceLock("java.lang.System.err")
	@Test
	void deveTratarFalhaAoAbrirXml() throws IOException {
		when(recursosXml.contexto()).thenReturn("projeto");
		String destino = Paths.get("projeto/src/main/resources/xmlNfe/").toString() + "/chave.xml";
		when(recursosXml.abrirEscritor(destino)).thenThrow(new IOException("Abertura indisponível"));
		// O contrato atual não propaga a IOException: o diagnóstico é sua única sinalização.
		ByteArrayOutputStream diagnostico = new ByteArrayOutputStream();
		PrintStream erroOriginal = System.err;
		try (PrintStream captura = new PrintStream(diagnostico, true, "UTF-8")) {
			System.setErr(captura);
			assertDoesNotThrow(() -> notaFiscalService.salvaXML("<nfe/>", "chave"));
		} finally {
			System.setErr(erroOriginal);
		}
		assertTrue(diagnostico.toString("UTF-8").contains("java.io.IOException: Abertura indisponível"));
		verify(recursosXml).abrirEscritor(destino);
		verifyNoInteractions(escritor);
	}

	@DisplayName("Mantém o destino alternativo de gravação quando a consulta do contexto falha")
	@Test
	void deveGravarComContextoVazioQuandoConsultaFalhar() throws IOException {
		when(recursosXml.contexto()).thenThrow(new IOException("Contexto indisponível"));
		String destino = Paths.get("/src/main/resources/xmlNfe/").toString() + "/chave.xml";
		when(recursosXml.abrirEscritor(destino)).thenReturn(escritor);
		notaFiscalService.salvaXML("<nfe/>", "chave");
		verify(recursosXml).abrirEscritor(destino);
		verify(escritor).write("<nfe/>");
		verify(escritor).close();
	}

	@DisplayName("Exclui o XML somente quando o arquivo existe")
	@Test
	void deveExcluirXmlExistente() throws IOException {
		prepararArquivoXml();
		when(arquivo.exists()).thenReturn(true);
		notaFiscalService.removeXml("chave");
		InOrder ordem = inOrder(arquivo);
		ordem.verify(arquivo).exists();
		ordem.verify(arquivo).delete();
	}

	@DisplayName("Não tenta excluir um XML inexistente")
	@Test
	void naoDeveExcluirXmlInexistente() throws IOException {
		prepararArquivoXml();
		when(arquivo.exists()).thenReturn(false);
		notaFiscalService.removeXml("chave");
		verify(arquivo).exists();
		verify(arquivo, never()).delete();
	}

	@DisplayName("Trata a falha ao excluir o XML existente")
	@Test
	void deveTratarFalhaNaExclusaoXml() throws IOException {
		prepararArquivoXml();
		when(arquivo.exists()).thenReturn(true);
		when(arquivo.delete()).thenThrow(new SecurityException("Exclusão negada"));
		assertDoesNotThrow(() -> notaFiscalService.removeXml("chave"));
		verify(arquivo).delete();
	}

	@DisplayName("Mantém o destino alternativo de exclusão quando a consulta do contexto falha")
	@Test
	void deveRemoverComContextoVazioQuandoConsultaFalhar() throws IOException {
		when(recursosXml.contexto()).thenThrow(new IOException("Contexto indisponível"));
		when(recursosXml.arquivo("/src/main/resources/xmlNfe//chave.xml")).thenReturn(arquivo);
		when(arquivo.exists()).thenReturn(true);
		notaFiscalService.removeXml("chave");
		verify(recursosXml).arquivo("/src/main/resources/xmlNfe//chave.xml");
		verify(arquivo).delete();
	}

	private void prepararArquivoXml() throws IOException {
		when(recursosXml.contexto()).thenReturn("projeto");
		when(recursosXml.arquivo("projeto/src/main/resources/xmlNfe//chave.xml")).thenReturn(arquivo);
	}

	private Empresa prepararCadastroValido() {
		EmpresaParametro parametro = new EmpresaParametro();
		parametro.setSerie_nfe(3);
		parametro.setAmbiente(2);
		Empresa empresa = new Empresa();
		empresa.setParametro(parametro);
		when(empresas.verificaEmpresaCadastrada()).thenReturn(Optional.of(empresa));
		when(pessoas.buscaPessoa(1L)).thenReturn(Optional.of(new Pessoa()));
		return empresa;
	}

	private void verificarErroDeCadastro() {
		RuntimeException erro = assertThrows(RuntimeException.class,
				() -> notaFiscalService.cadastrar(1L, "Venda", NotaFiscalTipo.SAIDA));
		assertEquals("Erro ao cadastrar a nota, chame o suporte", erro.getMessage());
	}

}
