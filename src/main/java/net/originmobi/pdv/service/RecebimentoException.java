package net.originmobi.pdv.service;

public class RecebimentoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RecebimentoException(String mensagem) {
		super(mensagem);
	}

	public RecebimentoException(String mensagem, Throwable causa) {
		super(mensagem, causa);
	}
}
