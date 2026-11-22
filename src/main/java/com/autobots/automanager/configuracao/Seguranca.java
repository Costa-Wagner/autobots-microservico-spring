package com.autobots.automanager.configuracao;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.autobots.automanager.adaptadores.UserDetailsServiceImpl;
import com.autobots.automanager.filtros.Autenticador;
import com.autobots.automanager.filtros.Autorizador;
import com.autobots.automanager.jwt.ProvedorJwt;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class Seguranca extends WebSecurityConfigurerAdapter {
	@Autowired
	private UserDetailsServiceImpl servico;
	@Autowired
	private ProvedorJwt provedorJwt;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable();
		// /login é tratado pelo filtro Autenticador, antes desta regra
		http.authorizeRequests()
			.antMatchers("/empresa/*/usuario/**").hasAnyRole("ADMIN", "GERENTE")   // provisório até a R3
			.anyRequest().authenticated();
		http.addFilter(new Autenticador(authenticationManager(), provedorJwt));
		http.addFilter(new Autorizador(authenticationManager(), provedorJwt, servico));
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.exceptionHandling()
				.authenticationEntryPoint((req, res, ex) -> escrever(res, 401, "Autenticação necessária ou token inválido"))
				.accessDeniedHandler((req, res, ex) -> escrever(res, 403, "Acesso negado para o seu perfil"));
	}

	@Override
	protected void configure(AuthenticationManagerBuilder autenticador) throws Exception {
		autenticador.userDetailsService(servico).passwordEncoder(codificador());
	}

	@Bean
	public BCryptPasswordEncoder codificador() {
		return new BCryptPasswordEncoder();
	}

	private static void escrever(HttpServletResponse res, int status, String mensagem) throws IOException {
		res.setStatus(status);
		res.setContentType("application/json;charset=UTF-8");
		res.getWriter().write("{\"erro\": \"" + mensagem + "\"}");
	}
}
