package com.autobots.automanager.entidades;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
@Entity
public class Endereco {
	@Id()
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message = "Rua é obrigatória")
	@Column(nullable = false)
	private String rua;

	@NotBlank(message = "Número é obrigatório")
	@Column(nullable = false)
	private String numero;

	@Column(nullable = true)
	private String bairro;
	
	@NotBlank(message = "Cidade é obrigatória")
	@Column(nullable = false)
	private String cidade;

	@Column(nullable = true)
	private String estado;

	@Column(nullable = true)
	private String codigoPostal;
	
	@Column(unique = false, nullable = true)
	private String informacoesAdicionais;

}