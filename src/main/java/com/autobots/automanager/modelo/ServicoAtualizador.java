package com.autobots.automanager.modelo;

import com.autobots.automanager.entidades.Servico;

public class ServicoAtualizador {
	private StringVerificadorNulo verificador = new StringVerificadorNulo();

	public void atualizar(Servico s, Servico a) {
		if (!verificador.verificar(a.getNome())) s.setNome(a.getNome());
		if (!verificador.verificar(a.getDescricao())) s.setDescricao(a.getDescricao());
		if (a.getValor() != null && a.getValor() >= 0) s.setValor(a.getValor());
	}
}
