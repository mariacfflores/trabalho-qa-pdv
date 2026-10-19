package net.originmobi.pdv.selenium;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import net.originmobi.pdv.selenium.page.CartaoPage;
import net.originmobi.pdv.selenium.page.LoginPage;

public class CartaoSeleniumTest {

    private WebDriver driver;

    private LoginPage loginPage;
    private CartaoPage cartaoPage;

    @Before
        public void setUp() {
        System.setProperty(
                "webdriver.chrome.driver",
                "drivers/chromedriver.exe"
        );

        driver = new ChromeDriver();
        loginPage = new LoginPage(driver);
        cartaoPage = new CartaoPage(driver);
    }

    @After
    public void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void deveAnteciparLancamentoDeCartaoComSucesso() {

        // Login
        loginPage.abrir();

        loginPage.logar(
                "gerente",
                "123"
        );

        // Tela de cartões
        cartaoPage.abrir();

        WebElement botaoAntecipar =
                cartaoPage
                        .localizarBotaoAnteciparDisponivel();

        String codigo =
                cartaoPage.obterCodigoLancamento(
                        botaoAntecipar
                );

        // Solicita antecipação
        cartaoPage.clicarAntecipar(
                botaoAntecipar
        );

        // Confirma a operação
        String mensagemConfirmacao =
                cartaoPage.obterMensagemConfirmacao();

        assertTrue(
                mensagemConfirmacao.contains(codigo)
        );

        cartaoPage.aceitarConfirmacao();

        // Verifica retorno do sistema
        String mensagemResultado =
                cartaoPage.obterMensagemResultado();

        assertEquals(
                "Antecipação realizada com sucesso",
                mensagemResultado
        );

        cartaoPage.aceitarMensagemResultado();

        // Aguarda atualização da tabela
        cartaoPage
                .aguardarLancamentoAntecipado(
                        codigo
                );

        assertEquals(
                "Antecipado",
                cartaoPage
                        .obterSituacaoLancamento(
                                codigo
                        )
        );
    }
}