package net.originmobi.pdv.service.notafiscal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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

	@InjectMocks
	private NotaFiscalService notaFiscalService;

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

	@Test
	void deveRetornarListaVaziaQuandoNaoExistiremNotasFiscais() {
		when(notasFiscais.findAll()).thenReturn(Collections.emptyList());

		List<NotaFiscal> resultado = notaFiscalService.lista();

		assertEquals(0, resultado.size());
		verify(notasFiscais).findAll();
	}

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

	@Test
	void deveRetornarVazioQuandoNotaFiscalNaoExistir() {
		when(notasFiscais.findById(1L)).thenReturn(Optional.empty());

		Optional<NotaFiscal> resultado = notaFiscalService.busca(1L);

		assertEquals(false, resultado.isPresent());
		verify(notasFiscais).findById(1L);
	}

	@Test
	void deveRetornarTotalDeNotasFiscaisEmitidas() {
		when(notasFiscais.totalNotaFiscalEmitidas()).thenReturn(5);

		int resultado = notaFiscalService.totalNotaFiscalEmitidas();

		assertEquals(5, resultado);
		verify(notasFiscais).totalNotaFiscalEmitidas();
	}

	@Test
	void deveGerarDigitoVerificador() {
		Integer resultado = notaFiscalService.geraDV("12345");

		assertEquals(5, resultado);
	}

	@Test
	void deveRetornarZeroQuandoRestoForZeroOuUm() {
		Integer resultado = notaFiscalService.geraDV("123456");

		assertEquals(0, resultado);
	}

	@Test
	void deveRetornarZeroQuandoCodigoForInvalido() {
		Integer resultado = notaFiscalService.geraDV(null);

		assertEquals(0, resultado);
	}

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

}
