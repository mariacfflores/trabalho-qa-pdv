package net.originmobi.pdv.service.cartao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import net.originmobi.pdv.enumerado.TituloTipo;
import net.originmobi.pdv.enumerado.cartao.CartaoSituacao;
import net.originmobi.pdv.enumerado.cartao.CartaoTipo;
import net.originmobi.pdv.filter.CartaoFilter;
import net.originmobi.pdv.model.Titulo;
import net.originmobi.pdv.model.cartao.CartaoLancamento;
import net.originmobi.pdv.repository.cartao.CartaoLancamentoRepository;

public class CartaoLancamentoServiceTest {

    private CartaoLancamentoService service;
    private CartaoLancamentoRepository repository;

    @Before
    public void setUp() {

        service = new CartaoLancamentoService();

        repository = mock(CartaoLancamentoRepository.class);

        ReflectionTestUtils.setField(
                service,
                "repository",
                repository
        );
    }

    // =========================================================
    // TESTES DO MÉTODO lancamento()
    // =========================================================

    @Test
    public void deveRealizarLancamentoDeCartaoDebito() {

        Titulo titulo = mock(
                Titulo.class,
                Mockito.RETURNS_DEEP_STUBS
        );

        when(titulo.getTipo().getSigla())
                .thenReturn(TituloTipo.CARTDEB.toString());

        when(titulo.getMaquina().getTaxa_debito())
                .thenReturn(2.0);

        when(titulo.getMaquina().getDias_debito())
                .thenReturn(1);

        when(titulo.getMaquina().getTaxa_antecipacao())
                .thenReturn(1.0);

        service.lancamento(
                100.0,
                Optional.of(titulo)
        );

        verify(repository)
                .save(any(CartaoLancamento.class));
    }

    @Test
    public void deveRealizarLancamentoDeCartaoCredito() {

        Titulo titulo = mock(
                Titulo.class,
                Mockito.RETURNS_DEEP_STUBS
        );

        when(titulo.getTipo().getSigla())
                .thenReturn(TituloTipo.CARTCRED.toString());

        when(titulo.getMaquina().getTaxa_credito())
                .thenReturn(3.0);

        when(titulo.getMaquina().getDias_credito())
                .thenReturn(30);

        when(titulo.getMaquina().getTaxa_antecipacao())
                .thenReturn(1.5);

        service.lancamento(
                100.0,
                Optional.of(titulo)
        );

        verify(repository)
                .save(any(CartaoLancamento.class));
    }

    // =========================================================
    // TESTES DO MÉTODO listar()
    // =========================================================

    @Test
    public void deveListarLancamentosSemFiltros() {

        CartaoFilter filter = mock(CartaoFilter.class);

        service.listar(filter);

        verify(repository).buscaLancamentos(
                "%",
                "%",
                "%"
        );
    }

    @Test
    public void deveListarLancamentosComFiltrosPreenchidos() {

        CartaoFilter filter = mock(CartaoFilter.class);

        when(filter.getSituacao())
                .thenReturn(CartaoSituacao.PROCESSADO);

        when(filter.getTipo())
                .thenReturn(CartaoTipo.DEBITO);

        when(filter.getData_recebimento())
                .thenReturn("15/09/2026");

        service.listar(filter);

        verify(repository).buscaLancamentos(
                CartaoSituacao.PROCESSADO.toString(),
                CartaoTipo.DEBITO.toString(),
                "15-09-2026"
        );
    }

    // =========================================================
    // TESTES DO MÉTODO processar()
    // =========================================================

    @Test
    public void deveLancarErroAoProcessarCartaoJaProcessado() {

        CartaoLancamento cartao =
                mock(CartaoLancamento.class);

        when(cartao.getSituacao())
                .thenReturn(CartaoSituacao.PROCESSADO);

        try {

            service.processar(cartao);

            fail("Era esperada uma RuntimeException");

        } catch (RuntimeException e) {

            assertEquals(
                    "Registro já processado",
                    e.getMessage()
            );
        }
    }

    @Test
    public void deveLancarErroAoProcessarCartaoAntecipado() {

        CartaoLancamento cartao =
                mock(CartaoLancamento.class);

        when(cartao.getSituacao())
                .thenReturn(CartaoSituacao.ANTECIPADO);

        try {

            service.processar(cartao);

            fail("Era esperada uma RuntimeException");

        } catch (RuntimeException e) {

            assertEquals(
                    "Registro já foi antecipado",
                    e.getMessage()
            );
        }
    }

    // =========================================================
    // TESTES DO MÉTODO antecipar()
    // =========================================================

    @Test
    public void deveLancarErroAoAnteciparCartaoJaProcessado() {

        CartaoLancamento cartao =
                mock(CartaoLancamento.class);

        when(cartao.getSituacao())
                .thenReturn(CartaoSituacao.PROCESSADO);

        try {

            service.antecipar(cartao);

            fail("Era esperada uma RuntimeException");

        } catch (RuntimeException e) {

            assertEquals(
                    "Registro já processado",
                    e.getMessage()
            );
        }
    }

    @Test
    public void deveLancarErroAoAnteciparCartaoJaAntecipado() {

        CartaoLancamento cartao =
                mock(CartaoLancamento.class);

        when(cartao.getSituacao())
                .thenReturn(CartaoSituacao.ANTECIPADO);

        try {

            service.antecipar(cartao);

            fail("Era esperada uma RuntimeException");

        } catch (RuntimeException e) {

            assertEquals(
                    "Registro já foi antecipado",
                    e.getMessage()
            );
        }
    }
}