package com.autobots.automanager.excecoes;

public class CredencialNaoEncontradaException extends RuntimeException {
	public CredencialNaoEncontradaException(long id) {
		super("Credencial não encontrada: id " + id);
	}
}
