package net.originmobi.pdv.selenium.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage {

    private WebDriver driver;

    @FindBy(id = "user")
    private WebElement campoUsuario;

    @FindBy(id = "password")
    private WebElement campoSenha;

    @FindBy(id = "btn-login")
    private WebElement botaoEntrar;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void abrir() {
        driver.get("http://localhost:8080/login");
    }

    public void preencherUsuario(String usuario) {
        campoUsuario.sendKeys(usuario);
    }

    public void preencherSenha(String senha) {
        campoSenha.sendKeys(senha);
    }

    public void clicarEntrar() {
        botaoEntrar.click();
    }

    public void logar(String usuario, String senha) {
        preencherUsuario(usuario);
        preencherSenha(senha);
        clicarEntrar();
    }
}