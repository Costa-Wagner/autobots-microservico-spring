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

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
public class Empresa extends RepresentationModel<Empresa> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Razão social é obrigatória")
	@Column(nullable = false)
	private String razaoSocial;

	@Column
	private String nomeFantasia;

	@Valid
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
	private List<Telefone> telefones = new ArrayList<>();

	@Valid
	@NotNull(message = "Endereço é obrigatório")
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private Endereco endereco;

	@Column(nullable = false)
	private Date cadastro;

	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
	private List<Usuario> usuarios = new ArrayList<>();

	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
	private List<Mercadoria> mercadorias = new ArrayList<>();

	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
	private List<Servico> servicos = new ArrayList<>();

	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
	private List<Venda> vendas = new ArrayList<>();
}