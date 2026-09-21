package com.autobots.automanager.modelo;

import java.util.List;

import com.autobots.automanager.entidades.Telefone;

//Instancia o verificador que checa se um campo veio nulo/vazio na atualização — usado para não sobrescrever um campo com valor vazio sem querer.
public class TelefoneAtualizador {
	private StringVerificadorNulo verificador = new StringVerificadorNulo();
	
	//Atualiza um único telefone campo a campo — só grava o novo valor se ele não veio nulo/vazio. 
	public void atualizar(Telefone telefone, Telefone atualizacao) {
		if (atualizacao != null) {
			if (!verificador.verificar(atualizacao.getDdd())) {
				telefone.setDdd(atualizacao.getDdd());
			}
			if (!verificador.verificar(atualizacao.getNumero())) {
				telefone.setNumero(atualizacao.getNumero());
			}
		}
	}
	//Percorre cada telefone que veio na atualização. Se ele não tem id, é um telefone novo → adiciona direto na lista do cliente.
	public void atualizar(List<Telefone> telefones, List<Telefone> atualizacoes) {
	    if (atualizacoes == null || atualizacoes.isEmpty()) return;
		for (Telefone atualizacao : atualizacoes) {
			if (atualizacao.getId() == null) {
				telefones.add(atualizacao);
			
			//Se tem id, procura na lista atual do cliente um telefone com esse mesmo id. Quando acha, chama o método de cima para atualizar campo a campo.
			} else {
				for (Telefone telefone : telefones) {
					if (atualizacao.getId().equals(telefone.getId())) {
						atualizar(telefone, atualizacao);
					}
				}
			}
		}
		//Depois de inserir/atualizar, varre a lista atual do cliente e remove qualquer telefone cujo id não apareceu na lista de atualização recebida — ou seja, telefone que o usuário tirou da lista. Como o relacionamento tem orphanRemoval = true, essa remoção da lista já basta para o JPA apagar do banco no save().
		telefones.removeIf(telefone ->
			telefone.getId() != null &&
			atualizacoes.stream().noneMatch(a -> telefone.getId().equals(a.getId()))
		);
	}
}