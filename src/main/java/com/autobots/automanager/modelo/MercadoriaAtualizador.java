package com.autobots.automanager.modelo;

import com.autobots.automanager.entidades.Mercadoria;

public class MercadoriaAtualizador {
	private StringVerificadorNulo verificador = new StringVerificadorNulo();

	public void atualizar(Mercadoria m, Mercadoria a) {
		if (!verificador.verificar(a.getNome())) m.setNome(a.getNome());
		if (!verificador.verificar(a.getDescricao())) m.setDescricao(a.getDescricao());
		if (a.getValidade() != null) m.setValidade(a.getValidade());
		if (a.getFabricacao() != null) m.setFabricacao(a.getFabricacao());
		if (a.getQuantidade() != null && a.getQuantidade() >= 0) m.setQuantidade(a.getQuantidade());
		if (a.getValor() != null && a.getValor() >= 0) m.setValor(a.getValor());
	}
}
