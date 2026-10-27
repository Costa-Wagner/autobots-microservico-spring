package com.autobots.automanager.modelo;

import com.autobots.automanager.entidades.Credencial;
import com.autobots.automanager.entidades.CredencialCodigoBarra;
import com.autobots.automanager.entidades.CredencialUsuarioSenha;

public class CredencialAtualizador {
	private StringVerificadorNulo verificador = new StringVerificadorNulo();

	public void atualizar(Credencial credencial, Credencial atualizacao) {
		if (atualizacao.getInativo() != null) {
			credencial.setInativo(atualizacao.getInativo());
		}
		if (credencial instanceof CredencialUsuarioSenha atual && atualizacao instanceof CredencialUsuarioSenha nova) {
			if (!verificador.verificar(nova.getNomeUsuario())) {
				atual.setNomeUsuario(nova.getNomeUsuario());
			}
			if (!verificador.verificar(nova.getSenha())) {
				atual.setSenha(nova.getSenha());
			}
		} else if (credencial instanceof CredencialCodigoBarra atual && atualizacao instanceof CredencialCodigoBarra nova) {
			if (nova.getCodigo() > 0) {
				atual.setCodigo(nova.getCodigo());
			}
		}
	}
}
