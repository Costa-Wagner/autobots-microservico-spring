package com.autobots.automanager.filtros;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.autobots.automanager.jwt.ProvedorJwt;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

// Trata POST /login com corpo JSON {"nomeUsuario": "...", "senha": "..."}
public class Autenticador extends UsernamePasswordAuthenticationFilter {
	public static class LoginDados {
		public String nomeUsuario;
		public String senha;
	}

	private final AuthenticationManager gerenciador;
	private final ProvedorJwt provedorJwt;
	private final ObjectMapper mapeador = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	public Autenticador(AuthenticationManager gerenciador, ProvedorJwt provedorJwt) {
		this.gerenciador = gerenciador;
		this.provedorJwt = provedorJwt;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		LoginDados dados;
		try {
			dados = mapeador.readValue(request.getInputStream(), LoginDados.class);
		} catch (IOException e) {
			dados = new LoginDados();
		}
		String usuario = dados == null || dados.nomeUsuario == null ? "" : dados.nomeUsuario;
		String senha = dados == null || dados.senha == null ? "" : dados.senha;
		return gerenciador.authenticate(new UsernamePasswordAuthenticationToken(usuario, senha, new ArrayList<>()));
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			FilterChain chain, Authentication autenticacao) throws IOException, ServletException {
		UserDetails usuario = (UserDetails) autenticacao.getPrincipal();
		String jwt = provedorJwt.gerar(usuario.getUsername());
		response.addHeader("Authorization", "Bearer " + jwt);
		Map<String, String> corpo = new LinkedHashMap<>();
		corpo.put("token", jwt);
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json;charset=UTF-8");
		mapeador.writeValue(response.getWriter(), corpo);
	}
}
