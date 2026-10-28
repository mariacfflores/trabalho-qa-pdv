package net.originmobi.pdv.selenium.page;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class UsuarioPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public UsuarioPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(
                driver,
                10
        );
    }

    public void abrirFormulario() {
        driver.get(
                "http://localhost:8080/usuario/form"
        );
    }

    public void abrirLista() {
        driver.get(
                "http://localhost:8080/usuario"
        );
    }

    public void preencher(
            String usuario,
            String senha,
            String nomePessoa
    ) {
        driver.findElement(By.id("usuariovalor")).sendKeys(usuario);
        driver.findElement(By.id("senha")).sendKeys(senha);

        new Select(
                driver.findElement(By.id("pessoavalor"))
        ).selectByVisibleText(nomePessoa);
    }

    public void salvar() {
        driver.findElement(
                By.cssSelector("#usuariotab input[type='submit']")
        ).click();
    }

    public String obterMensagemSucesso() {

        WebElement mensagem = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".alert-success span")
                )
        );

        return mensagem.getText();
    }

    // Retorna o nome da pessoa vinculada ao usuário na listagem, ou null se não encontrar
    public String obterPessoaDoUsuarioNaLista(String usuario) {

        List<WebElement> linhas = driver.findElements(
                By.xpath("//table//tr[td[2][normalize-space()='" + usuario + "']]")
        );

        if (linhas.isEmpty()) {
            return null;
        }

        return linhas.get(0)
                .findElement(By.xpath("td[3]"))
                .getText();
    }
}
