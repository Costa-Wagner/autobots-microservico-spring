package com.autobots.automanager.modelo;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.CredencialControle;
import com.autobots.automanager.controles.UsuarioControle;
import com.autobots.automanager.entidades.Credencial;

@Component
public class AdicionadorLinkCredencial {
	public void adicionarLink(long empresaId, long usuarioId, Credencial c) {
		c.add(linkTo(methodOn(CredencialControle.class).obter(empresaId, usuarioId, c.getId())).withSelfRel());
		c.add(linkTo(methodOn(CredencialControle.class).listar(empresaId, usuarioId)).withRel("credenciais"));
		c.add(linkTo(methodOn(UsuarioControle.class).obter(empresaId, usuarioId)).withRel("usuario"));
	}
}
