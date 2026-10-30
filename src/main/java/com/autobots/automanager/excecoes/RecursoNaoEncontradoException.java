package com.autobots.automanager.excecoes;

public class RecursoNaoEncontradoException extends RuntimeException {
	public RecursoNaoEncontradoException(String descricao, long id) {
		super(descricao + ": id " + id);
	}
}
