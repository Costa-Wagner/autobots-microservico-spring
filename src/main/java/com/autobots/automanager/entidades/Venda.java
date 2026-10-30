package com.autobots.automanager.entidades;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.hateoas.RepresentationModel;

import com.autobots.automanager.enumeracoes.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
public class Venda extends RepresentationModel<Venda> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Date cadastro;

	@NotBlank(message = "Identificação é obrigatória")
	@Column(nullable = false, unique = true)
	private String identificacao;

	@JsonIgnoreProperties({ "perfis", "telefones", "endereco",
		"documentos", "emails", "credenciais",
		"mercadorias", "veiculos", "links" })
	@ManyToOne
	private Usuario cliente;

	@JsonIgnoreProperties({ "perfis", "telefones", "endereco",
			"documentos", "emails", "credenciais",
			"mercadorias", "veiculos", "links" })
	@ManyToOne
	private Usuario funcionario;

	@ManyToMany
	private List<Mercadoria> mercadorias = new ArrayList<>();

	@ManyToMany
	private List<Servico> servicos = new ArrayList<>();

	@ManyToOne
	private Veiculo veiculo;
}