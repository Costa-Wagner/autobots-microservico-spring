package com.autobots.automanager.modelo;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.EmpresaControle;
import com.autobots.automanager.controles.UsuarioControle;
import com.autobots.automanager.entidades.Usuario;

@Component
public class AdicionadorLinkUsuario {
	public void adicionarLink(long empresaId, Usuario usuario) {
		usuario.add(linkTo(methodOn(UsuarioControle.class).obter(empresaId, usuario.getId())).withSelfRel());
		usuario.add(linkTo(methodOn(UsuarioControle.class).listar(empresaId)).withRel("usuarios"));
		usuario.add(linkTo(methodOn(EmpresaControle.class).obterEmpresa(empresaId)).withRel("empresa"));
	}
}
