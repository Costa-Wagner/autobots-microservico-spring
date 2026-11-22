package com.autobots.automanager.adaptadores;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.autobots.automanager.entidades.CredencialUsuarioSenha;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.enumeracoes.PerfilUsuario;

// Copia os dados para campos simples: o objeto é usado fora da transação (filtros), sem lazy loading
@SuppressWarnings("serial")
public class UserDetailsImpl implements UserDetails {
	private final Long usuarioId;
	private final Long empresaId;
	private final String nomeUsuario;
	private final String senha;
	private final boolean ativo;
	private final List<GrantedAuthority> autoridades = new ArrayList<>();

	public UserDetailsImpl(Usuario usuario, CredencialUsuarioSenha credencial, Long empresaId) {
		this.usuarioId = usuario.getId();
		this.empresaId = empresaId;
		this.nomeUsuario = credencial.getNomeUsuario();
		this.senha = credencial.getSenha();
		this.ativo = !Boolean.TRUE.equals(credencial.getInativo());
		for (PerfilUsuario perfil : usuario.getPerfis()) {
			if (perfil != PerfilUsuario.FORNECEDOR) {   // fornecedor não tem permissão em rota alguma
				autoridades.add(new SimpleGrantedAuthority("ROLE_" + perfil.name()));
			}
		}
	}

	public Long getUsuarioId() { return usuarioId; }
	public Long getEmpresaId() { return empresaId; }
	@Override public Collection<? extends GrantedAuthority> getAuthorities() { return autoridades; }
	@Override public String getPassword() { return senha; }
	@Override public String getUsername() { return nomeUsuario; }
	@Override public boolean isAccountNonExpired() { return true; }
	@Override public boolean isAccountNonLocked() { return true; }
	@Override public boolean isCredentialsNonExpired() { return true; }
	@Override public boolean isEnabled() { return ativo; }
	public boolean temRole(String role) {
		return autoridades.stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
	}
	public boolean isGestao() {
		return temRole("ADMIN") || temRole("GERENTE");
	}
}