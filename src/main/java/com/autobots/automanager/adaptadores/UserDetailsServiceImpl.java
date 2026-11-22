package com.autobots.automanager.adaptadores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.autobots.automanager.entidades.CredencialUsuarioSenha;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.repositorios.EmpresaRepositorio;
import com.autobots.automanager.repositorios.UsuarioRepositorio;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	private UsuarioRepositorio repositorio;
	@Autowired
	private EmpresaRepositorio empresas;

	// @Transactional: os filtros rodam antes do MVC, sem sessão aberta; sem isso perfis e credenciais (lazy) falham
	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String nomeUsuario) throws UsernameNotFoundException {
		Usuario usuario = repositorio.buscarPorNomeUsuario(nomeUsuario)
				.orElseThrow(() -> new UsernameNotFoundException(nomeUsuario));
		CredencialUsuarioSenha credencial = usuario.getCredenciais().stream()
				.filter(c -> c instanceof CredencialUsuarioSenha)
				.map(c -> (CredencialUsuarioSenha) c)
				.filter(c -> nomeUsuario.equals(c.getNomeUsuario()))
				.findFirst()
				.orElseThrow(() -> new UsernameNotFoundException(nomeUsuario));
		Long empresaId = empresas.buscarIdPorUsuario(usuario.getId()).orElse(null);
		return new UserDetailsImpl(usuario, credencial, empresaId);
	}
}