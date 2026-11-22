package com.autobots.automanager.configuracao;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.autobots.automanager.adaptadores.UserDetailsImpl;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.enumeracoes.PerfilUsuario;
import com.autobots.automanager.repositorios.EmpresaRepositorio;
import com.autobots.automanager.repositorios.UsuarioRepositorio;

@Component("acesso")
public class Acesso {
	@Autowired
	private UsuarioRepositorio usuarios;
	@Autowired
	private EmpresaRepositorio empresas;

	private UserDetailsImpl logado() {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		if (a == null || !(a.getPrincipal() instanceof UserDetailsImpl)) {
			return null;
		}
		return (UserDetailsImpl) a.getPrincipal();
	}

	// perfis de usuário que o logado pode gerenciar (Tabela 1)
	private Set<PerfilUsuario> gerenciaveis(UserDetailsImpl u) {
		Set<PerfilUsuario> set = EnumSet.noneOf(PerfilUsuario.class);
		if (u.temRole("ADMIN")) {
			set.addAll(EnumSet.allOf(PerfilUsuario.class));
		}
		if (u.temRole("GERENTE")) {
			set.addAll(EnumSet.of(PerfilUsuario.GERENTE, PerfilUsuario.VENDEDOR, PerfilUsuario.CLIENTE));
		}
		if (u.temRole("VENDEDOR")) {
			set.add(PerfilUsuario.CLIENTE);
		}
		return set;
	}

	// ADMIN acessa qualquer empresa; os demais perfis só a própria
	public boolean daEmpresa(long empresaId) {
		UserDetailsImpl u = logado();
		if (u == null) {
			return false;
		}
		return u.temRole("ADMIN") || (u.getEmpresaId() != null && u.getEmpresaId().longValue() == empresaId);
	}

	public boolean proprio(long usuarioId) {
		UserDetailsImpl u = logado();
		return u != null && u.getUsuarioId() != null && u.getUsuarioId().longValue() == usuarioId;
	}

	// todos os perfis informados estão entre os que o logado pode gerenciar (usado no corpo de POST/PUT)
	public boolean perfisPermitidos(Collection<PerfilUsuario> perfis) {
		UserDetailsImpl u = logado();
		return u != null && perfis != null && !perfis.isEmpty() && gerenciaveis(u).containsAll(perfis);
	}

	// usuário-alvo existe na empresa e todos os perfis dele são gerenciáveis pelo logado (falha fechada: 403)
	@Transactional(readOnly = true)
	public boolean podeGerenciar(long empresaId, long usuarioId) {
		UserDetailsImpl u = logado();
		if (u == null) {
			return false;
		}
		if (u.temRole("ADMIN")) {
			return true;
		}
		Long dono = empresas.buscarIdPorUsuario(usuarioId).orElse(null);
		if (dono == null || dono.longValue() != empresaId) {
			return false;
		}
		Set<PerfilUsuario> permitidos = gerenciaveis(u);
		return usuarios.findById(usuarioId).map(t -> permitidos.containsAll(t.getPerfis())).orElse(false);
	}

	// filtro da listagem de usuários
	@Transactional(readOnly = true)
	public boolean visivel(Usuario alvo) {
		return perfisPermitidos(alvo.getPerfis()) || proprio(alvo.getId());
	}
}