package net.originmobi.pdv.selenium.page;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PessoaPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public PessoaPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(
                driver,
                10
        );
    }

    public void abrir() {
        driver.get(
                "http://localhost:8080/pessoa/form"
        );

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("nome"))
        );
    }

    // A data de nascimento é obrigatória no servidor: vazia, o cadastro falha com "Unparseable date".
    // Defeito conhecido: o datepicker (yyyy/mm/dd) e o servidor (dd/MM/yyyy) usam formatos
    // diferentes, então a data é gravada incorreta. Aqui ela é apenas pré-condição do cadastro.
    public void preencherDadosPessoais(
            String nome,
            String cpf,
            String dataNascimento
    ) {
        driver.findElement(By.id("nome")).sendKeys(nome);
        driver.findElement(By.id("cpfcnpj")).sendKeys(cpf);
        driver.findElement(By.id("nascimento")).sendKeys(dataNascimento);
    }

    // O campo "Numero" aceita no máximo 6 caracteres (VARCHAR(6), issue #6)
    public void preencherEndereco(
            String rua,
            String bairro,
            String numero
    ) {
        abrirAba("Endereço");

        new Select(
                driver.findElement(By.id("cidade"))
        ).selectByIndex(0);

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("rua"))
        ).sendKeys(rua);

        driver.findElement(By.id("bairro")).sendKeys(bairro);
        driver.findElement(By.id("numero")).sendKeys(numero);
    }

    public void preencherTelefone(String telefone) {
        abrirAba("Contato");

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("fone"))
        ).sendKeys(telefone);
    }

    public void salvar() {
        driver.findElement(
                By.cssSelector("#form_pessoa input[type='submit']")
        ).click();
    }

    // O cadastro de pessoa é enviado via AJAX e o resultado aparece em um alert
    public String obterMensagemResultado() {

        Alert alerta = wait.until(
                ExpectedConditions.alertIsPresent()
        );

        String mensagem = alerta.getText();
        alerta.accept();

        return mensagem;
    }

    private void abrirAba(String nomeAba) {
        driver.findElement(
                By.xpath("//ul[contains(@class,'nav-tabs')]//a[normalize-space()='" + nomeAba + "']")
        ).click();
    }
}
