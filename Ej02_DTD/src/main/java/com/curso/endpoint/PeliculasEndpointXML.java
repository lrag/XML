package com.curso.endpoint;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.curso.endpoint.dto.PeliculaDTO;
import com.curso.endpoint.dto.Respuesta;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@RestController
public class PeliculasEndpointXML {

	@GetMapping(
			path = "/peliculas", 
			produces = { MediaType.APPLICATION_XML_VALUE }
		)
	public ResponseEntity<Respuesta<?>> listarPeliculas() throws IOException{
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("XML/peliculas.xml");
		XmlMapper xmlMapper = new XmlMapper();
		Map<String, Object> datosXml = xmlMapper.readValue(inputStream, Map.class);
		Map<String, Map<String, Object>> peliculas = new HashMap<>();
		peliculas.put("peliculas", datosXml);
	    Respuesta<Map<String, ?>> respuesta = Respuesta.success(peliculas, "Listado de películas");
	    return ResponseEntity.ok(respuesta);		
	}
	
	/*
	@PostMapping(
			path = "/peliculas", 
			consumes = { MediaType.APPLICATION_XML_VALUE }
		)
	public ResponseEntity<String> insertarPelicula(@RequestBody String xmlString) {        
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            
            //Desactivamos la validación automática tradicional por DOCTYPE
            factory.setValidating(false); 
            factory.setNamespaceAware(true);

            //Añadimos el DTD en el servidor
            String dtdUrl = new ClassPathResource("xml/peliculas.dtd").getURL().toExternalForm();
            factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/XML/1998/namespace");
            factory.setAttribute("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", dtdUrl);

            DocumentBuilder builder = factory.newDocumentBuilder();

            //Error handler
            builder.setErrorHandler(new org.xml.sax.ErrorHandler() {
                @Override
                public void warning(org.xml.sax.SAXParseException e) { throw new RuntimeException("WTF"); }
                @Override
                public void error(org.xml.sax.SAXParseException e) throws SAXException { throw e; }
                @Override
                public void fatalError(org.xml.sax.SAXParseException e) throws SAXException { throw e; }
            });

            //Analizamos el XML en crudo
            builder.parse(new InputSource(new StringReader(xmlString)));

            XmlMapper xmlMapper = new XmlMapper();
            PeliculaDTO peliculaDTO = xmlMapper.readValue(xmlString, PeliculaDTO.class);
            
            System.out.println("Película: " + peliculaDTO);
            return new ResponseEntity<>("Película insertada correctamente", HttpStatus.CREATED);

        } catch (SAXException e) {
            return new ResponseEntity<>("Error de validación (DTD): " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    */
	
	@PostMapping(
		    path = "/peliculas", 
		    consumes = { MediaType.APPLICATION_XML_VALUE }
		)
	public ResponseEntity<String> insertarPelicula(@RequestBody String xmlString) {        
	    try {
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        factory.setValidating(true); // Activamos validación DTD
	        factory.setNamespaceAware(true);

	        //Protección contra XXE y ataques de denegación de servicio
	        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
	        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
	        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

	        DocumentBuilder builder = factory.newDocumentBuilder();

	        //EntityResolver
	        builder.setEntityResolver((publicId, systemId) -> {
	        	//DE aquí y solo de aquí
	            return new InputSource(new ClassPathResource("xml/peliculas.dtd").getInputStream());
	        });

	        //ErrorHandler
	        builder.setErrorHandler(new org.xml.sax.ErrorHandler() {
	            @Override
	            public void warning(org.xml.sax.SAXParseException e) {}
	            @Override
	            public void error(org.xml.sax.SAXParseException e) throws SAXException { throw e; }
	            @Override
	            public void fatalError(org.xml.sax.SAXParseException e) throws SAXException { throw e; }
	        });

	        //InputStream que "lee" <!DOCTYPE pelicula SYSTEM "peliculas.dtd">
	        InputStream doctypeStream = new ByteArrayInputStream(
	            "<!DOCTYPE pelicula SYSTEM \"peliculas.dtd\">".getBytes(java.nio.charset.StandardCharsets.UTF_8)
	        );
	        //InputStream que lee el body
	        InputStream xmlStream = new ByteArrayInputStream(xmlString.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	        
	        //Combinamos los dos streams
	        InputStream streamCombinado = new SequenceInputStream(doctypeStream, xmlStream);

	        //Validación
	        builder.parse(streamCombinado);

	        //Si no se ha lancazo excepción creamos el objeto
	        XmlMapper xmlMapper = new XmlMapper();
	        PeliculaDTO peliculaDTO = xmlMapper.readValue(xmlString, PeliculaDTO.class);
	        
	        System.out.println("Insertar película: " + peliculaDTO);
	        return new ResponseEntity<>("Película insertada correctamente", HttpStatus.CREATED);

	    } catch (SAXException e) {
	        return new ResponseEntity<>("Error de validación (DTD): " + e.getMessage(), HttpStatus.BAD_REQUEST);
	    } catch (Exception e) {
	        return new ResponseEntity<>("Error interno: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}	
	
}	
	
