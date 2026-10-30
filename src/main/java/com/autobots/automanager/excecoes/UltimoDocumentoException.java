package com.autobots.automanager.excecoes;

public class UltimoDocumentoException extends RuntimeException {
	public UltimoDocumentoException() {
		super("Usuário precisa ter ao menos um documento");
	}
}