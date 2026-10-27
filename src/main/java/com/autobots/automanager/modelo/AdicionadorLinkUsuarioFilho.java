package com.autobots.automanager.modelo;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.UsuarioControle;
import com.autobots.automanager.controles.UsuarioDocumentoControle;
import com.autobots.automanager.controles.UsuarioEmailControle;
import com.autobots.automanager.controles.UsuarioEnderecoControle;
import com.autobots.automanager.controles.UsuarioTelefoneControle;
import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.entidades.Email;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.entidades.Telefone;

@Component
public class AdicionadorLinkUsuarioFilho {

	public void adicionarLink(long empresaId, long usuarioId, Telefone t) {
		t.add(linkTo(methodOn(UsuarioTelefoneControle.class).obter(empresaId, usuarioId, t.getId())).withSelfRel());
		t.add(linkTo(methodOn(UsuarioTelefoneControle.class).listar(empresaId, usuarioId)).withRel("telefones"));
		t.add(linkTo(methodOn(UsuarioControle.class).obter(empresaId, usuarioId)).withRel("usuario"));
	}

	public void adicionarLink(long empresaId, long usuarioId, Documento d) {
		d.add(linkTo(methodOn(UsuarioDocumentoControle.class).obter(empresaId, usuarioId, d.getId())).withSelfRel());
		d.add(linkTo(methodOn(UsuarioDocumentoControle.class).listar(empresaId, usuarioId)).withRel("documentos"));
		d.add(linkTo(methodOn(UsuarioControle.class).obter(empresaId, usuarioId)).withRel("usuario"));
	}

	public void adicionarLink(long empresaId, long usuarioId, Email e) {
		e.add(linkTo(methodOn(UsuarioEmailControle.class).obter(empresaId, usuarioId, e.getId())).withSelfRel());
		e.add(linkTo(methodOn(UsuarioEmailControle.class).listar(empresaId, usuarioId)).withRel("emails"));
		e.add(linkTo(methodOn(UsuarioControle.class).obter(empresaId, usuarioId)).withRel("usuario"));
	}

	public void adicionarLink(long empresaId, long usuarioId, Endereco e) {
		e.add(linkTo(methodOn(UsuarioEnderecoControle.class).obter(empresaId, usuarioId)).withSelfRel());
		e.add(linkTo(methodOn(UsuarioControle.class).obter(empresaId, usuarioId)).withRel("usuario"));
	}
}
