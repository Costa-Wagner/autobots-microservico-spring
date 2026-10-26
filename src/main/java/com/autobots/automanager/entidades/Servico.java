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
public class Servico extends RepresentationModel<Servico> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotBlank(message = "Nome é obrigatório")
	@Column(nullable = false)
	private String nome;
	@PositiveOrZero(message = "Valor não pode ser negativo")
	@Column(nullable = false)
	private double valor;
	@Column
	private String descricao;
}