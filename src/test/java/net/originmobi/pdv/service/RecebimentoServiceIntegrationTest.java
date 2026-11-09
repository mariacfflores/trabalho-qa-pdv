package net.originmobi.pdv.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import net.originmobi.pdv.controller.TituloService;
import net.originmobi.pdv.enumerado.caixa.EstiloLancamento;
import net.originmobi.pdv.enumerado.caixa.TipoLancamento;
import net.originmobi.pdv.model.Caixa;
import net.originmobi.pdv.model.CaixaLancamento;
import net.originmobi.pdv.model.Parcela;
import net.originmobi.pdv.model.Recebimento;
import net.originmobi.pdv.model.Titulo;
import net.originmobi.pdv.model.TituloTipo;
import net.originmobi.pdv.repository.CaixaLancamentoRepository;
import net.originmobi.pdv.repository.ParcelaRepository;
import net.originmobi.pdv.repository.RecebimentoParcelaRepository;
import net.originmobi.pdv.repository.RecebimentoRepository;
import net.originmobi.pdv.service.cartao.CartaoLancamentoService;

class RecebimentoServiceIntegrationTest {

	// Unidades reais que serao integradas
	private RecebimentoService recebimentoService;
	private RecebimentoParcelaService recebimentoParcelaService;
	private ParcelaService parcelaService;
	private CaixaLancamentoService caixaLancamentoService;

	// Dubles apenas para o acesso ao banco
	private RecebimentoRepository recebimentoRepository;
	private RecebimentoParcelaRepository recebimentoParcelaRepository;
	private ParcelaRepository parcelaRepository;
	private CaixaLancamentoRepository caixaLancamentoRepository;

	// Dependencias fora do escopo desta integracao
	private TituloService tituloService;
	private CaixaService caixaService;
	private UsuarioService usuarioService;
	private CartaoLancamentoService cartaoLancamentoService;

	@BeforeEach
	void setUp() {
		recebimentoService = new RecebimentoService();
		recebimentoParcelaService = new RecebimentoParcelaService();
		parcelaService = new ParcelaService();
		caixaLancamentoService = new CaixaLancamentoService();

		recebimentoRepository = mock(RecebimentoRepository.class);
		recebimentoParcelaRepository = mock(RecebimentoParcelaRepository.class);
		parcelaRepository = mock(ParcelaRepository.class);
		caixaLancamentoRepository = mock(CaixaLancamentoRepository.class);

		tituloService = mock(TituloService.class);
		caixaService = mock(CaixaService.class);
		usuarioService = mock(UsuarioService.class);
		cartaoLancamentoService = mock(CartaoLancamentoService.class);

		// RecebimentoService -> services reais
		ReflectionTestUtils.setField(recebimentoService, "recebimentos", recebimentoRepository);
		ReflectionTestUtils.setField(recebimentoService, "receParcelas", recebimentoParcelaService);
		ReflectionTestUtils.setField(recebimentoService, "parcelas", parcelaService);
		ReflectionTestUtils.setField(recebimentoService, "lancamentos", caixaLancamentoService);
		ReflectionTestUtils.setField(recebimentoService, "titulos", tituloService);
		ReflectionTestUtils.setField(recebimentoService, "caixas", caixaService);
		ReflectionTestUtils.setField(recebimentoService, "usuarios", usuarioService);
		ReflectionTestUtils.setField(recebimentoService, "cartaoLancamentos", cartaoLancamentoService);

		// Services reais -> repositories simulados
		ReflectionTestUtils.setField(recebimentoParcelaService, "recebimentoparcelas", recebimentoParcelaRepository);
		ReflectionTestUtils.setField(parcelaService, "parcelas", parcelaRepository);
		ReflectionTestUtils.setField(caixaLancamentoService, "caixaLancamento", caixaLancamentoRepository);

		// O receber usa o usuario logado (classe Aplicacao)
		SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("gerente", "123"));

