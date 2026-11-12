package net.originmobi.pdv.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibm.icu.text.DecimalFormat;

import net.originmobi.pdv.controller.TituloService;
import net.originmobi.pdv.enumerado.TituloTipo;
import net.originmobi.pdv.enumerado.caixa.EstiloLancamento;
import net.originmobi.pdv.enumerado.caixa.TipoLancamento;
import net.originmobi.pdv.model.Caixa;
import net.originmobi.pdv.model.CaixaLancamento;
import net.originmobi.pdv.model.Parcela;
import net.originmobi.pdv.model.Pessoa;
import net.originmobi.pdv.model.Recebimento;
import net.originmobi.pdv.model.Titulo;
import net.originmobi.pdv.model.Usuario;
import net.originmobi.pdv.repository.RecebimentoRepository;
import net.originmobi.pdv.service.cartao.CartaoLancamentoService;
import net.originmobi.pdv.singleton.Aplicacao;
import net.originmobi.pdv.utilitarios.DataAtual;

@Service
public class RecebimentoService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RecebimentoService.class);

	private static final String ERRO_SUPORTE_RECEBER = "Erro ao receber, chame o suporte";
	private static final String ERRO_SUPORTE_RECEBIMENTO = "Ocorreu um erro ao realizar o recebimento, chame o suporte";

	private final RecebimentoRepository recebimentos;
	private final PessoaService pessoas;
	private final RecebimentoParcelaService receParcelas;
	private final ParcelaService parcelas;
	private final CaixaService caixas;
	private final UsuarioService usuarios;
	private final CaixaLancamentoService lancamentos;
	private final TituloService titulos;
	private final CartaoLancamentoService cartaoLancamentos;

	// Injeção pelo construtor (o Spring usa este construtor automaticamente)
	public RecebimentoService(RecebimentoRepository recebimentos, PessoaService pessoas,
			RecebimentoParcelaService receParcelas, ParcelaService parcelas, CaixaService caixas,
			UsuarioService usuarios, CaixaLancamentoService lancamentos, TituloService titulos,
			CartaoLancamentoService cartaoLancamentos) {
		this.recebimentos = recebimentos;
		this.pessoas = pessoas;
		this.receParcelas = receParcelas;
		this.parcelas = parcelas;
		this.caixas = caixas;
		this.usuarios = usuarios;
		this.lancamentos = lancamentos;
		this.titulos = titulos;
		this.cartaoLancamentos = cartaoLancamentos;
	}

	public String abrirRecebimento(Long codpes, String[] arrayParcelas) {
		List<Parcela> lista = new ArrayList<>();

		DataAtual dataAtual = new DataAtual();
		Double vlTotal = 0.0;

		for (int i = 0; i < arrayParcelas.length; i++) {
			Parcela parcela = parcelas.busca(Long.decode(arrayParcelas[i]));

			if (parcela.getQuitado() == 1)
				throw new RecebimentoException("Parcela " + parcela.getCodigo() + " já esta quitada, verifique.");

			// equals compara o valor; != comparava a referência dos objetos Long
			if (!parcela.getReceber().getPessoa().getCodigo().equals(codpes))
				throw new RecebimentoException(
						"A parcela " + parcela.getCodigo() + " não pertence ao cliente selecionado");

			try {
				lista.add(parcela);

				vlTotal = vlTotal + parcela.getValor_restante();

			} catch (Exception e) {
				throw new RecebimentoException(ERRO_SUPORTE_RECEBER, e);
			}
		}

		Optional<Pessoa> pessoa = pessoas.buscaPessoa(codpes);

		if (!pessoa.isPresent())
			throw new RecebimentoException("Cliente não encontrado");

		Recebimento recebimento = new Recebimento(vlTotal, dataAtual.dataAtualTimeStamp(), pessoa.get(), lista);

		try {
			recebimentos.save(recebimento);
		} catch (Exception e) {
			throw new RecebimentoException(ERRO_SUPORTE_RECEBER, e);
		}

		return recebimento.getCodigo().toString();
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public String receber(Long codreceber, Double vlrecebido, Double vlacrescimo, Double vldesconto, Long codtitulo) {
		// verifica null antes de comparar com 0, senão o null gerava NullPointerException
		if (codtitulo == null || codtitulo == 0)
			throw new RecebimentoException("Selecione um título para realizar o recebimento");

		Recebimento recebimento = buscaRecebimentoAberto(codreceber);
		Titulo titulo = buscaTitulo(codtitulo);

		// vincula o titulo ao recebimento
		recebimento.setTitulo(titulo);

		validaValorRecebido(recebimento, vlrecebido);

		List<Parcela> listParcelas = receParcelas.parcelasDoReceber(codreceber);

		if (listParcelas.isEmpty())
			throw new RecebimentoException("Recebimento não possue parcelas");

		if (vlrecebido <= 0.0)
			throw new RecebimentoException("Valor de recebimento inválido");

		receberParcelas(listParcelas, vlrecebido);

		lancarRecebimento(codreceber, recebimento, titulo, vlrecebido);

		fecharRecebimento(recebimento, vlrecebido, vlacrescimo, vldesconto);

		return "Recebimento realizado com sucesso";
	}

	private Recebimento buscaRecebimentoAberto(Long codreceber) {
		Optional<Recebimento> recebimento = recebimentos.findById(codreceber);

		if (!recebimento.isPresent())
			throw new RecebimentoException("Recebimento não encontrado");

		if (recebimento.get().getData_processamento() != null)
			throw new RecebimentoException("Recebimento já esta fechado");

		return recebimento.get();
	}

	private Titulo buscaTitulo(Long codtitulo) {
		Optional<Titulo> titulo = titulos.busca(codtitulo);

		if (!titulo.isPresent())
			throw new RecebimentoException("Título não encontrado");

		return titulo.get();
	}

	private void validaValorRecebido(Recebimento recebimento, Double vlrecebido) {
		DecimalFormat formata = new DecimalFormat("0.00");
		Double vlrecebimento = Double.valueOf(formata.format(recebimento.getValor_total()).replace(",", "."));

		if (vlrecebido > vlrecebimento)
			throw new RecebimentoException("Valor de recebimento é superior aos títulos");
	}

	// verifica cada parcela que veio e realiza o seu recebimento individual
	private void receberParcelas(List<Parcela> listParcelas, Double vlrecebido) {
		for (int i = 0; i < listParcelas.size(); i++) {

			if (vlrecebido > 0) {
				Double vlsobra = vlrecebido - listParcelas.get(i).getValor_restante();
				vlsobra = vlsobra < 0 ? 0 : vlsobra;

				Double vlquitado = vlsobra - vlrecebido;
				vlquitado = vlquitado < 0 ? vlquitado * -1 : vlquitado;

				vlrecebido = vlsobra;

				Long parcela = listParcelas.get(i).getCodigo();

				try {
					parcelas.receber(parcela, vlquitado, 0.00, 0.00);
				} catch (Exception e) {
					throw new RecebimentoException(ERRO_SUPORTE_RECEBIMENTO, e);
				}
			}
		}
	}

	// lança no cartão ou no caixa, de acordo com o tipo do título
	private void lancarRecebimento(Long codreceber, Recebimento recebimento, Titulo titulo, Double vllancamento) {
		Aplicacao aplicacao = new Aplicacao();
		Usuario usuario = usuarios.buscaUsuario(aplicacao.getUsuarioAtual());

		String sigla = titulo.getTipo().getSigla();

		if (sigla.equals(TituloTipo.CARTDEB.toString()) || sigla.equals(TituloTipo.CARTCRED.toString())) {
			cartaoLancamentos.lancamento(vllancamento, Optional.of(titulo));
			return;
		}

		Optional<Caixa> caixa = caixas.caixaAberto();

		if (!caixa.isPresent())
			throw new RecebimentoException("Nenhum caixa aberto");

		CaixaLancamento lancamento = new CaixaLancamento("Referente ao recebimento " + codreceber, vllancamento,
				TipoLancamento.RECEBIMENTO, EstiloLancamento.ENTRADA, caixa.get(), usuario);

		// vincula o recebimento ao caixa_lancamento
		lancamento.setRecebimento(recebimento);

		try {
			lancamentos.lancamento(lancamento);
		} catch (Exception e) {
			throw new RecebimentoException(ERRO_SUPORTE_RECEBIMENTO, e);
		}
	}

	private void fecharRecebimento(Recebimento recebimento, Double vllancamento, Double vlacrescimo,
			Double vldesconto) {
		try {
			DataAtual dataAtual = new DataAtual();

			recebimento.setValor_recebido(vllancamento);
			recebimento.setValor_acrescimo(vlacrescimo);
			recebimento.setValor_desconto(vldesconto);
			recebimento.setData_processamento(dataAtual.dataAtualTimeStamp());

			recebimentos.save(recebimento);
		} catch (Exception e) {
			throw new RecebimentoException(ERRO_SUPORTE_RECEBIMENTO, e);
		}
	}

	public String remover(Long codigo) {
		Optional<Recebimento> recebimento = recebimentos.findById(codigo);

		if (recebimento.map(Recebimento::getData_processamento).isPresent())
			throw new RecebimentoException("Esse recebimento não pode ser removido, pois ele já esta processado");

		try {
			recebimentos.deleteById(codigo);
		} catch (Exception e) {
			LOGGER.error("Erro ao remover recebimento {}", codigo, e);
			throw new RecebimentoException("Erro ao remover orçamento, chame o suporte", e);
		}

		return "removido com sucesso";
	}

}
