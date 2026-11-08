package net.originmobi.pdv.selenium;

import static org.junit.jupiter.api.Assertions.*;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

@DisplayName("Requisitos funcionais de nota fiscal pelo navegador")
public class NotaFiscalSeleniumTest extends NotaFiscalSeleniumSupport {
    @Test
    @DisplayName("RF-NF-01: cadastra nota de saída e consulta os dados pela listagem")
    void deveCadastrarEConsultarNotaDeSaida() {
        logar();
        int antes = notaPage.contarNotas();
        notaPage.abrirFormulario();
        String natureza = "Venda Selenium " + UUID.randomUUID();
        notaPage.preencherNatureza(natureza);
        notaPage.selecionarSaida();
        String destinatario = notaPage.selecionarDestinatario(System.getProperty("selenium.destinatario", "1"));
        notaPage.criar();
        String codigo = notaPage.aguardarCadastro();
        String numero = notaPage.valor("numero");
        String serie = notaPage.valor("serie");
        assertTrue(Long.parseLong(numero) > 0);
        assertTrue(Integer.parseInt(serie) > 0);
        assertEquals("55", notaPage.valor("modelo"));
        assertEquals(antes + 1, notaPage.contarNotas());
        List<WebElement> linha = notaPage.linhaDaNota(codigo);
        assertEquals(numero, linha.get(0).getText());
        assertEquals("55", linha.get(1).getText());
        assertEquals(serie, linha.get(2).getText());
        assertEquals("SAIDA", linha.get(3).getText());
        notaPage.reabrirPelaLista(codigo);
        assertEquals(natureza, notaPage.valor("natureza_operacao"));
        assertEquals(destinatario, notaPage.destinatarioSelecionado());
        assertEquals("SAIDA", notaPage.valor("tipo"));
    }

    @Test
    @DisplayName("RF-NF-02: rejeita natureza vazia sem criar nota")
    void deveRejeitarNaturezaVazia() {
        logar();
        int antes = notaPage.contarNotas();
        notaPage.abrirFormulario();
        notaPage.selecionarDestinatario(System.getProperty("selenium.destinatario", "1"));
        notaPage.criar();
        assertEquals("Favor, informe a natureza da operação", notaPage.consumirAlerta());
        assertEquals("/notafiscal/form", URI.create(driver.getCurrentUrl()).getPath());
        assertEquals(antes, notaPage.contarNotas());
    }

    @Test
    @DisplayName("RF-NF-03: rejeita destinatário vazio sem criar nota")
    void deveRejeitarDestinatarioVazio() {
        logar();
        int antes = notaPage.contarNotas();
        notaPage.abrirFormulario();
        notaPage.preencherNatureza("Venda sem destinatário");
        notaPage.criar();
        assertEquals("Favor, informe o destinatário", notaPage.consumirAlerta());
        assertEquals("/notafiscal/form", URI.create(driver.getCurrentUrl()).getPath());
        assertEquals(antes, notaPage.contarNotas());
    }
}
