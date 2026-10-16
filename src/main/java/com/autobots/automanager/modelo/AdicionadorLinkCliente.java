package com.autobots.automanager.modelo;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.ClienteControle;
import com.autobots.automanager.controles.DocumentoControle;
import com.autobots.automanager.controles.EnderecoControle;
import com.autobots.automanager.controles.TelefoneControle;
import com.autobots.automanager.entidades.Cliente;

@Component
public class AdicionadorLinkCliente implements AdicionadorLink<Cliente> {
	@Autowired
	private AdicionadorLinkTelefone linkTelefone;
	@Autowired
	private AdicionadorLinkDocumento linkDocumento;
	@Autowired
	private AdicionadorLinkEndereco linkEndereco;

	@Override
	public void adicionarLink(Cliente c) {
		long id = c.getId();
		c.add(linkTo(methodOn(ClienteControle.class).obterCliente(id)).withSelfRel());
		c.add(linkTo(methodOn(ClienteControle.class).obterClientes()).withRel("clientes"));
		c.add(linkTo(methodOn(ClienteControle.class).atualizarCliente(null)).withRel("atualizar"));
		c.add(linkTo(methodOn(ClienteControle.class).excluirCliente(null)).withRel("excluir"));
		c.add(linkTo(methodOn(TelefoneControle.class).listar(id)).withRel("telefones"));
		c.add(linkTo(methodOn(DocumentoControle.class).listar(id)).withRel("documentos"));
		c.add(linkTo(methodOn(EnderecoControle.class).obter(id)).withRel("endereco"));
		c.getTelefones().forEach(t -> linkTelefone.adicionarLink(id, t));
		c.getDocumentos().forEach(d -> linkDocumento.adicionarLink(id, d));
		linkEndereco.adicionarLink(id, c.getEndereco());
	}

	@Override
	public void adicionarLink(List<Cliente> lista) {
		lista.forEach(this::adicionarLink);
	}
}