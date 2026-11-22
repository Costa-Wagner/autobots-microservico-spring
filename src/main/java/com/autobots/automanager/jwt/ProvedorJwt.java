package com.autobots.automanager.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class ProvedorJwt {
	@Value("${jwt.secret}")
	private String assinatura;
	@Value("${jwt.expiration}")
	private long duracao;

	public String gerar(String nomeUsuario) {
		return Jwts.builder()
				.setSubject(nomeUsuario)
				.setExpiration(new Date(System.currentTimeMillis() + duracao))
				.signWith(SignatureAlgorithm.HS512, assinatura.getBytes(StandardCharsets.UTF_8))
				.compact();
	}

	// null se o token for inválido, adulterado ou expirado (o parser valida assinatura e exp)
	private Claims reivindicacoes(String jwt) {
		try {
			return Jwts.parser().setSigningKey(assinatura.getBytes(StandardCharsets.UTF_8))
					.parseClaimsJws(jwt).getBody();
		} catch (JwtException | IllegalArgumentException e) {
			return null;
		}
	}

	public boolean validar(String jwt) {
		Claims claims = reivindicacoes(jwt);
		return claims != null && claims.getSubject() != null;
	}

	public String obterNomeUsuario(String jwt) {
		Claims claims = reivindicacoes(jwt);
		return claims == null ? null : claims.getSubject();
	}
}
