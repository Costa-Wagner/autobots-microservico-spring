package com.autobots.automanager.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.autobots.automanager.entidades.Usuario;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
	// nomeUsuario é único; só credenciais do tipo USUARIO_SENHA têm esse campo
	@Query("select u from Usuario u, CredencialUsuarioSenha c "
			+ "where c member of u.credenciais and c.nomeUsuario = :nome")
	Optional<Usuario> buscarPorNomeUsuario(@Param("nome") String nome);
}