package net.originmobi.pdv.selenium;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import net.originmobi.pdv.selenium.page.LoginPage;
import net.originmobi.pdv.selenium.page.NotaFiscalPage;

abstract class NotaFiscalSeleniumSupport {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected NotaFiscalPage notaPage;
    protected String baseUrl;

    @BeforeEach
    void abrirNavegador() {
        baseUrl = System.getProperty("selenium.baseUrl", "http://localhost:8080").replaceAll("/$", "");
        System.setProperty("webdriver.chrome.driver", System.getProperty("webdriver.chrome.driver", "drivers/chromedriver.exe"));
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1440,1000");
        if (Boolean.parseBoolean(System.getProperty("selenium.headless", "false"))) {
            options.addArguments("--headless=new");
        }
        driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        wait = new WebDriverWait(driver, 15);
        notaPage = new NotaFiscalPage(driver, baseUrl);
    }

    protected void logar() {
        driver.get(baseUrl + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user")));
        new LoginPage(driver).logar(System.getProperty("selenium.usuario", "gerente"),
                System.getProperty("selenium.senha", "123"));
        wait.until(d -> !URI.create(d.getCurrentUrl()).getPath().equals("/login"));
    }

    @AfterEach
    void registrarEFechar(TestInfo teste) throws Exception {
        if (driver == null) return;
        try {
            // Captura também em falhas; o nome identifica o cenário no relatório JUnit.
            Path pasta = Paths.get("target", "selenium-evidencias", "notafiscal");
            Files.createDirectories(pasta);
            String nome = (teste.getTestMethod().get().getName() + "-" + teste.getDisplayName())
                    .replaceAll("[^\\p{L}\\p{N}._-]", "_");
            Files.write(pasta.resolve(nome + ".png"),
                    ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
        } finally {
            driver.quit();
        }
    }
}
