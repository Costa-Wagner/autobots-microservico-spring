package com.autobots.automanager.excecoes;

public class UsuarioComVendasException extends RuntimeException {
	public UsuarioComVendasException() {
		super("Usuário possui vendas associadas e não pode ser excluído");
	}
}
