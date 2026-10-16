package com.autobots.automanager.modelo;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.ClienteControle;
import com.autobots.automanager.controles.TelefoneControle;
import com.autobots.automanager.entidades.Telefone;

@Component
public class AdicionadorLinkTelefone {
	public void adicionarLink(long clienteId, Telefone t) {
		long id = t.getId();
		t.add(linkTo(methodOn(TelefoneControle.class).obter(clienteId, id)).withSelfRel());
		t.add(linkTo(methodOn(TelefoneControle.class).listar(clienteId)).withRel("telefones"));
		t.add(linkTo(methodOn(ClienteControle.class).obterCliente(clienteId)).withRel("cliente"));
	}
}