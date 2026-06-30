package com.curso.endpoint.dto;

import com.curso.modelo.entidad.Pelicula;

public class PeliculaDTO {

	private Integer id;
	private String titulo;
	private String director;
	private String genero;
	private Integer year;

	public PeliculaDTO() {
		super();
	}

	public PeliculaDTO(Integer id, String titulo, String director, String genero, Integer year) {
		super();
		this.id = id;
		this.titulo = titulo;
		this.director = director;
		this.genero = genero;
		this.year = year;
	}

	public PeliculaDTO(Pelicula pelicula) {
		super();
		if (pelicula != null) {
			this.id = pelicula.getId();
			this.titulo = pelicula.getTitulo();
			this.director = pelicula.getDirector();
			this.genero = pelicula.getGenero();
			this.year = pelicula.getYear();
		}
	}

	public Pelicula asPelicula() {
		return new Pelicula(this.id, this.titulo, this.director, this.genero, this.year);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String getGenero() {
		return genero;
	}

	public void setGenero(String genero) {
		this.genero = genero;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	@Override
	public String toString() {
		return "PeliculaDTO [id=" + id + ", titulo=" + titulo + ", director=" + director + ", genero=" + genero
				+ ", year=" + year + "]";
	}

}