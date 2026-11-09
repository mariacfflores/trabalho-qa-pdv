package net.originmobi.pdv.selenium.page;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class RecebimentoPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // Tela /receber (lista de parcelas do cliente)
    @FindBy(css = ".btn-receber-varios")
    private WebElement botaoReceberParcelas;

    // Tela /recebimento/{codigo}
    @FindBy(id = "titulo")
    private WebElement campoTitulo;

    @FindBy(id = "vlrecebido")
    private WebElement campoValorRecebido;

    @FindBy(css = ".btn-receber-titulo")
    private WebElement botaoReceber;

    public RecebimentoPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, 10);
        PageFactory.initElements(driver, this);
    }

    // Abre a lista de parcelas em aberto do cliente
    // (mesmo resultado de escolher o cliente e clicar em "Buscar")
    public void abrirParcelasDoCliente(Long codigoCliente) {
        driver.get("http://localhost:8080/receber?codigo=" + codigoCliente + "&pago=false");
    }

    public void marcarParcela(Long codigoParcela) {
        driver.findElement(By.cssSelector("input.marcado[value='" + codigoParcela + "']")).click();
    }

    // Abre o recebimento das parcelas marcadas e espera a tela do recebimento
    public void abrirRecebimento() {
        botaoReceberParcelas.click();
        wait.until(ExpectedConditions.urlContains("/recebimento/"));
        wait.until(ExpectedConditions.visibilityOf(campoTitulo));
    }

    public void selecionarTitulo(String descricao) {
        new Select(campoTitulo).selectByVisibleText(descricao);
    }

    public void preencherValorRecebido(String valor) {
        campoValorRecebido.clear();
        campoValorRecebido.sendKeys(valor);
    }

    // Clica em Receber e devolve a mensagem do alerta exibido pelo sistema
    public String receber() {
        botaoReceber.click();

        Alert alerta = wait.until(ExpectedConditions.alertIsPresent());
        String mensagem = alerta.getText();
        alerta.accept();

        return mensagem;
    }
}
