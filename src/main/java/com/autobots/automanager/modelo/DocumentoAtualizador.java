package com.autobots.automanager.modelo;

import java.util.List;

import com.autobots.automanager.entidades.Documento;

//Instancia o verificador que checa se um campo veio nulo/vazio na atualização — usado para não sobrescrever um campo com valor vazio sem querer.
public class DocumentoAtualizador {
	private StringVerificadorNulo verificador = new StringVerificadorNulo();

	//Atualiza um único documento campo a campo — só grava o novo valor se ele não veio nulo/vazio.
	public void atualizar(Documento documento, Documento atualizacao) {
		if (atualizacao != null) {
			
			if (atualizacao.getTipo() != null) {
					documento.setTipo(atualizacao.getTipo());
				}
				if (atualizacao.getDataEmissao() != null) {
					documento.setDataEmissao(atualizacao.getDataEmissao());
				}
			if (!verificador.verificar(atualizacao.getNumero())) {
				documento.setNumero(atualizacao.getNumero());
			}
		}
	}
	//Percorre cada documento que veio na atualização. Se ele não tem id, é um documento novo → adiciona direto na lista do cliente.
	public void atualizar(List<Documento> documentos, List<Documento> atualizacoes) {
	    if (atualizacoes == null || atualizacoes.isEmpty()) return;
		for (Documento atualizacao : atualizacoes) {
			if (atualizacao.getId() == null) {
				documentos.add(atualizacao);
			
			//Se tem id, procura na lista atual do cliente um doc com esse mesmo id. Quando acha, chama o método de cima para atualizar campo a campo.
			} else {
				for (Documento documento : documentos) {
					if (atualizacao.getId().equals(documento.getId())) {
						atualizar(documento, atualizacao);
					}
				}
			}
		}
		//Depois de inserir/atualizar, varre a lista atual do cliente e remove qualquer documento cujo id não apareceu na lista de atualização recebida — ou seja, documento que o usuário tirou da lista. Como o relacionamento tem orphanRemoval = true, essa remoção da lista já basta para o JPA apagar do banco no save().
		documentos.removeIf(documento ->
			documento.getId() != null &&
			atualizacoes.stream().noneMatch(a -> documento.getId().equals(a.getId()))
		);
	}
}
