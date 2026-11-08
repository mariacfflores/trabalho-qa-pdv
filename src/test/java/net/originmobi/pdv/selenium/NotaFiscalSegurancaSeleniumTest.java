package net.originmobi.pdv.selenium;

import static org.junit.jupiter.api.Assertions.*;
import java.net.URI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

@DisplayName("Requisito não funcional de segurança da nota fiscal")
public class NotaFiscalSegurancaSeleniumTest extends NotaFiscalSeleniumSupport {
    @DisplayName("RNF-NF-01: exige autenticação para acessar notas fiscais")
    @ParameterizedTest(name = "Acesso anônimo a {0}")
    @ValueSource(strings = {"/notafiscal", "/notafiscal/form"})
    void deveExigirLoginParaAcessarNotas(String caminho) {
        // Cada execução começa com um navegador novo, sem sessão autenticada.
        driver.get(baseUrl + caminho);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btn-login")));
        assertEquals("/login", URI.create(driver.getCurrentUrl()).getPath());
        assertTrue(driver.findElement(By.id("user")).isDisplayed());
        assertTrue(driver.findElement(By.id("password")).isDisplayed());
        assertTrue(driver.findElements(By.id("form_notafiscal")).isEmpty());
        assertTrue(driver.findElements(By.cssSelector("a[href='/notafiscal/form']")).isEmpty());
    }
}
