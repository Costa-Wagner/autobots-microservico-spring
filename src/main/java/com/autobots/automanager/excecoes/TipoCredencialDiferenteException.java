package com.autobots.automanager.excecoes;

public class TipoCredencialDiferenteException extends RuntimeException {
	public TipoCredencialDiferenteException() {
		super("O tipo da credencial não pode ser alterado");
	}
}
