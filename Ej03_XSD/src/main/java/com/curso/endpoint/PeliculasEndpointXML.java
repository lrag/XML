package com.curso.endpoint;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.sax.SAXSource;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

import com.curso.endpoint.dto.PeliculaDTO;
import com.curso.endpoint.dto.Respuesta;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@RestController
@RequestMapping(path = "/peliculas")
public class PeliculasEndpointXML {

	@GetMapping(produces = { MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<Respuesta<?>> listarPeliculas() throws IOException{

		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("XML/peliculas.xml");
	    
	    XmlMapper xmlMapper = new XmlMapper();
	    xmlMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	    
	    com.fasterxml.jackson.databind.JsonNode rootNode = xmlMapper.readTree(inputStream);
	    com.fasterxml.jackson.databind.JsonNode peliculasNode = rootNode.path("pelicula");
	    PeliculaDTO[] arrayPeliculas = xmlMapper.treeToValue(peliculasNode, PeliculaDTO[].class);
	    
	    //Nodo raíz para los datos creado dinámicamente
	    com.fasterxml.jackson.databind.node.ObjectNode dataNode = xmlMapper.createObjectNode();
	    
	    com.fasterxml.jackson.databind.node.ArrayNode arrayNode = xmlMapper.createArrayNode();
	    for (PeliculaDTO p : arrayPeliculas) {
	        //Envolvemos cada película en un nodo con el nombre "pelicula" 
	        arrayNode.add(xmlMapper.createObjectNode().putPOJO("pelicula", p));
	    }
	    
	    //Añadimos las películas al nodo
	    dataNode.set("peliculas", arrayNode);
	    
	    Respuesta<com.fasterxml.jackson.databind.node.ObjectNode> respuesta = 
	        Respuesta.success(dataNode, "Listado de películas");
	        
	    return ResponseEntity.ok(respuesta);		
	}
	
	@PostMapping(consumes = { MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> insertarPelicula(@RequestBody String xmlRaw) {        
	    try {
	        //Leemos el xsd
	        org.springframework.core.io.ClassPathResource xsdResource = 
	            new org.springframework.core.io.ClassPathResource("./XML/peliculas.xsd");

	        //Validador
	        javax.xml.validation.SchemaFactory factory = 
	            javax.xml.validation.SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
	        javax.xml.validation.Schema schema = factory.newSchema(xsdResource.getURL());
	        javax.xml.validation.Validator xsdValidator = schema.newValidator();

	        //Este filtro intercepta los elementos y les inyecta el namespace en memoria durante el procesamiento
	        XMLFilterImpl namespaceFilter = new XMLFilterImpl() {
	            @Override
	            public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
	                //Si el elemento no tiene namespace, le asignamos el del XSD dinámicamente
	                if (uri == null || uri.isEmpty()) {
	                    uri = "https://www.curso.com/peliculas";
	                }
	                super.startElement(uri, localName, qName, atts);
	            }
	        };

	        //Acoplamos el filtro al lector SAX estándar de Java
	        javax.xml.parsers.SAXParserFactory spf = javax.xml.parsers.SAXParserFactory.newInstance();
	        spf.setNamespaceAware(true);
	        org.xml.sax.XMLReader xmlReader = spf.newSAXParser().getXMLReader();
	        namespaceFilter.setParent(xmlReader);

	        //Preparamos el origen de datos usando el filtro y el String original intacto
	        SAXSource saxSource = new SAXSource(namespaceFilter, new InputSource(new java.io.StringReader(xmlRaw)));

	        //Validacion
	        xsdValidator.validate(saxSource);
	        
	        //Creamos el objeto
	        com.fasterxml.jackson.dataformat.xml.XmlMapper xmlMapper = new com.fasterxml.jackson.dataformat.xml.XmlMapper();
	        xmlMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	        PeliculaDTO peliculaDTO = xmlMapper.readValue(xmlRaw, PeliculaDTO.class);
	        System.out.println("Pelicula para insertar: "+peliculaDTO);
	        
	        return new ResponseEntity<>("Película insertada con éxito", HttpStatus.CREATED);	          
	        
	    } catch (Exception e) {
	        return new ResponseEntity<>("XML Inválido según XSD: " + e.getMessage(), HttpStatus.BAD_REQUEST);
	    }
	}	
	
}	
	
