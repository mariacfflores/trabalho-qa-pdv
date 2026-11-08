package net.originmobi.pdv.service.notafiscal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.springframework.test.util.ReflectionTestUtils;

import net.originmobi.pdv.enumerado.notafiscal.NotaFiscalTipo;
import net.originmobi.pdv.model.Empresa;
import net.originmobi.pdv.model.EmpresaParametro;
import net.originmobi.pdv.model.NotaFiscal;
import net.originmobi.pdv.model.NotaFiscalTotais;
import net.originmobi.pdv.model.Pessoa;
import net.originmobi.pdv.repository.EmpresaRepository;
import net.originmobi.pdv.repository.PessoaRepository;
import net.originmobi.pdv.repository.notafiscal.NotaFiscalRepository;
import net.originmobi.pdv.repository.notafiscal.NotaFiscalTotaisRepository;
import net.originmobi.pdv.service.EmpresaService;
import net.originmobi.pdv.service.PessoaService;

/** Integra serviços reais; somente os repositórios são simulados. */
@DisplayName("Integração entre os serviços de cadastro da nota fiscal")
class NotaFiscalServiceIntegrationTest {
	private NotaFiscalService notaService;
	private EmpresaRepository empresas;
	private PessoaRepository pessoas;
	private NotaFiscalTotaisRepository totais;
	private NotaFiscalRepository notas;
	private Empresa empresa;
	private Pessoa destinatario;

	@BeforeEach
	void setUp() {
		notaService = new NotaFiscalService();
		EmpresaService empresaService = new EmpresaService();
		PessoaService pessoaService = new PessoaService();
		NotaFiscalTotaisServer totaisService = new NotaFiscalTotaisServer();
		empresas = mock(EmpresaRepository.class);
		pessoas = mock(PessoaRepository.class);
		totais = mock(NotaFiscalTotaisRepository.class);
		notas = mock(NotaFiscalRepository.class);
		ReflectionTestUtils.setField(empresaService, "empresas", empresas);
		ReflectionTestUtils.setField(pessoaService, "pessoas", pessoas);
		ReflectionTestUtils.setField(totaisService, "totais", totais);
		ReflectionTestUtils.setField(notaService, "empresas", empresaService);
		ReflectionTestUtils.setField(notaService, "pessoas", pessoaService);
		ReflectionTestUtils.setField(notaService, "notaTotais", totaisService);
		ReflectionTestUtils.setField(notaService, "notasFiscais", notas);

		EmpresaParametro parametro = new EmpresaParametro();
		parametro.setSerie_nfe(3);
		parametro.setAmbiente(2);
		empresa = new Empresa();
		empresa.setCodigo(10L);
		empresa.setParametro(parametro);
		destinatario = new Pessoa();
		destinatario.setCodigo(1L);
	}

	@Test
	@DisplayName("Integra empresa, destinatário e totais ao cadastrar a nota")
	void deveIntegrarServicosAoCadastrarNota() {
		prepararConsultasValidas();
		when(totais.save(any(NotaFiscalTotais.class))).thenAnswer(invocacao -> {
			NotaFiscalTotais total = invocacao.getArgument(0);
			total.setCodigo(20L);
			return total;
		});
		when(notas.buscaUltimaNota(3)).thenReturn(42L);
		when(notas.save(any(NotaFiscal.class))).thenAnswer(invocacao -> {
			NotaFiscal nota = invocacao.getArgument(0);
			assertEquals(Long.valueOf(20), nota.getTotais().getCodigo());
			nota.setCodigo(100L);
			return nota;
		});
		assertEquals("100", cadastrar());

		ArgumentCaptor<NotaFiscalTotais> totalCaptor = ArgumentCaptor.forClass(NotaFiscalTotais.class);
		ArgumentCaptor<NotaFiscal> notaCaptor = ArgumentCaptor.forClass(NotaFiscal.class);
		InOrder ordem = inOrder(empresas, pessoas, totais, notas);
		ordem.verify(empresas).buscaEmpresaCadastrada();
		ordem.verify(pessoas).findById(1L);
		ordem.verify(totais).save(totalCaptor.capture());
		ordem.verify(notas).buscaUltimaNota(3);
		ordem.verify(notas).save(notaCaptor.capture());
		NotaFiscal nota = notaCaptor.getValue();
		assertSame(empresa, nota.getEmissor());
		assertSame(destinatario, nota.getDestinatario());
		assertSame(totalCaptor.getValue(), nota.getTotais());
		assertEquals(Long.valueOf(42), nota.getNumero());
		assertEquals(3, nota.getSerie());
		assertEquals("Venda", nota.getNatureza_operacao());
		assertEquals(NotaFiscalTipo.SAIDA, nota.getTipo());
		NotaFiscalTotais total = totalCaptor.getValue();
		assertEquals(Collections.nCopies(13, 0.0), Arrays.asList(total.getV_bc(), total.getV_icms(),
				total.getV_st(), total.getV_prod(), total.getV_frete(), total.getV_seg(), total.getV_desc(),
				total.getV_ii(), total.getV_ipi(), total.getV_pis(), total.getV_cofins(), total.getV_outros(), total.getV_nf()));
	}

