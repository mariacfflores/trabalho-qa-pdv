package net.originmobi.pdv.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.originmobi.pdv.enumerado.caixa.CaixaTipo;
import net.originmobi.pdv.enumerado.caixa.EstiloLancamento;
import net.originmobi.pdv.enumerado.caixa.TipoLancamento;
import net.originmobi.pdv.filter.BancoFilter;
import net.originmobi.pdv.filter.CaixaFilter;
import net.originmobi.pdv.model.Caixa;
import net.originmobi.pdv.model.CaixaLancamento;
import net.originmobi.pdv.model.Usuario;
import net.originmobi.pdv.repository.CaixaRepository;
import net.originmobi.pdv.singleton.Aplicacao;

@Service
public class CaixaService {

	private final CaixaRepository caixas;

	private final UsuarioService usuarios;

	private final CaixaLancamentoService lancamentos;

	public CaixaService(CaixaRepository caixas, UsuarioService usuarios, CaixaLancamentoService lancamentos) {
		this.caixas = caixas;
		this.usuarios = usuarios;
		this.lancamentos = lancamentos;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Long cadastro(Caixa caixa) {

		if (caixa.getTipo().equals(CaixaTipo.CAIXA) && caixaIsAberto())
			throw new CaixaException("Existe caixa de dias anteriores em aberto, favor verifique");

		// caso o valor de abertura seja null, modifica o mesmo para 0.0, esse valor é
		// adicionado tambem no valor_total
		Double vlbertura = caixa.getValor_abertura() == null ? 0.0 : caixa.getValor_abertura();
		caixa.setValor_abertura(vlbertura);

		if (caixa.getValor_abertura() < 0)
			throw new CaixaException("Valor informado é inválido");

		Aplicacao aplicacao = Aplicacao.getInstancia();
		Usuario usuario = usuarios.buscaUsuario(aplicacao.getUsuarioAtual());

		LocalDate dataAtual = LocalDate.now(ZoneId.systemDefault());

		caixa.setDescricao(definirDescricao(caixa));
		caixa.setUsuario(usuario);
		caixa.setData_cadastro(java.sql.Date.valueOf(dataAtual));

		// se for BANCO, limpa os valores especiais de agencia e conta
		if (caixa.getTipo().equals(CaixaTipo.BANCO))
			limparDadosBancarios(caixa);

		try {
			caixas.save(caixa);
		} catch (Exception e) {
			e.getStackTrace();
			throw new CaixaException("Erro no processo de abertura, chame o suporte técnico");
		}

		if (caixa.getValor_abertura() > 0) {
			lancarSaldoInicial(caixa, usuario);
		} else {
			// se não for realizado o lançamento de caixa então joga o valor total do caixa
			// para 0.0
			caixa.setValor_total(0.0);
		}

		return caixa.getCodigo();
	}

	// usa a descrição informada ou, se vier vazia, a descrição padrão do tipo
	private String definirDescricao(Caixa caixa) {
		if (!caixa.getDescricao().isEmpty())
			return caixa.getDescricao();

		switch (caixa.getTipo()) {
		case COFRE:
			return "Cofre";
		case BANCO:
			return "Banco";
		default:
			return "Caixa diário";
		}
	}

	private String observacaoAbertura(CaixaTipo tipo) {
		switch (tipo) {
		case CAIXA:
			return "Abertura de caixa";
		case COFRE:
			return "Abertura de cofre";
		default:
			return "Abertura de banco";
		}
	}

	private void limparDadosBancarios(Caixa caixa) {
		caixa.setAgencia(caixa.getAgencia().replaceAll("\\D", ""));
		caixa.setConta(caixa.getConta().replaceAll("\\D", ""));
	}

	private void lancarSaldoInicial(Caixa caixa, Usuario usuario) {
		try {

			CaixaLancamento lancamento = new CaixaLancamento(observacaoAbertura(caixa.getTipo()),
					caixa.getValor_abertura(), TipoLancamento.SALDOINICIAL, EstiloLancamento.ENTRADA, caixa, usuario);

			lancamentos.lancamento(lancamento);

		} catch (Exception e) {
			e.getStackTrace();
			throw new CaixaException("Erro no processo, chame o suporte");
		}
	}

	public String fechaCaixa(Long caixa, String senha) {

		Aplicacao aplicacao = Aplicacao.getInstancia();
		Usuario usuario = usuarios.buscaUsuario(aplicacao.getUsuarioAtual());

		BCryptPasswordEncoder decode = new BCryptPasswordEncoder();

		if (senha.equals(""))
			return "Favor, informe a senha";

		if (decode.matches(senha, usuario.getSenha())) {

			// busca caixa atual
			Caixa caixaAtual = caixas.findById(caixa)
					.orElseThrow(() -> new CaixaException("Caixa não encontrado"));

			if (caixaAtual.getData_fechamento() != null)
				throw new CaixaException("Caixa já esta fechado");

			Double valorTotal = caixaAtual.getValor_total() == null ? 0.0 : caixaAtual.getValor_total();

			Timestamp dataHoraAtual = new Timestamp(System.currentTimeMillis());
			caixaAtual.setData_fechamento(dataHoraAtual);
			caixaAtual.setValor_fechamento(valorTotal);

			try {
				caixas.save(caixaAtual);
			} catch (Exception e) {
				throw new CaixaException("Ocorreu um erro ao fechar o caixa, chame o suporte");
			}

			return "Caixa fechado com sucesso";

		} else {
			return "Senha incorreta, favor verifique";
		}
	}

	public boolean caixaIsAberto() {
		return caixas.caixaAberto().isPresent();
	}

	public List<Caixa> listaTodos() {
		return caixas.findByCodigoOrdenado();
	}

	public List<Caixa> listarCaixas(CaixaFilter filter) {
		if (filter.getData_cadastro() != null && !filter.getData_cadastro().equals("")) {
			filter.setData_cadastro(filter.getData_cadastro().replace("/", "-"));
			return caixas.buscaCaixasPorDataAbertura(Date.valueOf(filter.getData_cadastro()));
		}
		
		return caixas.listaCaixasAbertos();
	}

	public Optional<Caixa> caixaAberto() {
		return caixas.caixaAberto();
	}

	public List<Caixa> caixasAbertos() {
		return caixas.caixasAbertos();
	}

	public Optional<Caixa> busca(Long codigo) {
		return caixas.findById(codigo);
	}

	// pega o caixa aberto do usuário informado
	public Optional<Caixa> buscaCaixaUsuario(String usuario) {
		Usuario usu = usuarios.buscaUsuario(usuario);
		return Optional.ofNullable(caixas.findByCaixaAbertoUsuario(usu.getCodigo()));
	}

	public List<Caixa> listaBancos() {
		return caixas.buscaBancos(CaixaTipo.BANCO);
	}

	public List<Caixa> listaCaixasAbertosTipo(CaixaTipo tipo) {
		return caixas.buscaCaixaTipo(tipo);
	}

	public List<Caixa> listaBancosAbertosTipoFilterBanco(CaixaTipo tipo, BancoFilter filter) {
		if (filter.getData_cadastro() != null && !filter.getData_cadastro().equals("")) {
			filter.setData_cadastro(filter.getData_cadastro().replace("/", "-"));
			return caixas.buscaCaixaTipoData(tipo, Date.valueOf(filter.getData_cadastro()));
		}

		return caixas.buscaCaixaTipo(CaixaTipo.BANCO);
	}

}
