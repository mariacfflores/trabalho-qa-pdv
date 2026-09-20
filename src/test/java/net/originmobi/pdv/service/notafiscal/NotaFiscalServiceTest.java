package net.originmobi.pdv.service.notafiscal;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import net.originmobi.pdv.model.NotaFiscal;
import net.originmobi.pdv.repository.notafiscal.NotaFiscalRepository;

@ExtendWith(MockitoExtension.class)
public class NotaFiscalServiceTest {

	@Mock
	private NotaFiscalRepository notasFiscais;

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

}
