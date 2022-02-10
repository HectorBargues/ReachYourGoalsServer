package com.hectorbargues.ReachYourGoals.entity;

import javax.persistence.Entity;
// import javax.persistence.GeneratedValue;
// import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.OneToMany;

@Entity
@Table(name = "tipousuario")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class TipoUsuarioEntity {

	@Id
	// @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nombre;

	@OneToMany(mappedBy = "tipousuario")
	private List<UsuarioEntity> usuarios = new ArrayList<>();

	public TipoUsuarioEntity() {
	}

	public TipoUsuarioEntity(Long id, String nombre) {
		this.id = id;
		this.nombre = nombre;
	}

	public Long getId() {
		return this.id;
	}

	public String getNombre() {
		return this.nombre;
	}

	public int getUsuarios() {
		return usuarios.size();
	}

}
