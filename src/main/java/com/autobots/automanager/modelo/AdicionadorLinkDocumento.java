package com.autobots.automanager.modelo;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.ClienteControle;
import com.autobots.automanager.controles.DocumentoControle;
import com.autobots.automanager.entidades.Documento;

@Component
public class AdicionadorLinkDocumento {
	public void adicionarLink(long clienteId, Documento d) {
		long id = d.getId();
		d.add(linkTo(methodOn(DocumentoControle.class).obter(clienteId, id)).withSelfRel());
		d.add(linkTo(methodOn(DocumentoControle.class).listar(clienteId)).withRel("documentos"));
		d.add(linkTo(methodOn(ClienteControle.class).obterCliente(clienteId)).withRel("cliente"));
	}
}