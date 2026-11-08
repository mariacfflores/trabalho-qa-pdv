package net.originmobi.pdv.service.notafiscal;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.stereotype.Component;

import net.originmobi.pdv.xml.nfe.GeraXmlNfe;

@Component
public class RecursosXmlNfe {
	public String contexto() throws IOException {
		return new File(".").getCanonicalPath();
	}

	public PrintWriter abrirEscritor(String caminho) throws IOException {
		return new PrintWriter(new FileWriter(caminho));
	}

	public File arquivo(String caminho) {
		return new File(caminho);
	}

	public GeraXmlNfe criarGerador() {
		return new GeraXmlNfe();
	}
}