	@Test
	@DisplayName("Interrompe o cadastro quando o serviço de empresa não encontra cadastro")
	void deveRecusarEmpresaAusente() {
		when(empresas.buscaEmpresaCadastrada()).thenReturn(Optional.empty());
		when(pessoas.findById(1L)).thenReturn(Optional.of(destinatario));
		assertEquals("Nenhuma empresa cadastrada, verifique", assertThrows(RuntimeException.class, this::cadastrar).getMessage());
		verify(empresas).buscaEmpresaCadastrada();
		verify(pessoas).findById(1L);
		verifyNoInteractions(totais, notas);
	}

	@Test
	@DisplayName("Interrompe o cadastro quando o serviço de pessoa não encontra o destinatário")
	void deveRecusarDestinatarioAusente() {
		when(empresas.buscaEmpresaCadastrada()).thenReturn(Optional.of(empresa));
		when(pessoas.findById(1L)).thenReturn(Optional.empty());
		assertEquals("Favor, selecione o destinatário", assertThrows(RuntimeException.class, this::cadastrar).getMessage());
		verify(empresas).buscaEmpresaCadastrada();
		verify(pessoas).findById(1L);
		verifyNoInteractions(totais, notas);
	}

	@Test
	@DisplayName("Trata a falha propagada pelo serviço de totais sem salvar a nota")
	void deveTratarFalhaDosTotais() {
		prepararConsultasValidas();
		when(totais.save(any(NotaFiscalTotais.class))).thenThrow(new RuntimeException("Falha simulada nos totais"));
		assertEquals("Erro ao cadastrar a nota, chame o suporte", assertThrows(RuntimeException.class, this::cadastrar).getMessage());
		verify(totais).save(any(NotaFiscalTotais.class));
		verifyNoInteractions(notas);
	}

	@Test
	@DisplayName("Trata a falha ao salvar a nota depois de chamar o serviço de totais")
	void deveTratarFalhaAoSalvarNota() {
		prepararConsultasValidas();
		when(notas.buscaUltimaNota(3)).thenReturn(42L);
		when(notas.save(any(NotaFiscal.class))).thenThrow(new RuntimeException("Falha simulada na nota"));
		assertEquals("Erro ao cadastrar a nota, chame o suporte", assertThrows(RuntimeException.class, this::cadastrar).getMessage());
		InOrder ordem = inOrder(totais, notas);
		ordem.verify(totais).save(any(NotaFiscalTotais.class));
		ordem.verify(notas).buscaUltimaNota(3);
		ordem.verify(notas).save(any(NotaFiscal.class));
		// Repositórios simulados: este cenário não comprova rollback de banco.
	}

	private void prepararConsultasValidas() {
		when(empresas.buscaEmpresaCadastrada()).thenReturn(Optional.of(empresa));
		when(pessoas.findById(1L)).thenReturn(Optional.of(destinatario));
	}

	private String cadastrar() {
		return notaService.cadastrar(1L, "Venda", NotaFiscalTipo.SAIDA);
	}
}
