package com.autobots.automanager.excecoes;

public class UltimoDocumentoException extends RuntimeException {
	public UltimoDocumentoException() {
		super("Cliente precisa ter ao menos um documento");
	}
}