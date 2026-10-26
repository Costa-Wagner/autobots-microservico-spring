package com.autobots.automanager.modelo;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.EmpresaControle;
import com.autobots.automanager.controles.EmpresaTelefoneControle;
import com.autobots.automanager.entidades.Telefone;

@Component
public class AdicionadorLinkEmpresaTelefone {
	public void adicionarLink(long empresaId, Telefone telefone) {
		telefone.add(linkTo(methodOn(EmpresaTelefoneControle.class).obter(empresaId, telefone.getId())).withSelfRel());
		telefone.add(linkTo(methodOn(EmpresaTelefoneControle.class).listar(empresaId)).withRel("telefones"));
		telefone.add(linkTo(methodOn(EmpresaControle.class).obterEmpresa(empresaId)).withRel("empresa"));
	}
}
