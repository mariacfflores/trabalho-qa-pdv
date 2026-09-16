package net.originmobi.pdv.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
		
		// Chamar abrirRecebimento e esperar excecao
		assertThrows(RuntimeException.class, () -> {
			recebimentoService.abrirRecebimento(20L, new String[] {"1"});
		});
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
		assertThrows(RuntimeException.class, () -> {
			recebimentoService.abrirRecebimento(10L, new String[] {"1"});
		});
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
	}

}
