package net.originmobi.pdv.selenium.page;

import java.util.List;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class NotaFiscalPage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String baseUrl;

    public NotaFiscalPage(WebDriver driver, String baseUrl) {
        this.driver = driver;
        this.baseUrl = baseUrl;
        this.wait = new WebDriverWait(driver, 15);
    }

    public void abrirFormulario() {
        driver.get(baseUrl + "/notafiscal/form");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_notafiscal")));
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-cria-nota")));
    }

    public void preencherNatureza(String natureza) {
        WebElement campo = driver.findElement(By.name("natureza_operacao"));
        campo.clear();
        campo.sendKeys(natureza);
    }

    public void selecionarSaida() {
        new Select(driver.findElement(By.name("tipo"))).selectByValue("SAIDA");
    }

    public String selecionarDestinatario(String codigo) {
        // Interage com o controle visível do Bootstrap Select, sem alterar o DOM via JavaScript.
        Select select = new Select(driver.findElement(By.name("destinatario")));
        List<WebElement> opcoes = select.getOptions();
        for (int i = 0; i < opcoes.size(); i++) {
            if (codigo.equals(opcoes.get(i).getAttribute("value"))) {
                String nome = opcoes.get(i).getText();
                wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[data-id='destinatario']"))).click();
                wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(
                        ".bootstrap-select.open li[data-original-index='" + i + "'] a"))).click();
                return nome;
            }
        }
        throw new IllegalStateException("Pré-condição ausente: destinatário " + codigo);
    }

    public void criar() {
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-cria-nota"))).click();
    }

    public String aguardarCadastro() {
        wait.until(ExpectedConditions.urlMatches(".*/notafiscal/[0-9]+$"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".btn-emitir-nfe")));
        return driver.getCurrentUrl().substring(driver.getCurrentUrl().lastIndexOf('/') + 1);
    }

    public String consumirAlerta() {
        Alert alerta = wait.until(ExpectedConditions.alertIsPresent());
        String mensagem = alerta.getText();
        alerta.accept();
        return mensagem;
    }

    public String valor(String nome) {
        return driver.findElement(By.name(nome)).getAttribute("value");
    }

    public String destinatarioSelecionado() {
        return new Select(driver.findElement(By.name("destinatario"))).getFirstSelectedOption().getText();
    }

    public int contarNotas() {
        driver.get(baseUrl + "/notafiscal");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table.table")));
        return driver.findElements(By.cssSelector("table.table tbody tr")).size();
    }

    public List<WebElement> linhaDaNota(String codigo) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table.table")));
        WebElement link = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("table a[href$='/notafiscal/" + codigo + "']")));
        return link.findElement(By.xpath("ancestor::tr")).findElements(By.tagName("td"));
    }

    public void reabrirPelaLista(String codigo) {
        driver.findElement(By.cssSelector("table a[href$='/notafiscal/" + codigo + "']")).click();
        aguardarCadastro();
    }
}
