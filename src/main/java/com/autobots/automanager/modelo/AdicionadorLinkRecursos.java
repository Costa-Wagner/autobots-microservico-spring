package com.autobots.automanager.modelo;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.EmpresaControle;
import com.autobots.automanager.controles.MercadoriaControle;
import com.autobots.automanager.controles.ServicoControle;
import com.autobots.automanager.controles.UsuarioControle;
import com.autobots.automanager.controles.UsuarioVeiculoControle;
import com.autobots.automanager.entidades.Mercadoria;
import com.autobots.automanager.entidades.Servico;
import com.autobots.automanager.entidades.Veiculo;

@Component
public class AdicionadorLinkRecursos {
	public void adicionarLink(long empresaId, Mercadoria m) {
		m.add(linkTo(methodOn(MercadoriaControle.class).obter(empresaId, m.getId())).withSelfRel());
		m.add(linkTo(methodOn(MercadoriaControle.class).listar(empresaId)).withRel("mercadorias"));
		m.add(linkTo(methodOn(EmpresaControle.class).obterEmpresa(empresaId)).withRel("empresa"));
	}

	public void adicionarLink(long empresaId, Servico s) {
		s.add(linkTo(methodOn(ServicoControle.class).obter(empresaId, s.getId())).withSelfRel());
		s.add(linkTo(methodOn(ServicoControle.class).listar(empresaId)).withRel("servicos"));
		s.add(linkTo(methodOn(EmpresaControle.class).obterEmpresa(empresaId)).withRel("empresa"));
	}

	public void adicionarLink(long empresaId, long usuarioId, Veiculo v) {
		v.add(linkTo(methodOn(UsuarioVeiculoControle.class).obter(empresaId, usuarioId, v.getId())).withSelfRel());
		v.add(linkTo(methodOn(UsuarioVeiculoControle.class).listar(empresaId, usuarioId)).withRel("veiculos"));
		v.add(linkTo(methodOn(UsuarioControle.class).obter(empresaId, usuarioId)).withRel("usuario"));
	}
}
