package com.autobots.automanager.entidades;

import java.util.Date;
import javax.persistence.EnumType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.autobots.automanager.enumeracoes.TipoDocumento;

import org.springframework.hateoas.RepresentationModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
public class Documento extends RepresentationModel<Documento> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "Tipo é obrigatório")
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TipoDocumento tipo;

	@NotNull(message = "Data de emissão é obrigatória")
	@Column(nullable = false)
	private Date dataEmissao;
	
	@NotBlank(message = "Número é obrigatório")
	@Column(unique = true)
	private String numero;}