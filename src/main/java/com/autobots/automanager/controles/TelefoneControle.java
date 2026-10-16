package com.autobots.automanager.controles;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.excecoes.ClienteNaoEncontradoException;
import com.autobots.automanager.excecoes.TelefoneNaoEncontradoException;
import com.autobots.automanager.modelo.TelefoneAtualizador;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.modelo.AdicionadorLinkTelefone;

@RestController
@RequestMapping("/cliente/{clienteId}/telefone")
public class TelefoneControle {
	@Autowired
	private ClienteRepositorio repositorio;
	@Autowired
	private AdicionadorLinkTelefone adicionador;
	private TelefoneAtualizador atualizador = new TelefoneAtualizador();

	private Cliente buscarCliente(long clienteId) {
		return repositorio.findById(clienteId)
				.orElseThrow(() -> new ClienteNaoEncontradoException(clienteId));
	}

	private Telefone buscarTelefone(Cliente cliente, long telefoneId) {
		return cliente.getTelefones().stream()
				.filter(t -> t.getId().equals(telefoneId))
				.findFirst()
				.orElseThrow(() -> new TelefoneNaoEncontradoException(telefoneId));
	}

	@GetMapping
	public List<Telefone> listar(@PathVariable long clienteId) {
		List<Telefone> lista = buscarCliente(clienteId).getTelefones();
		lista.forEach(t -> adicionador.adicionarLink(clienteId, t));
		return lista;
	}

	@GetMapping("/{telefoneId}")
	public Telefone obter(@PathVariable long clienteId, @PathVariable long telefoneId) {
		Telefone telefone = buscarTelefone(buscarCliente(clienteId), telefoneId);
		adicionador.adicionarLink(clienteId, telefone);
		return telefone;
	}

	@PostMapping
	public ResponseEntity<Telefone> cadastrar(@PathVariable long clienteId, @Valid @RequestBody Telefone telefone) {
		Cliente cliente = buscarCliente(clienteId);
		telefone.setId(null);   // ignora id enviado, o banco gera
		cliente.getTelefones().add(telefone);
		Cliente salvo = repositorio.save(cliente);
		List<Telefone> lista = salvo.getTelefones();
		Telefone criado = lista.get(lista.size() - 1);
		adicionador.adicionarLink(clienteId, criado);
		return ResponseEntity.status(HttpStatus.CREATED).body(criado);
	}

	@PutMapping("/{telefoneId}")
	public ResponseEntity<Void> atualizar(@PathVariable long clienteId, @PathVariable long telefoneId,
			@RequestBody Telefone atualizacao) {
		Cliente cliente = buscarCliente(clienteId);
		atualizador.atualizar(buscarTelefone(cliente, telefoneId), atualizacao);
		repositorio.save(cliente);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{telefoneId}")
	public ResponseEntity<Void> excluir(@PathVariable long clienteId, @PathVariable long telefoneId) {
		Cliente cliente = buscarCliente(clienteId);
		Telefone telefone = buscarTelefone(cliente, telefoneId);
		cliente.getTelefones().remove(telefone);   // orphanRemoval apaga do banco
		repositorio.save(cliente);
		return ResponseEntity.ok().build();
	}
}