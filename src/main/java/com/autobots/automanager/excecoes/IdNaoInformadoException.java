package com.autobots.automanager.excecoes;

public class IdNaoInformadoException extends RuntimeException {
	public IdNaoInformadoException() {
		super("Id do cliente é obrigatório");
	}
}
