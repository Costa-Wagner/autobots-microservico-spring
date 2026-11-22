package com.autobots.automanager.filtros;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.autobots.automanager.jwt.ProvedorJwt;

// Lê "Authorization: Bearer <jwt>". Token ausente ou inválido: segue sem autenticar (o Spring responde 401).
public class Autorizador extends BasicAuthenticationFilter {
	private final ProvedorJwt provedorJwt;
	private final UserDetailsService servico;

	public Autorizador(AuthenticationManager gerenciador, ProvedorJwt provedorJwt, UserDetailsService servico) {
		super(gerenciador);
		this.provedorJwt = provedorJwt;
		this.servico = servico;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String cabecalho = request.getHeader("Authorization");
		if (cabecalho != null && cabecalho.startsWith("Bearer ")) {
			String jwt = cabecalho.substring(7).trim();
			if (provedorJwt.validar(jwt)) {
				try {
					// as autoridades vêm do banco a cada requisição (mudança de perfil vale na hora)
					UserDetails usuario = servico.loadUserByUsername(provedorJwt.obterNomeUsuario(jwt));
					SecurityContextHolder.getContext().setAuthentication(
							new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities()));
				} catch (UsernameNotFoundException e) {
					// usuário removido depois da emissão do token: segue sem autenticar
				}
			}
		}
		chain.doFilter(request, response);
	}
}
