package com.autobots.automanager.modelo;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.ClienteControle;
import com.autobots.automanager.controles.EnderecoControle;
import com.autobots.automanager.entidades.Endereco;

@Component
public class AdicionadorLinkEndereco {
	public void adicionarLink(long clienteId, Endereco e) {
		e.add(linkTo(methodOn(EnderecoControle.class).obter(clienteId)).withSelfRel());
		e.add(linkTo(methodOn(ClienteControle.class).obterCliente(clienteId)).withRel("cliente"));
	}
}