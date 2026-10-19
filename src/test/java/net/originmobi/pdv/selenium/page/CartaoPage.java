package net.originmobi.pdv.selenium.page;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CartaoPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public CartaoPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(
                driver,
                10
        );
    }

    public void abrir() {
        driver.get(
                "http://localhost:8080/cartaolancamentos"
        );
    }

    public WebElement localizarBotaoAnteciparDisponivel() {

        By botao = By.xpath(
                "//a[contains(@class,'btn-antecipar-cartao')]" +
                "[span[normalize-space()='Antecipar']]"
        );

        return wait.until(
                ExpectedConditions.elementToBeClickable(botao)
        );
    }

    public String obterCodigoLancamento(
            WebElement botaoAntecipar
    ) {
        return botaoAntecipar.getAttribute(
                "data-codlancamento"
        );
    }

    public void clicarAntecipar(
            WebElement botaoAntecipar
    ) {
        botaoAntecipar.click();
    }

    public String obterMensagemConfirmacao() {

        Alert alerta = wait.until(
                ExpectedConditions.alertIsPresent()
        );

        return alerta.getText();
    }

    public void aceitarConfirmacao() {

        Alert alerta = driver
                .switchTo()
                .alert();

        alerta.accept();
    }

    public String obterMensagemResultado() {

        Alert alerta = wait.until(
                ExpectedConditions.alertIsPresent()
        );

        return alerta.getText();
    }

    public void aceitarMensagemResultado() {

        driver.switchTo()
                .alert()
                .accept();
    }

    public void aguardarLancamentoAntecipado(
            String codigo
    ) {

        By situacao = By.cssSelector(
                "a.btn-antecipar-cartao" +
                "[data-codlancamento='" +
                codigo +
                "'] span"
        );

        wait.until(
                ExpectedConditions
                        .textToBePresentInElementLocated(
                                situacao,
                                "Antecipado"
                        )
        );
    }

    public String obterSituacaoLancamento(
            String codigo
    ) {

        By situacao = By.cssSelector(
                "a.btn-antecipar-cartao" +
                "[data-codlancamento='" +
                codigo +
                "'] span"
        );

        return driver
                .findElement(situacao)
                .getText();
    }

     public void aguardarTabelaCarregada() {
        By tabela = By.cssSelector(".table-dados-cartoes");

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(tabela)
        );
     }
}

