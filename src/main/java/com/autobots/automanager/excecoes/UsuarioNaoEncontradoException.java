package com.autobots.automanager.excecoes;

public class UsuarioNaoEncontradoException extends RuntimeException {
	public UsuarioNaoEncontradoException(long id) {
		super("Usuário não encontrado: id " + id);
	}
}
