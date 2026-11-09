package net.originmobi.pdv.selenium;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import net.originmobi.pdv.selenium.page.LoginPage;
import net.originmobi.pdv.selenium.page.RecebimentoPage;

public class RecebimentoSeleniumTest {

    // Banco do docker-compose do projeto
    private static final String URL_BANCO = "jdbc:mysql://localhost:3306/pdv?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String USUARIO_BANCO = "root";
    private static final String SENHA_BANCO = "123456";

    private static final Long CLIENTE = 1L;
    private static final String TITULO = "Dinheiro";

    private WebDriver driver;

    private LoginPage loginPage;
    private RecebimentoPage recebimentoPage;

    private Long parcela;

    @Before
    public void setUp() throws Exception {
        // Pré-condição: cliente com uma parcela de R$ 100,00 em aberto.
        // A parcela é criada direto no banco porque a venda a prazo pela
        // tela está com defeito (ver TestLink PDVQA-1).
        parcela = criarParcelaEmAberto(100.0);

        System.setProperty(
                "webdriver.chrome.driver",
                System.getProperty("webdriver.chrome.driver", "drivers/chromedriver.exe")
        );

        driver = new ChromeDriver();
        loginPage = new LoginPage(driver);
        recebimentoPage = new RecebimentoPage(driver);

        // Login
        loginPage.abrir();
        loginPage.logar("gerente", "123");

        new WebDriverWait(driver, 10).until(
                ExpectedConditions.not(
                        ExpectedConditions.urlContains("/login")
                )
        );
    }

    @After
    public void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void deveReceberTituloComSucesso() {

        recebimentoPage.abrirParcelasDoCliente(CLIENTE);
        recebimentoPage.marcarParcela(parcela);
        recebimentoPage.abrirRecebimento();

        recebimentoPage.selecionarTitulo(TITULO);
        recebimentoPage.preencherValorRecebido("100,00");

        assertEquals(
                "Recebimento realizado com sucesso",
                recebimentoPage.receber()
        );
    }

    @Test
    public void deveRecusarValorMaiorQueOTitulo() {

        recebimentoPage.abrirParcelasDoCliente(CLIENTE);
        recebimentoPage.marcarParcela(parcela);
        recebimentoPage.abrirRecebimento();

        recebimentoPage.selecionarTitulo(TITULO);
        recebimentoPage.preencherValorRecebido("150,00");

        assertEquals(
                "Valor de recebimento é superior aos títulos",
                recebimentoPage.receber()
        );
    }

    // ===================== Preparação dos dados =====================

    private Long criarParcelaEmAberto(Double valor) throws Exception {

        try (Connection banco = DriverManager.getConnection(URL_BANCO, USUARIO_BANCO, SENHA_BANCO)) {

            // Título "Dinheiro" para aparecer na tela de recebimento
            if (!existe(banco, "select 1 from titulo where descricao = '" + TITULO + "'")) {
                inserir(banco, "insert into titulo (descricao, tipo_codigo) values ('" + TITULO + "', 1)");
            }

            // Caixa aberto, exigido para lançar o recebimento em dinheiro
            if (!existe(banco, "select 1 from caixa where tipo = 'CAIXA' and data_fechamento is null")) {
                inserir(banco, "insert into caixa (descricao, valor_abertura, valor_total, tipo, usuario_codigo, data_cadastro) "
                        + "values ('Caixa Selenium', 0, 0, 'CAIXA', 1, curdate())");
            }

            // Venda a prazo -> receber -> parcela em aberto
            Long venda = inserir(banco, "insert into venda (observacao, valor_total, situacao, pessoa_codigo, usuario_codigo) "
                    + "values ('Venda Selenium', " + valor + ", 'FECHADA', " + CLIENTE + ", 1)");

            Long receber = inserir(banco, "insert into receber (observacao, valor_total, pessoa_codigo, data_cadastro, venda_codigo) "
                    + "values ('Receber Selenium', " + valor + ", " + CLIENTE + ", now(), " + venda + ")");

            return inserir(banco, "insert into parcela (valor_total, valor_desconto, valor_acrescimo, valor_recebido, "
                    + "valor_restante, data_cadastro, data_alteracao, data_vencimento, receber_codigo, quitado, sequencia) "
                    + "values (" + valor + ", 0, 0, 0, " + valor + ", now(), now(), date_add(now(), interval 30 day), "
                    + receber + ", 0, 1)");
        }
    }

    private boolean existe(Connection banco, String sql) throws Exception {
        try (Statement st = banco.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            return rs.next();
        }
    }

    // Executa o insert e devolve o código gerado
    private Long inserir(Connection banco, String sql) throws Exception {
        try (PreparedStatement st = banco.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.executeUpdate();
            ResultSet chave = st.getGeneratedKeys();
            chave.next();
            return chave.getLong(1);
        }
    }
}
