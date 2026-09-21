package com.autobots.automanager.excecoes;

public class ClienteNaoEncontradoException extends RuntimeException {
	public ClienteNaoEncontradoException(long id) {
		super("Cliente não encontrado: id " + id);
	}
}