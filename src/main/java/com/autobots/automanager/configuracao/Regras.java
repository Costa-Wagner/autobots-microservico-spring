package com.autobots.automanager.configuracao;

public final class Regras {
	private Regras() {}
	public static final String SOMENTE_ADMIN = "hasRole('ADMIN')";
	public static final String GESTAO = "hasAnyRole('ADMIN','GERENTE') and @acesso.daEmpresa(#empresaId)";
	public static final String GESTAO_ID = "hasAnyRole('ADMIN','GERENTE') and @acesso.daEmpresa(#id)";
	public static final String LEITURA_EQUIPE = "hasAnyRole('ADMIN','GERENTE','VENDEDOR') and @acesso.daEmpresa(#empresaId)";
	public static final String LEITURA_TODOS = "hasAnyRole('ADMIN','GERENTE','VENDEDOR','CLIENTE') and @acesso.daEmpresa(#empresaId)";
	public static final String USUARIO_GERENCIA = "@acesso.daEmpresa(#empresaId) and @acesso.podeGerenciar(#empresaId, #usuarioId)";
	public static final String USUARIO_LEITURA = "hasAnyRole('ADMIN','GERENTE','VENDEDOR','CLIENTE') and @acesso.daEmpresa(#empresaId) and (@acesso.podeGerenciar(#empresaId, #usuarioId) or @acesso.proprio(#usuarioId))";
	public static final String USUARIO_CADASTRO = "hasAnyRole('ADMIN','GERENTE','VENDEDOR') and @acesso.daEmpresa(#empresaId) and @acesso.perfisPermitidos(#usuario.perfis)";
	public static final String USUARIO_ATUALIZACAO = USUARIO_GERENCIA + " and (#atualizacao.perfis.isEmpty() or @acesso.perfisPermitidos(#atualizacao.perfis))";
}