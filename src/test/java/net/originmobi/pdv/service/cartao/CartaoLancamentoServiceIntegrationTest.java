package net.originmobi.pdv.service.cartao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import net.originmobi.pdv.enumerado.caixa.EstiloLancamento;
import net.originmobi.pdv.enumerado.caixa.TipoLancamento;
import net.originmobi.pdv.enumerado.cartao.CartaoSituacao;
import net.originmobi.pdv.model.Caixa;
import net.originmobi.pdv.model.CaixaLancamento;
import net.originmobi.pdv.model.Usuario;
import net.originmobi.pdv.model.cartao.CartaoLancamento;
import net.originmobi.pdv.repository.CaixaLancamentoRepository;
import net.originmobi.pdv.repository.cartao.CartaoLancamentoRepository;
import net.originmobi.pdv.service.CaixaLancamentoService;
import net.originmobi.pdv.service.UsuarioService;

public class CartaoLancamentoServiceIntegrationTest {

    private CartaoLancamentoService cartaoService;
    private CaixaLancamentoService caixaService;

    private CartaoLancamentoRepository cartaoRepository;
    private CaixaLancamentoRepository caixaRepository;
    private UsuarioService usuarios;

    @Before
    public void setUp() {

        // Duas unidades reais que serão integradas
        cartaoService = new CartaoLancamentoService();
        caixaService = new CaixaLancamentoService();

        // Dublês para dependências externas
        cartaoRepository =
                mock(CartaoLancamentoRepository.class);

        caixaRepository =
                mock(CaixaLancamentoRepository.class);

        usuarios =
                mock(UsuarioService.class);

        // CartaoLancamentoService -> CaixaLancamentoService real
        ReflectionTestUtils.setField(
                cartaoService,
                "repository",
                cartaoRepository
        );

        ReflectionTestUtils.setField(
                cartaoService,
                "caixaLancamentos",
                caixaService
        );

        ReflectionTestUtils.setField(
                cartaoService,
                "usuarios",
                usuarios
        );

        // CaixaLancamentoService -> repository simulado
        ReflectionTestUtils.setField(
                caixaService,
                "caixaLancamento",
                caixaRepository
        );

        ReflectionTestUtils.setField(
                caixaService,
                "usuarios",
                usuarios
        );

        SecurityContextHolder.getContext()
                .setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                "gerente",
                                "123"
                        )
                );
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void deveIntegrarCartaoECaixaAoProcessarLancamento() {

        CartaoLancamento cartao =
                mock(
                        CartaoLancamento.class,
                        Mockito.RETURNS_DEEP_STUBS
                );

        Usuario usuario =
                mock(Usuario.class);

        Caixa banco =
                mock(Caixa.class);

        when(cartao.getSituacao())
                .thenReturn(
                        CartaoSituacao.APROCESSAR
                );

        when(cartao.getVlLiqParcela())
                .thenReturn(98.0);

        when(
                cartao.getMaquina_cartao()
                        .getBanco()
        ).thenReturn(banco);

        when(
                usuarios.buscaUsuario("gerente")
        ).thenReturn(usuario);

        String resultado =
                cartaoService.processar(cartao);

        assertEquals(
                "Processamento realizado com sucesso",
                resultado
        );

        verify(cartao)
                .setSituacao(
                        CartaoSituacao.PROCESSADO
                );

        ArgumentCaptor<CaixaLancamento> captor =
                ArgumentCaptor.forClass(
                        CaixaLancamento.class
                );

        verify(caixaRepository)
                .save(captor.capture());

        CaixaLancamento lancamento =
                captor.getValue();

        assertEquals(
                98.0,
                lancamento.getValor(),
                0.001
        );

        assertEquals(
                TipoLancamento.RECEBIMENTO,
                lancamento.getTipo()
        );

        assertEquals(
                EstiloLancamento.ENTRADA,
                lancamento.getEstilo()
        );

        assertEquals(
                usuario,
                lancamento.getUsuario()
        );

        assertNotNull(
                lancamento.getData_cadastro()
        );
    }

    @Test
    public void deveIntegrarCartaoECaixaAoAnteciparLancamento() {

        CartaoLancamento cartao =
                mock(
                        CartaoLancamento.class,
                        Mockito.RETURNS_DEEP_STUBS
                );

        Usuario usuario =
                mock(Usuario.class);

        Caixa banco =
                mock(Caixa.class);

        when(cartao.getSituacao())
                .thenReturn(
                        CartaoSituacao.APROCESSAR
                );

        when(cartao.getVlLiqAntecipacao())
                .thenReturn(99.0);

        when(
                cartao.getMaquina_cartao()
                        .getBanco()
        ).thenReturn(banco);

        when(cartao.getCodigo())
                .thenReturn(1L);

        when(
                usuarios.buscaUsuario("gerente")
        ).thenReturn(usuario);

        String resultado =
                cartaoService.antecipar(cartao);

        assertEquals(
                "Antecipação realizada com sucesso",
                resultado
        );

        verify(cartao)
                .setSituacao(
                        CartaoSituacao.ANTECIPADO
                );

        verify(cartaoRepository)
                .save(cartao);

        ArgumentCaptor<CaixaLancamento> captor =
                ArgumentCaptor.forClass(
                        CaixaLancamento.class
                );

        verify(caixaRepository)
                .save(captor.capture());

        CaixaLancamento lancamento =
                captor.getValue();

        assertEquals(
                99.0,
                lancamento.getValor(),
                0.001
        );

        assertEquals(
                TipoLancamento.RECEBIMENTO,
                lancamento.getTipo()
        );

        assertEquals(
                EstiloLancamento.ENTRADA,
                lancamento.getEstilo()
        );

        assertEquals(
                usuario,
                lancamento.getUsuario()
        );

        assertNotNull(
                lancamento.getData_cadastro()
        );
    }

    @Test
    public void devePropagarFalhaDaIntegracaoComCaixaAoProcessar() {

        CartaoLancamento cartao =
                mock(
                        CartaoLancamento.class,
                        Mockito.RETURNS_DEEP_STUBS
                );

        Usuario usuario =
                mock(Usuario.class);

        Caixa banco =
                mock(Caixa.class);

        when(cartao.getSituacao())
                .thenReturn(
                        CartaoSituacao.APROCESSAR
                );

        when(cartao.getVlLiqParcela())
                .thenReturn(98.0);

        when(
                cartao.getMaquina_cartao()
                        .getBanco()
        ).thenReturn(banco);

        when(
                usuarios.buscaUsuario("gerente")
        ).thenReturn(usuario);

        doThrow(
                new RuntimeException(
                        "Falha simulada no repository"
                )
        )
        .when(caixaRepository)
        .save(
                any(CaixaLancamento.class)
        );

        try {

            cartaoService.processar(cartao);

            fail(
                    "Era esperada uma RuntimeException"
            );

        } catch (RuntimeException e) {

            assertEquals(
                    "Erro ao tentar realizar o processamento, chame o suporte",
                    e.getMessage()
            );
        }
    }
}