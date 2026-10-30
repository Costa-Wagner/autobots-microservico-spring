package com.autobots.automanager.modelo;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.EmpresaControle;
import com.autobots.automanager.controles.UsuarioControle;
import com.autobots.automanager.controles.UsuarioVeiculoControle;
import com.autobots.automanager.controles.VendaControle;
import com.autobots.automanager.entidades.Venda;

@Component
public class AdicionadorLinkVenda {
	public void adicionarLink(long empresaId, Venda v) {
		v.add(linkTo(methodOn(VendaControle.class).obter(empresaId, v.getId())).withSelfRel());
		v.add(linkTo(methodOn(VendaControle.class).listar(empresaId)).withRel("vendas"));
		v.add(linkTo(methodOn(EmpresaControle.class).obterEmpresa(empresaId)).withRel("empresa"));
		if (v.getCliente() != null) {
			long clienteId = v.getCliente().getId();
			v.add(linkTo(methodOn(UsuarioControle.class).obter(empresaId, clienteId)).withRel("cliente"));
			if (v.getVeiculo() != null) {
				v.add(linkTo(methodOn(UsuarioVeiculoControle.class)
						.obter(empresaId, clienteId, v.getVeiculo().getId())).withRel("veiculo"));
			}
		}
		if (v.getFuncionario() != null) {
			v.add(linkTo(methodOn(UsuarioControle.class)
					.obter(empresaId, v.getFuncionario().getId())).withRel("funcionario"));
		}
	}
}
