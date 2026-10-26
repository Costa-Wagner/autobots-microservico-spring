package com.autobots.automanager.modelo;

import com.autobots.automanager.entidades.Usuario;

public class UsuarioAtualizador {
	private StringVerificadorNulo verificador = new StringVerificadorNulo();
	private EnderecoAtualizador enderecoAtualizador = new EnderecoAtualizador();

	public void atualizar(Usuario usuario, Usuario atualizacao) {
		if (!verificador.verificar(atualizacao.getNome())) {
			usuario.setNome(atualizacao.getNome());
		}
		if (!verificador.verificar(atualizacao.getNomeSocial())) {
			usuario.setNomeSocial(atualizacao.getNomeSocial());
		}
		// lista omitida ou vazia = sem alteração; caso contrário, substitui (sem repetidos)
		if (atualizacao.getPerfis() != null && !atualizacao.getPerfis().isEmpty()) {
			usuario.getPerfis().clear();
			atualizacao.getPerfis().stream().distinct().forEach(usuario.getPerfis()::add);
		}
		enderecoAtualizador.atualizar(usuario.getEndereco(), atualizacao.getEndereco());
	}
}
