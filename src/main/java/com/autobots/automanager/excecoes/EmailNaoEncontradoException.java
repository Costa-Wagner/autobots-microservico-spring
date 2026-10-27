package com.autobots.automanager.excecoes;

public class EmailNaoEncontradoException extends RuntimeException {
	public EmailNaoEncontradoException(long id) {
		super("E-mail não encontrado: id " + id);
	}
}
