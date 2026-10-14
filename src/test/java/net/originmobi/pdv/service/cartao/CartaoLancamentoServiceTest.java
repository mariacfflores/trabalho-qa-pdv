package net.originmobi.pdv.service.cartao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import net.originmobi.pdv.enumerado.TituloTipo;
import net.originmobi.pdv.enumerado.cartao.CartaoSituacao;
import net.originmobi.pdv.enumerado.cartao.CartaoTipo;
import net.originmobi.pdv.filter.CartaoFilter;
import net.originmobi.pdv.model.Caixa;
import net.originmobi.pdv.model.CaixaLancamento;
import net.originmobi.pdv.model.Titulo;
import net.originmobi.pdv.model.Usuario;
import net.originmobi.pdv.model.cartao.CartaoLancamento;
import net.originmobi.pdv.repository.cartao.CartaoLancamentoRepository;
import net.originmobi.pdv.service.CaixaLancamentoService;
import net.originmobi.pdv.service.UsuarioService;

public class CartaoLancamentoServiceTest {

    private CartaoLancamentoService service;
    private CartaoLancamentoRepository repository;
    private CaixaLancamentoService caixaLancamentos;
    private UsuarioService usuarios;

    @Before
    public void setUp() {

        service = new CartaoLancamentoService();

        repository = mock(CartaoLancamentoRepository.class);
        caixaLancamentos = mock(CaixaLancamentoService.class);
        usuarios = mock(UsuarioService.class);

        ReflectionTestUtils.setField(
                service,
                "repository",
                repository
        );

        ReflectionTestUtils.setField(
                service,
                "caixaLancamentos",
                caixaLancamentos
        );

        ReflectionTestUtils.setField(
                service,
                "usuarios",
                usuarios
        );

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "gerente",
                        "123"
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);
    }

    @After
    public void tearDown() {

        SecurityContextHolder.clearContext();
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

        ArgumentCaptor<CartaoLancamento> captor =
                ArgumentCaptor.forClass(CartaoLancamento.class);

        verify(repository).save(captor.capture());

        CartaoLancamento salvo = captor.getValue();

        // Valor original da parcela
        assertEquals(
                100.0,
                salvo.getVlParcela(),
                0.001
        );

        // Taxa de débito: 2%
        assertEquals(
                2.0,
                salvo.getTaxa(),
                0.001
        );

        // 2% de R$ 100 = R$ 2
        assertEquals(
                2.0,
                salvo.getVlTaxa(),
                0.001
        );

        // R$ 100 - R$ 2 = R$ 98
        assertEquals(
                98.0,
                salvo.getVlLiqParcela(),
                0.001
        );

        // Taxa de antecipação: 1%
        assertEquals(
                1.0,
                salvo.getTaxaAntecipacao(),
                0.001
        );

        // 1% de R$ 100 = R$ 1
        assertEquals(
                1.0,
                salvo.getVlTaxaAntecipacao(),
                0.001
        );

        // R$ 100 - R$ 1 = R$ 99
        assertEquals(
                99.0,
                salvo.getVlLiqAntecipacao(),
                0.001
        );

        assertEquals(
                CartaoTipo.DEBITO,
                salvo.getTipo()
        );

        assertEquals(
                CartaoSituacao.APROCESSAR,
                salvo.getSituacao()
        );

        assertNotNull(
                salvo.getData_recebimento()
        );

        assertNotNull(
                salvo.getData_cadastro()
        );
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

        ArgumentCaptor<CartaoLancamento> captor =
                ArgumentCaptor.forClass(CartaoLancamento.class);

        verify(repository).save(captor.capture());

        CartaoLancamento salvo = captor.getValue();

        // Valor original da parcela
        assertEquals(
                100.0,
                salvo.getVlParcela(),
                0.001
        );

        // Taxa de crédito: 3%
        assertEquals(
                3.0,
                salvo.getTaxa(),
                0.001
        );

        // 3% de R$ 100 = R$ 3
        assertEquals(
                3.0,
                salvo.getVlTaxa(),
                0.001
        );

        // R$ 100 - R$ 3 = R$ 97
        assertEquals(
                97.0,
                salvo.getVlLiqParcela(),
                0.001
        );

        // Taxa de antecipação: 1,5%
        assertEquals(
                1.5,
                salvo.getTaxaAntecipacao(),
                0.001
        );

        // 1,5% de R$ 100 = R$ 1,50
        assertEquals(
                1.5,
                salvo.getVlTaxaAntecipacao(),
                0.001
        );

        // R$ 100 - R$ 1,50 = R$ 98,50
        assertEquals(
                98.5,
                salvo.getVlLiqAntecipacao(),
                0.001
        );

        assertEquals(
                CartaoTipo.CREDITO,
                salvo.getTipo()
        );

        assertEquals(
                CartaoSituacao.APROCESSAR,
                salvo.getSituacao()
        );

        assertNotNull(
                salvo.getData_recebimento()
        );

        assertNotNull(
                salvo.getData_cadastro()
        );
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
    // NOVOS TESTES DA ENTREGA 2 - processar()
    // =========================================================

    @Test
    public void deveProcessarCartaoComSucesso() {

        CartaoLancamento cartao = mock(
                CartaoLancamento.class,
                Mockito.RETURNS_DEEP_STUBS
        );

        Usuario usuario = mock(Usuario.class);
        Caixa banco = mock(Caixa.class);

        when(cartao.getSituacao())
                .thenReturn(CartaoSituacao.APROCESSAR);

        when(cartao.getVlLiqParcela())
                .thenReturn(98.0);

        when(cartao.getMaquina_cartao().getBanco())
                .thenReturn(banco);

        when(usuarios.buscaUsuario(any()))
                .thenReturn(usuario);

        String resultado = service.processar(cartao);

        assertEquals(
                "Processamento realizado com sucesso",
                resultado
        );

        verify(caixaLancamentos)
                .lancamento(any(CaixaLancamento.class));

        verify(cartao)
                .setSituacao(CartaoSituacao.PROCESSADO);
    }

    @Test
    public void deveLancarErroAoProcessarQuandoLancamentoNoCaixaFalhar() {

        CartaoLancamento cartao = mock(
                CartaoLancamento.class,
                Mockito.RETURNS_DEEP_STUBS
        );

        Usuario usuario = mock(Usuario.class);
        Caixa banco = mock(Caixa.class);

        when(cartao.getSituacao())
                .thenReturn(CartaoSituacao.APROCESSAR);

        when(cartao.getVlLiqParcela())
                .thenReturn(98.0);

        when(cartao.getMaquina_cartao().getBanco())
                .thenReturn(banco);

        when(usuarios.buscaUsuario(any()))
                .thenReturn(usuario);

        doThrow(new RuntimeException("Falha simulada no caixa"))
                .when(caixaLancamentos)
                .lancamento(any(CaixaLancamento.class));

        try {

            service.processar(cartao);

            fail("Era esperada uma RuntimeException");

        } catch (RuntimeException e) {

            assertEquals(
                    "Erro ao tentar realizar o processamento, chame o suporte",
                    e.getMessage()
            );
        }
    }

    @Test
    public void deveLancarErroAoProcessarQuandoAlteracaoDeSituacaoFalhar() {

        CartaoLancamento cartao = mock(
                CartaoLancamento.class,
                Mockito.RETURNS_DEEP_STUBS
        );

        Usuario usuario = mock(Usuario.class);
        Caixa banco = mock(Caixa.class);

        when(cartao.getSituacao())
                .thenReturn(CartaoSituacao.APROCESSAR);

        when(cartao.getVlLiqParcela())
                .thenReturn(98.0);

        when(cartao.getMaquina_cartao().getBanco())
                .thenReturn(banco);

        when(usuarios.buscaUsuario(any()))
                .thenReturn(usuario);

        doThrow(new RuntimeException("Falha simulada"))
                .when(cartao)
                .setSituacao(CartaoSituacao.PROCESSADO);

        try {

            service.processar(cartao);

            fail("Era esperada uma RuntimeException");

        } catch (RuntimeException e) {

            assertEquals(
                    "Erro ao tentar realizar o processamento, chame o suporte",
                    e.getMessage()
            );
        }

        verify(caixaLancamentos)
                .lancamento(any(CaixaLancamento.class));
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

    // =========================================================
    // NOVOS TESTES DA ENTREGA 2 - antecipar()
    // =========================================================

    @Test
    public void deveAnteciparCartaoComSucesso() {

        CartaoLancamento cartao = mock(
                CartaoLancamento.class,
                Mockito.RETURNS_DEEP_STUBS
        );

        Usuario usuario = mock(Usuario.class);
        Caixa banco = mock(Caixa.class);

        when(cartao.getSituacao())
                .thenReturn(CartaoSituacao.APROCESSAR);

        when(cartao.getVlLiqAntecipacao())
                .thenReturn(98.5);

        when(cartao.getMaquina_cartao().getBanco())
                .thenReturn(banco);

        when(cartao.getCodigo())
                .thenReturn(1L);

        when(usuarios.buscaUsuario(any()))
                .thenReturn(usuario);

        String resultado = service.antecipar(cartao);

        assertEquals(
                "Antecipação realizada com sucesso",
                resultado
        );

        verify(caixaLancamentos)
                .lancamento(any(CaixaLancamento.class));

        verify(cartao)
                .setSituacao(CartaoSituacao.ANTECIPADO);

        verify(repository)
                .save(cartao);
    }

    @Test
    public void deveLancarErroAoAnteciparQuandoLancamentoNoCaixaFalhar() {

        CartaoLancamento cartao = mock(
                CartaoLancamento.class,
                Mockito.RETURNS_DEEP_STUBS
        );

        Usuario usuario = mock(Usuario.class);
        Caixa banco = mock(Caixa.class);

        when(cartao.getSituacao())
                .thenReturn(CartaoSituacao.APROCESSAR);

        when(cartao.getVlLiqAntecipacao())
                .thenReturn(98.5);

        when(cartao.getMaquina_cartao().getBanco())
                .thenReturn(banco);

        when(cartao.getCodigo())
                .thenReturn(1L);

        when(usuarios.buscaUsuario(any()))
                .thenReturn(usuario);

        doThrow(new RuntimeException("Falha simulada no caixa"))
                .when(caixaLancamentos)
                .lancamento(any(CaixaLancamento.class));

        try {

            service.antecipar(cartao);

            fail("Era esperada uma RuntimeException");

        } catch (RuntimeException e) {

            assertEquals(
                    "Erro ao tentar realizar a antecipação, chame o suporte",
                    e.getMessage()
            );
        }
    }

    @Test
    public void deveLancarErroAoAnteciparQuandoPersistenciaFalhar() {

        CartaoLancamento cartao = mock(
                CartaoLancamento.class,
                Mockito.RETURNS_DEEP_STUBS
        );

        Usuario usuario = mock(Usuario.class);
        Caixa banco = mock(Caixa.class);

        when(cartao.getSituacao())
                .thenReturn(CartaoSituacao.APROCESSAR);

        when(cartao.getVlLiqAntecipacao())
                .thenReturn(98.5);

        when(cartao.getMaquina_cartao().getBanco())
                .thenReturn(banco);

        when(cartao.getCodigo())
                .thenReturn(1L);

        when(usuarios.buscaUsuario(any()))
                .thenReturn(usuario);

        doThrow(new RuntimeException("Falha simulada no repository"))
                .when(repository)
                .save(cartao);

        try {

            service.antecipar(cartao);

            fail("Era esperada uma RuntimeException");

        } catch (RuntimeException e) {

            assertEquals(
                    "Erro ao tentar realizar a antecipação, chame o suporte",
                    e.getMessage()
            );
        }

        verify(cartao)
                .setSituacao(CartaoSituacao.ANTECIPADO);

        verify(repository)
                .save(cartao);
    }
}