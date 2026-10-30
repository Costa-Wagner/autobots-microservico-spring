package com.autobots.automanager.modelo;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.EmpresaControle;
import com.autobots.automanager.controles.EmpresaEnderecoControle;
import com.autobots.automanager.controles.EmpresaTelefoneControle;
import com.autobots.automanager.controles.MercadoriaControle;
import com.autobots.automanager.controles.ServicoControle;
import com.autobots.automanager.controles.UsuarioControle;
import com.autobots.automanager.entidades.Empresa;

@Component
public class AdicionadorLinkEmpresa implements AdicionadorLink<Empresa> {
	@Autowired
	private AdicionadorLinkEmpresaTelefone telefoneAdicionador;
	@Autowired
	private AdicionadorLinkUsuario usuarioAdicionador;
	@Autowired
	private AdicionadorLinkEmpresaEndereco enderecoAdicionador;
	@Autowired
	private AdicionadorLinkRecursos recursosAdicionador;

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
		empresa.add(linkTo(methodOn(EmpresaTelefoneControle.class).listar(empresa.getId())).withRel("telefones"));
		empresa.add(linkTo(methodOn(EmpresaEnderecoControle.class).obter(empresa.getId())).withRel("endereco"));
		empresa.add(linkTo(methodOn(UsuarioControle.class).listar(empresa.getId())).withRel("usuarios"));
		empresa.getUsuarios().forEach(u -> usuarioAdicionador.adicionarLink(empresa.getId(), u));
		empresa.getTelefones().forEach(t -> telefoneAdicionador.adicionarLink(empresa.getId(), t));
		if (empresa.getEndereco() != null) {
			enderecoAdicionador.adicionarLink(empresa.getId(), empresa.getEndereco());
		}
		empresa.add(linkTo(methodOn(MercadoriaControle.class).listar(empresa.getId())).withRel("mercadorias"));
		empresa.add(linkTo(methodOn(ServicoControle.class).listar(empresa.getId())).withRel("servicos"));
		empresa.getMercadorias().forEach(m -> recursosAdicionador.adicionarLink(empresa.getId(), m));
		empresa.getServicos().forEach(s -> recursosAdicionador.adicionarLink(empresa.getId(), s));
	}
}
