package net.originmobi.pdv.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import net.originmobi.pdv.controller.TituloService;
import net.originmobi.pdv.model.Caixa;
import net.originmobi.pdv.model.CaixaLancamento;
import net.originmobi.pdv.model.Titulo;
import net.originmobi.pdv.model.TituloTipo;
import net.originmobi.pdv.model.Usuario;
import net.originmobi.pdv.service.cartao.CartaoLancamentoService;

import net.originmobi.pdv.model.Parcela;
import net.originmobi.pdv.model.Pessoa;
import net.originmobi.pdv.model.Receber;
import net.originmobi.pdv.model.Recebimento;
import net.originmobi.pdv.repository.RecebimentoRepository;

@ExtendWith(MockitoExtension.class)
public class RecebimentoServiceTest {

	@Mock
	private ParcelaService parcelas;

	@Mock
	private PessoaService pessoas;

	@Mock
	private RecebimentoRepository recebimentos;

	@Mock
	private TituloService titulos;

	@Mock
	private RecebimentoParcelaService receParcelas;

	@Mock
	private UsuarioService usuarios;

	@Mock
	private CaixaService caixas;

	@Mock
	private CaixaLancamentoService lancamentos;

	@Mock
	private CartaoLancamentoService cartaoLancamentos;

	@InjectMocks
	private RecebimentoService recebimentoService;

