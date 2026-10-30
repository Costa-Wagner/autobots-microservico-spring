package com.autobots.automanager.controles;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entidades.Email;
import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.excecoes.EmailNaoEncontradoException;
import com.autobots.automanager.modelo.AdicionadorLinkUsuarioFilho;
import com.autobots.automanager.modelo.BuscadorUsuario;
import com.autobots.automanager.modelo.EmailAtualizador;
import com.autobots.automanager.repositorios.EmpresaRepositorio;

@RestController
@RequestMapping("/empresa/{empresaId}/usuario/{usuarioId}/email")
public class UsuarioEmailControle {
	@Autowired
	private EmpresaRepositorio repositorio;
	@Autowired
	private BuscadorUsuario buscador;
	@Autowired
	private AdicionadorLinkUsuarioFilho adicionador;
	private EmailAtualizador atualizador = new EmailAtualizador();

	private Email buscarEmail(Usuario usuario, long emailId) {
		return usuario.getEmails().stream()
				.filter(e -> e.getId().equals(emailId))
				.findFirst()
				.orElseThrow(() -> new EmailNaoEncontradoException(emailId));
	}

	@GetMapping
	public List<Email> listar(@PathVariable long empresaId, @PathVariable long usuarioId) {
		List<Email> lista = buscador.usuario(buscador.empresa(empresaId), usuarioId).getEmails();
		lista.forEach(e -> adicionador.adicionarLink(empresaId, usuarioId, e));
		return lista;
	}

	@GetMapping("/{emailId}")
	public Email obter(@PathVariable long empresaId, @PathVariable long usuarioId, @PathVariable long emailId) {
		Usuario usuario = buscador.usuario(buscador.empresa(empresaId), usuarioId);
		Email email = buscarEmail(usuario, emailId);
		adicionador.adicionarLink(empresaId, usuarioId, email);
		return email;
	}

	@PostMapping
	public ResponseEntity<Email> cadastrar(@PathVariable long empresaId, @PathVariable long usuarioId,
			@Valid @RequestBody Email email) {
		Empresa empresa = buscador.empresa(empresaId);
		Usuario usuario = buscador.usuario(empresa, usuarioId);
		email.setId(null);
		usuario.getEmails().add(email);
		Empresa salva = repositorio.save(empresa);
		List<Email> lista = buscador.usuario(salva, usuarioId).getEmails();
		Email criado = lista.get(lista.size() - 1);
		adicionador.adicionarLink(empresaId, usuarioId, criado);
		return ResponseEntity.status(HttpStatus.CREATED).body(criado);
	}

	@PutMapping("/{emailId}")
	public ResponseEntity<Void> atualizar(@PathVariable long empresaId, @PathVariable long usuarioId,
			@PathVariable long emailId, @RequestBody Email atualizacao) {
		Empresa empresa = buscador.empresa(empresaId);
		Usuario usuario = buscador.usuario(empresa, usuarioId);
		atualizador.atualizar(buscarEmail(usuario, emailId), atualizacao);
		repositorio.save(empresa);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{emailId}")
	public ResponseEntity<Void> excluir(@PathVariable long empresaId, @PathVariable long usuarioId,
			@PathVariable long emailId) {
		Empresa empresa = buscador.empresa(empresaId);
		Usuario usuario = buscador.usuario(empresa, usuarioId);
		usuario.getEmails().remove(buscarEmail(usuario, emailId));   // orphanRemoval apaga do banco
		repositorio.save(empresa);
		return ResponseEntity.ok().build();
	}
}
