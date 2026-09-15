package net.originmobi.pdv.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
		Pessoa pessoa = mock(Pessoa.class);

		when(pessoa.getCodigo()).thenReturn(10L);

		Receber receber1 = mock(Receber.class);
		Receber receber2 = mock(Receber.class);

		when(receber1.getPessoa()).thenReturn(pessoa);
		when(receber2.getPessoa()).thenReturn(pessoa);

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
		when(pessoas.buscaPessoa(10L)).thenReturn(Optional.of(pessoa));

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

}
