package com.autobots.automanager.modelo;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.autobots.automanager.controles.UsuarioDocumentoControle;
import com.autobots.automanager.controles.UsuarioEmailControle;
import com.autobots.automanager.controles.UsuarioEnderecoControle;
import com.autobots.automanager.controles.UsuarioTelefoneControle;
import com.autobots.automanager.controles.CredencialControle;
import com.autobots.automanager.controles.EmpresaControle;
import com.autobots.automanager.controles.UsuarioControle;
import com.autobots.automanager.entidades.Usuario;

@Component
public class AdicionadorLinkUsuario {
	@Autowired
	private AdicionadorLinkUsuarioFilho filhoAdicionador;
	@Autowired
	private AdicionadorLinkCredencial credencialAdicionador;
	
	public void adicionarLink(long empresaId, Usuario usuario) {
		usuario.add(linkTo(methodOn(UsuarioControle.class).obter(empresaId, usuario.getId())).withSelfRel());
		usuario.add(linkTo(methodOn(UsuarioControle.class).listar(empresaId)).withRel("usuarios"));
		usuario.add(linkTo(methodOn(EmpresaControle.class).obterEmpresa(empresaId)).withRel("empresa"));
		usuario.add(linkTo(methodOn(UsuarioTelefoneControle.class).listar(empresaId, usuario.getId())).withRel("telefones"));
		usuario.add(linkTo(methodOn(UsuarioDocumentoControle.class).listar(empresaId, usuario.getId())).withRel("documentos"));
		usuario.add(linkTo(methodOn(UsuarioEmailControle.class).listar(empresaId, usuario.getId())).withRel("emails"));
		usuario.add(linkTo(methodOn(UsuarioEnderecoControle.class).obter(empresaId, usuario.getId())).withRel("endereco"));
		usuario.getTelefones().forEach(t -> filhoAdicionador.adicionarLink(empresaId, usuario.getId(), t));
		usuario.getDocumentos().forEach(d -> filhoAdicionador.adicionarLink(empresaId, usuario.getId(), d));
		usuario.getEmails().forEach(e -> filhoAdicionador.adicionarLink(empresaId, usuario.getId(), e));
		usuario.add(linkTo(methodOn(CredencialControle.class).listar(empresaId, usuario.getId())).withRel("credenciais"));
		usuario.getCredenciais().forEach(c -> credencialAdicionador.adicionarLink(empresaId, usuario.getId(), c));
		if (usuario.getEndereco() != null) {
			filhoAdicionador.adicionarLink(empresaId, usuario.getId(), usuario.getEndereco());
		}
	}
}
