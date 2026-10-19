package net.originmobi.pdv.selenium;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import net.originmobi.pdv.selenium.page.CartaoPage;
import net.originmobi.pdv.selenium.page.LoginPage;

public class CartaoDesempenhoSeleniumTest {

    private WebDriver driver;
    private LoginPage loginPage;
    private CartaoPage cartaoPage;

    @Before
    public void setUp() {
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
    public void deveCarregarTelaDeCartoesEmAteCincoSegundos() {

        loginPage.abrir();
        loginPage.logar("gerente", "123");

        long inicio = System.currentTimeMillis();

        cartaoPage.abrir();
        cartaoPage.aguardarTabelaCarregada();

        long fim = System.currentTimeMillis();

        long tempoCarregamento = fim - inicio;

        System.out.println(
                "Tempo de carregamento da tela de cartões: "
                + tempoCarregamento
                + " ms"
        );

        assertTrue(
                "A tela levou mais de 5 segundos para carregar",
                tempoCarregamento <= 5000
        );
    }
}