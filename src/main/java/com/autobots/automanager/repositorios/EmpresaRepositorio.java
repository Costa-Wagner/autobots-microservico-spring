package com.autobots.automanager.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.autobots.automanager.entidades.Empresa;

public interface EmpresaRepositorio extends JpaRepository<Empresa, Long> {
	@Query("select e.id from Empresa e join e.usuarios u where u.id = :usuarioId")
	Optional<Long> buscarIdPorUsuario(@Param("usuarioId") Long usuarioId);
}