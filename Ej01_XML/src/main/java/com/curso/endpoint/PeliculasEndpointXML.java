package com.curso.endpoint;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.curso.endpoint.dto.PeliculaDTO;
import com.curso.endpoint.dto.Respuesta;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@RestController
@RequestMapping(path = "/peliculas")
public class PeliculasEndpointXML {
	
	
	/*

	HEAD cr;lf
	cr;lf;
	BODY
	
	GET /peliculas
	
	200 OK
	ContentType: application/xml
	----------------------------
	<respuesta>
		<success>true/false</success>
		<message>Listado de peliculas</message>
		<data>
			<peliculas>
				<pelicula></pelicula>
				<pelicula></pelicula>
				<pelicula></pelicula>
			</peliculas>
		</data>
		<timestamp>1234567879</timestamp>
	</respuesta>
	*/

	@GetMapping(produces = { MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<Respuesta<?>> listarPeliculas() throws IOException{
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//Aqui en realidad estaríamos llamando a la capa de modelo para que se busquen las películas en una base de datos
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("XML/01_sintaxis.xml");
		XmlMapper xmlMapper = new XmlMapper();
		Map<String, Object> datosXml = xmlMapper.readValue(inputStream, Map.class);
		Map<String, Map<String, Object>> peliculas = new HashMap<>();
		peliculas.put("peliculas", datosXml);
	    Respuesta<Map<String, ?>> respuesta = Respuesta.success(peliculas, "Listado de películas");
	    return ResponseEntity.ok(respuesta);		
	}
	
	@PostMapping
	public ResponseEntity<String> insertarPelicula(@RequestBody PeliculaDTO peliculaDTO){		
		System.out.println("Insertando película: "+peliculaDTO);
		return new ResponseEntity<>("Película insertada", HttpStatus.CREATED);		
	}	
	
}
