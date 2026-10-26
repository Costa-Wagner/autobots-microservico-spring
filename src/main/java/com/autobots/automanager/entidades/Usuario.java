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
@EqualsAndHashCode(callSuper = false, exclude = { "veiculos", "mercadorias" })
@Entity
public class Usuario extends RepresentationModel<Usuario> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Nome é obrigatório")
	@Column(nullable = false)
	private String nome;

	@Column
	private String nomeSocial;

	@NotEmpty(message = "Usuário precisa ter ao menos um perfil")
	@ElementCollection
	@Enumerated(EnumType.STRING)
	private List<PerfilUsuario> perfis = new ArrayList<>();

	@Valid
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
	private List<Telefone> telefones = new ArrayList<>();

	@Valid
	@NotNull(message = "Endereço é obrigatório")
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private Endereco endereco;

	@Valid
	@NotEmpty(message = "Usuário precisa ter ao menos um documento")
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
	private List<Documento> documentos = new ArrayList<>();

	@Valid
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
	private List<Email> emails = new ArrayList<>();

	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
	private List<Credencial> credenciais = new ArrayList<>();

	// mercadorias fornecidas (perfil FORNECEDOR); a persistência vem da Empresa
	@OneToMany
	private List<Mercadoria> mercadorias = new ArrayList<>();

	@OneToMany(mappedBy = "proprietario", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Veiculo> veiculos = new ArrayList<>();
}