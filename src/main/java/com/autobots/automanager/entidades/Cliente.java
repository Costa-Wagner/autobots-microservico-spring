package com.autobots.automanager.entidades;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.hateoas.RepresentationModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
public class Cliente extends RepresentationModel<Cliente> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Nome é obrigatório")   // impede nome vazio ou só espaços
	@Column
	private String nome;

	@Column
	private String nomeSocial;

	@NotNull(message = "Data de nascimento é obrigatória")   // impede data nula
	@Column
	private Date dataNascimento;

	@Column
	private Date dataCadastro;

	@Valid   // valida também os campos internos de cada Documento da lista
	@NotEmpty(message = "Cliente precisa ter ao menos um documento")	// impede lista vazia
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)			//orphanRemoval - apaga do BD
	private List<Documento> documentos = new ArrayList<>();

	@Valid   // valida também os campos internos do Endereco
	@NotNull(message = "Endereço é obrigatório")   // impede endereço nulo
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private Endereco endereco;

	@Valid   // valida os campos internos de cada Telefone, caso a lista não esteja vazia
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
	private List<Telefone> telefones = new ArrayList<>();

}