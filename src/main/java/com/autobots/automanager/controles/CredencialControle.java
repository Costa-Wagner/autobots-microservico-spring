package com.autobots.automanager.controles;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entidades.Credencial;
import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.excecoes.CredencialNaoEncontradaException;
import com.autobots.automanager.excecoes.TipoCredencialDiferenteException;
import com.autobots.automanager.modelo.AdicionadorLinkCredencial;
import com.autobots.automanager.modelo.BuscadorUsuario;
import com.autobots.automanager.modelo.CredencialAtualizador;
import com.autobots.automanager.repositorios.EmpresaRepositorio;

@RestController
@RequestMapping("/empresa/{empresaId}/usuario/{usuarioId}/credencial")
public class CredencialControle {
	@Autowired
	private EmpresaRepositorio repositorio;
	@Autowired
	private BuscadorUsuario buscador;
	@Autowired
	private AdicionadorLinkCredencial adicionador;
	private CredencialAtualizador atualizador = new CredencialAtualizador();

	private Credencial buscarCredencial(Usuario usuario, long credencialId) {
		return usuario.getCredenciais().stream()
				.filter(c -> c.getId().equals(credencialId))
				.findFirst()
				.orElseThrow(() -> new CredencialNaoEncontradaException(credencialId));
	}

	@GetMapping
	public List<Credencial> listar(@PathVariable long empresaId, @PathVariable long usuarioId) {
		List<Credencial> lista = buscador.usuario(buscador.empresa(empresaId), usuarioId).getCredenciais();
		lista.forEach(c -> adicionador.adicionarLink(empresaId, usuarioId, c));
		return lista;
	}

	@GetMapping("/{credencialId}")
	public Credencial obter(@PathVariable long empresaId, @PathVariable long usuarioId,
			@PathVariable long credencialId) {
		Usuario usuario = buscador.usuario(buscador.empresa(empresaId), usuarioId);
		Credencial credencial = buscarCredencial(usuario, credencialId);
		adicionador.adicionarLink(empresaId, usuarioId, credencial);
		return credencial;
	}

	@PostMapping
	public ResponseEntity<Credencial> cadastrar(@PathVariable long empresaId, @PathVariable long usuarioId,
			@Valid @RequestBody Credencial credencial) {
		Empresa empresa = buscador.empresa(empresaId);
		Usuario usuario = buscador.usuario(empresa, usuarioId);
		credencial.setId(null);
		credencial.setCriacao(new Date());   // controlados pelo servidor
		credencial.setUltimoAcesso(null);
		credencial.setInativo(Boolean.TRUE.equals(credencial.getInativo()));
		usuario.getCredenciais().add(credencial);
		Empresa salva = repositorio.save(empresa);
		List<Credencial> lista = buscador.usuario(salva, usuarioId).getCredenciais();
		Credencial criada = lista.get(lista.size() - 1);
		adicionador.adicionarLink(empresaId, usuarioId, criada);
		return ResponseEntity.status(HttpStatus.CREATED).body(criada);
	}

	@PutMapping("/{credencialId}")
	public ResponseEntity<Void> atualizar(@PathVariable long empresaId, @PathVariable long usuarioId,
			@PathVariable long credencialId, @RequestBody Credencial atualizacao) {
		Empresa empresa = buscador.empresa(empresaId);
		Usuario usuario = buscador.usuario(empresa, usuarioId);
		Credencial atual = buscarCredencial(usuario, credencialId);
		if (!atual.getClass().equals(atualizacao.getClass())) {
			throw new TipoCredencialDiferenteException();
		}
		atualizador.atualizar(atual, atualizacao);
		repositorio.save(empresa);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{credencialId}")
	public ResponseEntity<Void> excluir(@PathVariable long empresaId, @PathVariable long usuarioId,
			@PathVariable long credencialId) {
		Empresa empresa = buscador.empresa(empresaId);
		Usuario usuario = buscador.usuario(empresa, usuarioId);
		usuario.getCredenciais().remove(buscarCredencial(usuario, credencialId));   // orphanRemoval apaga do banco
		repositorio.save(empresa);
		return ResponseEntity.ok().build();
	}
}
