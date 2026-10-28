package net.originmobi.pdv.selenium;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import net.originmobi.pdv.selenium.page.LoginPage;
import net.originmobi.pdv.selenium.page.PessoaPage;
import net.originmobi.pdv.selenium.page.UsuarioPage;

public class UsuarioSeleniumTest {

    private WebDriver driver;

    private LoginPage loginPage;
    private PessoaPage pessoaPage;
    private UsuarioPage usuarioPage;

    @Before
    public void setUp() {
        System.setProperty(
                "webdriver.chrome.driver",
                "drivers/chromedriver.exe"
        );

        driver = new ChromeDriver();
        loginPage = new LoginPage(driver);
        pessoaPage = new PessoaPage(driver);
        usuarioPage = new UsuarioPage(driver);
    }

    @After
    public void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void deveCadastrarUsuarioComSucesso() {

        // Sufixo para gerar dados únicos a cada execução:
        // o sistema não aceita username nem CPF repetidos
        String sufixo = String.valueOf(System.currentTimeMillis()).substring(4);

        String nomePessoa = "Pessoa Selenium " + sufixo;
        String usuario = "sel" + sufixo;

        // Login
        loginPage.abrir();

        loginPage.logar(
                "gerente",
                "123"
        );

        // Aguarda o login terminar antes de navegar; sem isso, o driver.get
        // interrompe o envio do login e o sistema volta para a tela de login
        new WebDriverWait(driver, 10).until(
                ExpectedConditions.not(
                        ExpectedConditions.urlContains("/login")
                )
        );

        // Pré-condição: o usuário precisa de uma pessoa ainda não vinculada
        pessoaPage.abrir();

        pessoaPage.preencherDadosPessoais(
                nomePessoa,
                "10" + sufixo,
                "01/01/1990"
        );

        pessoaPage.preencherEndereco(
                "Rua dos Testes",
                "Centro",
                "123"
        );

        pessoaPage.preencherTelefone("11999999999");

        pessoaPage.salvar();

        assertEquals(
                "Pessoa salva com sucesso",
                pessoaPage.obterMensagemResultado()
        );

        // Cadastro do usuário
        usuarioPage.abrirFormulario();

        usuarioPage.preencher(
                usuario,
                "123456",
                nomePessoa
        );

        usuarioPage.salvar();

        assertEquals(
                "Usuário salvo com sucesso",
                usuarioPage.obterMensagemSucesso()
        );

        // Verifica o usuário na listagem, vinculado à pessoa criada
        usuarioPage.abrirLista();

        assertEquals(
                nomePessoa,
                usuarioPage.obterPessoaDoUsuarioNaLista(usuario)
        );
    }
}