		when(tituloService.busca(5L)).thenReturn(Optional.of(novoTitulo("DIN")));
		when(caixaService.caixaAberto()).thenReturn(Optional.of(new Caixa()));
	}

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	private Titulo novoTitulo(String sigla) {
		TituloTipo tipo = new TituloTipo();
		tipo.setSigla(sigla);

		Titulo titulo = new Titulo();
		titulo.setTipo(tipo);
		return titulo;
	}

	private Recebimento novoRecebimento(Double valorTotal) {
		Recebimento recebimento = new Recebimento();
		recebimento.setValor_total(valorTotal);
		return recebimento;
	}

	// Parcela em aberto, sem nada recebido ainda
	private Parcela novaParcela(Long codigo, Double valor) {
		Parcela parcela = new Parcela();
		parcela.setCodigo(codigo);
		parcela.setValor_total(valor);
		parcela.setValor_restante(valor);
		parcela.setValor_recebido(0.0);
		parcela.setValor_acrescimo(0.0);
		parcela.setValor_desconto(0.0);
		parcela.setQuitado(0);
		return parcela;
	}

	// Prepara o banco simulado com o recebimento e suas parcelas
	private void prepararRecebimento(Double valorTotal, Parcela... parcelas) {
		List<Parcela> lista = new ArrayList<>();
		for (Parcela parcela : parcelas) {
			lista.add(parcela);
			when(parcelaRepository.findById(parcela.getCodigo())).thenReturn(Optional.of(parcela));
		}

		when(recebimentoRepository.findById(1L)).thenReturn(Optional.of(novoRecebimento(valorTotal)));
		when(recebimentoParcelaRepository.parcelasDoRecebimento(1L)).thenReturn(lista);
	}

	// CT-INT-01 - Receber o valor total quita a parcela e lanca entrada no caixa

	@Test
	void deveQuitarParcelaELancarEntradaNoCaixa() {
		prepararRecebimento(100.0, novaParcela(10L, 100.0));

		String resultado = recebimentoService.receber(1L, 100.0, 0.0, 0.0, 5L);

		assertEquals("Recebimento realizado com sucesso", resultado);

		// ParcelaService real calculou: recebido 100, restante 0, quitado 1
		verify(parcelaRepository).receber(eq(0.0), eq(0.0), eq(100.0), eq(0.0), eq(1), any(), eq(10L));

		// CaixaLancamentoService real montou e salvou o lancamento
		ArgumentCaptor<CaixaLancamento> captor = ArgumentCaptor.forClass(CaixaLancamento.class);
		verify(caixaLancamentoRepository).save(captor.capture());

		CaixaLancamento lancamento = captor.getValue();
		assertEquals(100.0, lancamento.getValor());
		assertEquals(EstiloLancamento.ENTRADA, lancamento.getEstilo());
		assertEquals(TipoLancamento.RECEBIMENTO, lancamento.getTipo());

		verify(recebimentoRepository).save(any(Recebimento.class));
	}

	// CT-INT-02 - Pagamento parcial deixa a parcela em aberto

	@Test
	void deveDeixarParcelaEmAbertoNoPagamentoParcial() {
		prepararRecebimento(100.0, novaParcela(10L, 100.0));

		recebimentoService.receber(1L, 30.0, 0.0, 0.0, 5L);

		// recebido 30, restante 70, quitado 0
		verify(parcelaRepository).receber(eq(0.0), eq(0.0), eq(30.0), eq(70.0), eq(0), any(), eq(10L));
	}

	// CT-INT-03 - Valor recebido e rateado entre duas parcelas

	@Test
	void deveQuitarDuasParcelas() {
		prepararRecebimento(110.0, novaParcela(10L, 50.0), novaParcela(11L, 60.0));

		recebimentoService.receber(1L, 110.0, 0.0, 0.0, 5L);

		verify(parcelaRepository).receber(eq(0.0), eq(0.0), eq(50.0), eq(0.0), eq(1), any(), eq(10L));
		verify(parcelaRepository).receber(eq(0.0), eq(0.0), eq(60.0), eq(0.0), eq(1), any(), eq(11L));
	}

	// CT-INT-04 - Falha no banco da parcela e propagada sem fechar o recebimento

	@Test
	void devePropagarFalhaDaParcelaSemSalvarRecebimento() {
		prepararRecebimento(100.0, novaParcela(10L, 100.0));

		doThrow(new RuntimeException("erro no banco")).when(parcelaRepository)
				.receber(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyInt(), any(), anyLong());

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			recebimentoService.receber(1L, 100.0, 0.0, 0.0, 5L);
		});

		assertEquals("Ocorreu um erro ao realizar o recebimento, chame o suporte", exception.getMessage());

		verify(caixaLancamentoRepository, never()).save(any());
		verify(recebimentoRepository, never()).save(any());
	}

}
