package com.autobots.automanager.excecoes;

public class IdNaoInformadoException extends RuntimeException {
	public IdNaoInformadoException() {
		super("Id é obrigatório");
	}
}
