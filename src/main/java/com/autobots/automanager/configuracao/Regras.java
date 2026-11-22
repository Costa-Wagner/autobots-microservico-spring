package com.autobots.automanager.configuracao;

public final class Regras {
	private Regras() {}
	public static final String SOMENTE_ADMIN = "hasRole('ADMIN')";
	public static final String GESTAO = "hasAnyRole('ADMIN','GERENTE') and @acesso.daEmpresa(#empresaId)";
	public static final String GESTAO_ID = "hasAnyRole('ADMIN','GERENTE') and @acesso.daEmpresa(#id)";
	public static final String LEITURA_EQUIPE = "hasAnyRole('ADMIN','GERENTE','VENDEDOR') and @acesso.daEmpresa(#empresaId)";
	public static final String LEITURA_TODOS = "hasAnyRole('ADMIN','GERENTE','VENDEDOR','CLIENTE') and @acesso.daEmpresa(#empresaId)";
}