	@Test
	void deveCriarRecebimentoComParcelasValidas() {
		Pessoa caio = mock(Pessoa.class);

		when(caio.getCodigo()).thenReturn(10L);

		Receber receber1 = mock(Receber.class);
		Receber receber2 = mock(Receber.class);

		when(receber1.getPessoa()).thenReturn(caio);
		when(receber2.getPessoa()).thenReturn(caio);

		Parcela parcela1 = mock(Parcela.class);
		Parcela parcela2 = mock(Parcela.class);

		// Parcela 1
		when(parcela1.getQuitado()).thenReturn(0);
		when(parcela1.getValor_restante()).thenReturn(50.0);
		when(parcela1.getReceber()).thenReturn(receber1);

		// Parcela 2
		when(parcela2.getQuitado()).thenReturn(0);
		when(parcela2.getValor_restante()).thenReturn(60.0);
		when(parcela2.getReceber()).thenReturn(receber2);

		// Quando o service procurar as parcelas, devolve as que foram criadas
		when(parcelas.busca(1L)).thenReturn(parcela1);
		when(parcelas.busca(2L)).thenReturn(parcela2);

		// Quando o service procurar o cliente, encontra a pessoa
		when(pessoas.buscaPessoa(10L)).thenReturn(Optional.of(caio));

		when(recebimentos.save(any(Recebimento.class))).thenAnswer(invocacao -> {
			Recebimento recebimento = invocacao.getArgument(0);
			recebimento.setCodigo(100L);
			return recebimento;
		});

		// Executando o método que estamos testando
		String resultado = recebimentoService.abrirRecebimento(10L, new String[] { "1", "2" });

		assertEquals("100", resultado);

		verify(recebimentos).save(any(Recebimento.class));
	}
	@Test
	void deveRecusarParcelaJaQuitada() {
		Parcela parcela1 = mock(Parcela.class);
		
		when(parcela1.getQuitado()).thenReturn(1);
		when(parcela1.getCodigo()).thenReturn(1L);
		
		when(parcelas.busca(1L)).thenReturn(parcela1);
		
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
	        recebimentoService.abrirRecebimento(10L, new String[] { "1" });
	    });

	    assertEquals("Parcela 1 já esta quitada, verifique.", exception.getMessage());

	    verify(recebimentos, never()).save(any());
	}
	@Test 
	void deveRecusarParcelaDeOutroCliente(){
		// Criar mockde parcela, pessoa e receber
		Parcela parcela1 = mock(Parcela.class);
		Pessoa caio = mock(Pessoa.class);
		Receber receber1 = mock(Receber.class);
		
		// Parcela aberta
		when(parcela1.getQuitado()).thenReturn(0);
		
		// Buscar parcela
		when(parcelas.busca(1L)).thenReturn(parcela1);
		
		// Relacionar parcela -> receber -> pessoa
		when(parcela1.getReceber()).thenReturn(receber1);
		when(receber1.getPessoa()).thenReturn(caio);
		
		// Codigo da pessoa
		when(caio.getCodigo()).thenReturn(10L);
		when(parcela1.getCodigo()).thenReturn(1L);

		// Chamar abrirRecebimento e esperar excecao
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			recebimentoService.abrirRecebimento(20L, new String[] {"1"});
		});

		assertEquals("A parcela 1 não pertence ao cliente selecionado", exception.getMessage());

		verify(recebimentos, never()).save(any());
	}
	@Test
	void deveRecusarClienteInexistente() {
		// Criar mock de pessoa, parcelas e receber
		Pessoa caio = mock(Pessoa.class);
		Parcela parcela1 = mock(Parcela.class);
		Receber receber1 = mock(Receber.class);
		
		// Parcela aberta
		when(parcela1.getQuitado()).thenReturn(0);
		
		when(parcelas.busca(1L)).thenReturn(parcela1);
		
		when(parcela1.getReceber()).thenReturn(receber1);
		
		when(receber1.getPessoa()).thenReturn(caio);
		
		when(caio.getCodigo()).thenReturn(10L);
		
		// Quando o service procurar o cliente, não encontra a pessoa
		when(pessoas.buscaPessoa(10L)).thenReturn(Optional.empty());

		// Chamar abrirrecebimento e esperar excecao
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			recebimentoService.abrirRecebimento(10L, new String[] {"1"});
		});

		assertEquals("Cliente não encontrado", exception.getMessage());

		verify(recebimentos, never()).save(any());
	}
	@Test
	void deveCalcularValorTotalDasParcelas() {
	    Pessoa caio = mock(Pessoa.class);
	    when(caio.getCodigo()).thenReturn(10L);

	    Receber receber = mock(Receber.class);
	    when(receber.getPessoa()).thenReturn(caio);

	    Parcela parcela1 = mock(Parcela.class);
	    when(parcela1.getQuitado()).thenReturn(0);
	    when(parcela1.getValor_restante()).thenReturn(50.0);
	    when(parcela1.getReceber()).thenReturn(receber);

	    Parcela parcela2 = mock(Parcela.class);
	    when(parcela2.getQuitado()).thenReturn(0);
	    when(parcela2.getValor_restante()).thenReturn(60.0);
	    when(parcela2.getReceber()).thenReturn(receber);

	    when(parcelas.busca(1L)).thenReturn(parcela1);
	    when(parcelas.busca(2L)).thenReturn(parcela2);
	    when(pessoas.buscaPessoa(10L)).thenReturn(Optional.of(caio));

	    when(recebimentos.save(any(Recebimento.class))).thenAnswer(invocacao -> {
	        Recebimento recebimento = invocacao.getArgument(0);
	        recebimento.setCodigo(100L);
	        return recebimento;
	    });

	    recebimentoService.abrirRecebimento(10L, new String[] { "1", "2" });

	    ArgumentCaptor<Recebimento> captor = ArgumentCaptor.forClass(Recebimento.class);
	    verify(recebimentos).save(captor.capture());

	    assertEquals(110.0, captor.getValue().getValor_total());
	}

	@Test
	void deveCriarRecebimentoComArrayDeParcelasVazio() {
		Pessoa caio = mock(Pessoa.class);

		when(pessoas.buscaPessoa(10L)).thenReturn(Optional.of(caio));

		when(recebimentos.save(any(Recebimento.class))).thenAnswer(invocacao -> {
			Recebimento recebimento = invocacao.getArgument(0);
			recebimento.setCodigo(100L);
			return recebimento;
		});

		String resultado = recebimentoService.abrirRecebimento(10L, new String[] {});

		assertEquals("100", resultado);

		ArgumentCaptor<Recebimento> captor = ArgumentCaptor.forClass(Recebimento.class);
		verify(recebimentos).save(captor.capture());

		assertEquals(0.0, captor.getValue().getValor_total());
	}

	//Testes do método receber

	@AfterEach
	void limparUsuarioLogado() {
		SecurityContextHolder.clearContext();
	}

	// Simula o usuário logado, pois o receber usa a classe Aplicacao
	private void logarUsuario() {
		SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("gerente", "123"));
	}

	// Cria um título com a sigla informada (ex: DIN, CARTDEB, CARTCRED)
	private Titulo criarTitulo(String sigla) {
		TituloTipo tipo = new TituloTipo();
		tipo.setSigla(sigla);

		Titulo titulo = new Titulo();
		titulo.setTipo(tipo);
		return titulo;
	}

	private Recebimento criarRecebimentoAberto(Double valorTotal) {
		Recebimento recebimento = new Recebimento();
		recebimento.setValor_total(valorTotal);
		return recebimento;
	}

	@Test
	void deveRecusarRecebimentoSemTitulo() {
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			recebimentoService.receber(1L, 50.0, 0.0, 0.0, 0L);
		});

		assertEquals("Selecione um título para realizar o recebimento", exception.getMessage());

		verify(recebimentos, never()).save(any());
	}

	@Test
	void deveRecusarRecebimentoJaFechado() {
		Recebimento recebimento = criarRecebimentoAberto(100.0);
		recebimento.setData_processamento(new Timestamp(System.currentTimeMillis()));

		when(recebimentos.findById(1L)).thenReturn(Optional.of(recebimento));

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			recebimentoService.receber(1L, 50.0, 0.0, 0.0, 5L);
		});

		assertEquals("Recebimento já esta fechado", exception.getMessage());

		verify(recebimentos, never()).save(any());
	}

	@Test
	void deveRecusarValorSuperiorAoTotal() {
		when(recebimentos.findById(1L)).thenReturn(Optional.of(criarRecebimentoAberto(100.0)));
		when(titulos.busca(5L)).thenReturn(Optional.of(criarTitulo("DIN")));

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			recebimentoService.receber(1L, 150.0, 0.0, 0.0, 5L);
		});

		assertEquals("Valor de recebimento é superior aos títulos", exception.getMessage());

		verify(recebimentos, never()).save(any());
	}

	@Test
	void deveRecusarRecebimentoSemParcelas() {
		when(recebimentos.findById(1L)).thenReturn(Optional.of(criarRecebimentoAberto(100.0)));
		when(titulos.busca(5L)).thenReturn(Optional.of(criarTitulo("DIN")));
		when(receParcelas.parcelasDoReceber(1L)).thenReturn(new ArrayList<>());

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			recebimentoService.receber(1L, 50.0, 0.0, 0.0, 5L);
		});

		assertEquals("Recebimento não possue parcelas", exception.getMessage());

		verify(recebimentos, never()).save(any());
	}

	@Test
	void deveRecusarValorRecebidoZero() {
		Parcela parcela1 = mock(Parcela.class);
		List<Parcela> lista = new ArrayList<>();
		lista.add(parcela1);

		when(recebimentos.findById(1L)).thenReturn(Optional.of(criarRecebimentoAberto(100.0)));
		when(titulos.busca(5L)).thenReturn(Optional.of(criarTitulo("DIN")));
		when(receParcelas.parcelasDoReceber(1L)).thenReturn(lista);

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			recebimentoService.receber(1L, 0.0, 0.0, 0.0, 5L);
		});

		assertEquals("Valor de recebimento inválido", exception.getMessage());

		verify(parcelas, never()).receber(any(), anyDouble(), anyDouble(), anyDouble());
		verify(recebimentos, never()).save(any());
	}

	@Test
	void deveReceberEmDinheiroELancarNoCaixa() {
		logarUsuario();

		Recebimento recebimento = criarRecebimentoAberto(110.0);

		Parcela parcela1 = mock(Parcela.class);
		when(parcela1.getValor_restante()).thenReturn(50.0);
		when(parcela1.getCodigo()).thenReturn(1L);

		Parcela parcela2 = mock(Parcela.class);
		when(parcela2.getValor_restante()).thenReturn(60.0);
		when(parcela2.getCodigo()).thenReturn(2L);

		List<Parcela> lista = new ArrayList<>();
		lista.add(parcela1);
		lista.add(parcela2);

		when(recebimentos.findById(1L)).thenReturn(Optional.of(recebimento));
		when(titulos.busca(5L)).thenReturn(Optional.of(criarTitulo("DIN")));
		when(receParcelas.parcelasDoReceber(1L)).thenReturn(lista);
		when(usuarios.buscaUsuario("gerente")).thenReturn(mock(Usuario.class));
		when(caixas.caixaAberto()).thenReturn(Optional.of(mock(Caixa.class)));

		String resultado = recebimentoService.receber(1L, 110.0, 5.0, 2.0, 5L);

		assertEquals("Recebimento realizado com sucesso", resultado);

		verify(parcelas).receber(1L, 50.0, 0.00, 0.00);
		verify(parcelas).receber(2L, 60.0, 0.00, 0.00);

		verify(lancamentos).lancamento(any(CaixaLancamento.class));
		verify(cartaoLancamentos, never()).lancamento(anyDouble(), any());

		verify(recebimentos).save(recebimento);
		assertEquals(110.0, recebimento.getValor_recebido());
		assertNotNull(recebimento.getData_processamento());

		// Acréscimo, desconto e título gravados no recebimento
		assertEquals(5.0, recebimento.getValor_acrescimo());
		assertEquals(2.0, recebimento.getValor_desconto());
		assertEquals("DIN", recebimento.getTitulo().getTipo().getSigla());
	}

	@Test
	void deveReceberParcialmenteSomenteAPrimeiraParcela() {
		logarUsuario();

		Parcela parcela1 = mock(Parcela.class);
		when(parcela1.getValor_restante()).thenReturn(50.0);
		when(parcela1.getCodigo()).thenReturn(1L);

		Parcela parcela2 = mock(Parcela.class);

		List<Parcela> lista = new ArrayList<>();
		lista.add(parcela1);
		lista.add(parcela2);

		when(recebimentos.findById(1L)).thenReturn(Optional.of(criarRecebimentoAberto(110.0)));
		when(titulos.busca(5L)).thenReturn(Optional.of(criarTitulo("DIN")));
		when(receParcelas.parcelasDoReceber(1L)).thenReturn(lista);
		when(caixas.caixaAberto()).thenReturn(Optional.of(mock(Caixa.class)));

		recebimentoService.receber(1L, 30.0, 0.0, 0.0, 5L);

		verify(parcelas).receber(1L, 30.0, 0.00, 0.00);
	
		// Só a primeira parcela recebe; a segunda não recebe nada
		verify(parcelas, times(1)).receber(any(), anyDouble(), anyDouble(), anyDouble());
	}

	@Test
	void deveReceberNoCartaoSemLancarNoCaixa() {
		logarUsuario();

		Parcela parcela1 = mock(Parcela.class);
		when(parcela1.getValor_restante()).thenReturn(100.0);
		when(parcela1.getCodigo()).thenReturn(1L);

		List<Parcela> lista = new ArrayList<>();
		lista.add(parcela1);

		Optional<Titulo> titulo = Optional.of(criarTitulo("CARTCRED"));

		when(recebimentos.findById(1L)).thenReturn(Optional.of(criarRecebimentoAberto(100.0)));
		when(titulos.busca(5L)).thenReturn(titulo);
		when(receParcelas.parcelasDoReceber(1L)).thenReturn(lista);

		String resultado = recebimentoService.receber(1L, 100.0, 0.0, 0.0, 5L);

		assertEquals("Recebimento realizado com sucesso", resultado);

		verify(cartaoLancamentos).lancamento(100.0, titulo);
		verify(lancamentos, never()).lancamento(any());
		verify(caixas, never()).caixaAberto();
	}

	//Testes do método remover

	@Test
	void deveRemoverRecebimentoAberto() {
		when(recebimentos.findById(1L)).thenReturn(Optional.of(criarRecebimentoAberto(100.0)));

		String resultado = recebimentoService.remover(1L);

		assertEquals("removido com sucesso", resultado);

		verify(recebimentos).deleteById(1L);
	}

	@Test
	void deveRecusarRemoverRecebimentoProcessado() {
		Recebimento recebimento = criarRecebimentoAberto(100.0);
		recebimento.setData_processamento(new Timestamp(System.currentTimeMillis()));

		when(recebimentos.findById(1L)).thenReturn(Optional.of(recebimento));

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			recebimentoService.remover(1L);
		});

		assertEquals("Esse recebimento não pode ser removido, pois ele já esta processado", exception.getMessage());

		verify(recebimentos, never()).deleteById(any());
	}

	@Test
	void deveRetornarErroQuandoFalharAoRemover() {
		when(recebimentos.findById(1L)).thenReturn(Optional.of(criarRecebimentoAberto(100.0)));

		doThrow(new RuntimeException("erro no banco")).when(recebimentos).deleteById(1L);

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			recebimentoService.remover(1L);
		});

		assertEquals("Erro ao remover orçamento, chame o suporte", exception.getMessage());
	}

	@Test
	void deveReceberSomenteAsParcelasExistentes() {
		logarUsuario();

		// Total do recebimento é 200, mas a única parcela vale 50
		Parcela parcela1 = mock(Parcela.class);
		when(parcela1.getValor_restante()).thenReturn(50.0);
		when(parcela1.getCodigo()).thenReturn(1L);

		List<Parcela> lista = new ArrayList<>();
		lista.add(parcela1);

		when(recebimentos.findById(1L)).thenReturn(Optional.of(criarRecebimentoAberto(200.0)));
		when(titulos.busca(5L)).thenReturn(Optional.of(criarTitulo("DIN")));
		when(receParcelas.parcelasDoReceber(1L)).thenReturn(lista);
		when(caixas.caixaAberto()).thenReturn(Optional.of(mock(Caixa.class)));

		// Recebe 100: quita a parcela de 50 e sobram 50 sem parcela para receber
		recebimentoService.receber(1L, 100.0, 0.0, 0.0, 5L);

		// O laço percorre só a parcela existente
		verify(parcelas, times(1)).receber(1L, 50.0, 0.00, 0.00);
	}

}
