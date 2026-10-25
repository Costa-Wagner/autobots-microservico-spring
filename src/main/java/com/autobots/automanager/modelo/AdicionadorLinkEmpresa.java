package com.autobots.automanager.modelo;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.EmpresaControle;
import com.autobots.automanager.entidades.Empresa;

@Component
public class AdicionadorLinkEmpresa implements AdicionadorLink<Empresa> {

	@Override
	public void adicionarLink(List<Empresa> lista) {
		for (Empresa empresa : lista) {
			empresa.add(linkTo(methodOn(EmpresaControle.class).obterEmpresa(empresa.getId())).withSelfRel());
		}
	}

	@Override
	public void adicionarLink(Empresa empresa) {
		empresa.add(linkTo(methodOn(EmpresaControle.class).obterEmpresa(empresa.getId())).withSelfRel());
		empresa.add(linkTo(methodOn(EmpresaControle.class).obterEmpresas()).withRel("empresas"));
		empresa.add(linkTo(methodOn(EmpresaControle.class).atualizarEmpresa(null)).withRel("atualizar"));
		empresa.add(linkTo(methodOn(EmpresaControle.class).excluirEmpresa(null)).withRel("excluir"));
	}
}
