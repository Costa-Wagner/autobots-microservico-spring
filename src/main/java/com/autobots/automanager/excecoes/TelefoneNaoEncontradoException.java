package com.autobots.automanager.excecoes;

public class TelefoneNaoEncontradoException extends RuntimeException {
	public TelefoneNaoEncontradoException(long id) {
		super("Telefone não encontrado: id " + id);
	}
}