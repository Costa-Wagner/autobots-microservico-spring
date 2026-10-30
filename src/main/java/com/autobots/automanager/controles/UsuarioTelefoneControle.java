package com.autobots.automanager.controles;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.excecoes.TelefoneNaoEncontradoException;
import com.autobots.automanager.modelo.AdicionadorLinkUsuarioFilho;
import com.autobots.automanager.modelo.BuscadorUsuario;
import com.autobots.automanager.modelo.TelefoneAtualizador;
import com.autobots.automanager.repositorios.EmpresaRepositorio;

@RestController
@RequestMapping("/empresa/{empresaId}/usuario/{usuarioId}/telefone")
public class UsuarioTelefoneControle {
	@Autowired
	private EmpresaRepositorio repositorio;
	@Autowired
	private BuscadorUsuario buscador;
	@Autowired
	private AdicionadorLinkUsuarioFilho adicionador;
	private TelefoneAtualizador atualizador = new TelefoneAtualizador();

	private Telefone buscarTelefone(Usuario usuario, long telefoneId) {
		return usuario.getTelefones().stream()
				.filter(t -> t.getId().equals(telefoneId))
				.findFirst()
				.orElseThrow(() -> new TelefoneNaoEncontradoException(telefoneId));
	}

	@GetMapping
	public List<Telefone> listar(@PathVariable long empresaId, @PathVariable long usuarioId) {
		List<Telefone> lista = buscador.usuario(buscador.empresa(empresaId), usuarioId).getTelefones();
		lista.forEach(t -> adicionador.adicionarLink(empresaId, usuarioId, t));
		return lista;
	}

	@GetMapping("/{telefoneId}")
	public Telefone obter(@PathVariable long empresaId, @PathVariable long usuarioId, @PathVariable long telefoneId) {
		Usuario usuario = buscador.usuario(buscador.empresa(empresaId), usuarioId);
		Telefone telefone = buscarTelefone(usuario, telefoneId);
		adicionador.adicionarLink(empresaId, usuarioId, telefone);
		return telefone;
	}

	@PostMapping
	public ResponseEntity<Telefone> cadastrar(@PathVariable long empresaId, @PathVariable long usuarioId,
			@Valid @RequestBody Telefone telefone) {
		Empresa empresa = buscador.empresa(empresaId);
		Usuario usuario = buscador.usuario(empresa, usuarioId);
		telefone.setId(null);
		usuario.getTelefones().add(telefone);
		Empresa salva = repositorio.save(empresa);
		List<Telefone> lista = buscador.usuario(salva, usuarioId).getTelefones();
		Telefone criado = lista.get(lista.size() - 1);
		adicionador.adicionarLink(empresaId, usuarioId, criado);
		return ResponseEntity.status(HttpStatus.CREATED).body(criado);
	}

	@PutMapping("/{telefoneId}")
	public ResponseEntity<Void> atualizar(@PathVariable long empresaId, @PathVariable long usuarioId,
			@PathVariable long telefoneId, @RequestBody Telefone atualizacao) {
		Empresa empresa = buscador.empresa(empresaId);
		Usuario usuario = buscador.usuario(empresa, usuarioId);
		atualizador.atualizar(buscarTelefone(usuario, telefoneId), atualizacao);
		repositorio.save(empresa);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{telefoneId}")
	public ResponseEntity<Void> excluir(@PathVariable long empresaId, @PathVariable long usuarioId,
			@PathVariable long telefoneId) {
		Empresa empresa = buscador.empresa(empresaId);
		Usuario usuario = buscador.usuario(empresa, usuarioId);
		usuario.getTelefones().remove(buscarTelefone(usuario, telefoneId));   // orphanRemoval apaga do banco
		repositorio.save(empresa);
		return ResponseEntity.ok().build();
	}
}
