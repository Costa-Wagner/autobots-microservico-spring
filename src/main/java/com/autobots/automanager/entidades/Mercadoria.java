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
public class Mercadoria extends RepresentationModel<Mercadoria> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotBlank(message = "Nome é obrigatório")
	@Column(nullable = false)
	private String nome;
	@Column
	private Date validade;
	@Column
	private Date fabricacao;
	@Column(nullable = false)
	private Date cadastro;
	@PositiveOrZero(message = "Quantidade não pode ser negativa")
	@Column(nullable = false)
	private long quantidade;
	@PositiveOrZero(message = "Valor não pode ser negativo")
	@Column(nullable = false)
	private double valor;
	@Column
	private String descricao;
}