package net.originmobi.pdv.service.cartao;

public class CartaoLancamentoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CartaoLancamentoException(String mensagem) {
        super(mensagem);
    }

    public CartaoLancamentoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}