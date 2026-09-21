package com.autobots.automanager.excecoes;

public class DocumentoNaoEncontradoException extends RuntimeException {
	public DocumentoNaoEncontradoException(long id) {
		super("Documento não encontrado: id " + id);
	}
}