package com.autobots.automanager.excecoes;

public class EmpresaNaoEncontradaException extends RuntimeException {
	public EmpresaNaoEncontradaException(long id) {
		super("Empresa não encontrada: id " + id);
	}
}
