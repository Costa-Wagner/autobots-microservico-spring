package com.autobots.automanager.controles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.excecoes.ClienteNaoEncontradoException;
import com.autobots.automanager.modelo.EnderecoAtualizador;
import com.autobots.automanager.repositorios.ClienteRepositorio;

@RestController
@RequestMapping("/cliente/{clienteId}/endereco")
public class EnderecoControle {
	@Autowired
	private ClienteRepositorio repositorio;
	private EnderecoAtualizador atualizador = new EnderecoAtualizador();

	private Cliente buscarCliente(long clienteId) {
		return repositorio.findById(clienteId)
				.orElseThrow(() -> new ClienteNaoEncontradoException(clienteId));
	}

	@GetMapping
	public Endereco obter(@PathVariable long clienteId) {
		return buscarCliente(clienteId).getEndereco();
	}

	@PutMapping
	public ResponseEntity<Void> atualizar(@PathVariable long clienteId, @RequestBody Endereco atualizacao) {
		Cliente cliente = buscarCliente(clienteId);
		atualizador.atualizar(cliente.getEndereco(), atualizacao);
		repositorio.save(cliente);
		return ResponseEntity.ok().build();
	}
}