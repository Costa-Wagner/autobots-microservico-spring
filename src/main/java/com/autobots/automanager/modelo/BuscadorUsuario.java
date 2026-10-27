package com.autobots.automanager.modelo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.excecoes.EmpresaNaoEncontradaException;
import com.autobots.automanager.excecoes.UsuarioNaoEncontradoException;
import com.autobots.automanager.repositorios.EmpresaRepositorio;

@Component
public class BuscadorUsuario {
	@Autowired
	private EmpresaRepositorio repositorio;

	public Empresa empresa(long empresaId) {
		return repositorio.findById(empresaId)
				.orElseThrow(() -> new EmpresaNaoEncontradaException(empresaId));
	}

	// só procura dentro da empresa informada: usuário de outra empresa dá 404
	public Usuario usuario(Empresa empresa, long usuarioId) {
		return empresa.getUsuarios().stream()
				.filter(u -> u.getId().equals(usuarioId))
				.findFirst()
				.orElseThrow(() -> new UsuarioNaoEncontradoException(usuarioId));
	}
}
