package com.curso.controlador;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

@WebServlet("/SV_XLS_FO")
public class SV_XLS_FO extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private TransformerFactory tFactory = TransformerFactory.newInstance();	
	
	public SV_XLS_FO() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		ServletContext context = this.getServletContext();
		String rutaXSLT = context.getRealPath("/WEB-INF/plantillas/plantilla.xml");		
		String rutaXML  = context.getRealPath("/WEB-INF/xml/fichero.xml");		

		try {
			FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());

			// Setup a buffer to obtain the content length
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			// Setup FOP
			Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

			// Setup Transformer
			Source xsltSrc = new StreamSource(new File(rutaXSLT));
			Transformer transformer = tFactory.newTransformer(xsltSrc);

			// Make sure the XSL transformation's result is piped through to FOP
			Result res = new SAXResult(fop.getDefaultHandler());

			// Setup input
			Source src = new StreamSource(new File(rutaXML));

			// Start the transformation and rendering process
			transformer.transform(src, res);

			// Prepare response
			response.setContentType("application/pdf");
			response.setContentLength(out.size());

			// Send content to Browser
			response.getOutputStream().write(out.toByteArray());
			response.getOutputStream().flush();
		} catch (FOPException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

}
