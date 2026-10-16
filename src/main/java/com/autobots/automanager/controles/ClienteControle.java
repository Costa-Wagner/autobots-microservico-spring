package com.autobots.automanager.controles;

import java.util.Date;
import java.util.List;
import java.util.Optional;
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
import com.autobots.automanager.excecoes.ClienteNaoEncontradoException;
import com.autobots.automanager.excecoes.IdNaoInformadoException;
import com.autobots.automanager.modelo.ClienteAtualizador;
import com.autobots.automanager.modelo.ClienteSelecionador;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.modelo.AdicionadorLinkCliente;

@RestController
@RequestMapping("/cliente")
public class ClienteControle {
	@Autowired
	private ClienteRepositorio repositorio;
	@Autowired
	private ClienteSelecionador selecionador;
	@Autowired
	private AdicionadorLinkCliente adicionadorLink;

	@GetMapping("/{id}")
	public ResponseEntity<Cliente> obterCliente(@PathVariable long id) {
		List<Cliente> clientes = repositorio.findAll();
		Cliente cliente = selecionador.selecionar(clientes, id);
		if (cliente == null) {
			throw new ClienteNaoEncontradoException(id);
		}
		adicionadorLink.adicionarLink(cliente);
		return ResponseEntity.ok(cliente);
	}

	@GetMapping("/lista")
	public List<Cliente> obterClientes() {
		List<Cliente> clientes = repositorio.findAll();
		adicionadorLink.adicionarLink(clientes);
		return clientes;
	}

	@PostMapping("/cadastro")
	public ResponseEntity<Cliente> cadastrarCliente(@Valid @RequestBody Cliente cliente) {
		cliente.setDataCadastro(new Date());
		Cliente salvo = repositorio.save(cliente);
		adicionadorLink.adicionarLink(salvo);
		return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
	}

	@PutMapping("/atualizar")
	public ResponseEntity<Void> atualizarCliente(@RequestBody Cliente atualizacao) {
		if (atualizacao.getId() == null) {
			throw new IdNaoInformadoException();
		}
		Optional<Cliente> clienteOpt = repositorio.findById(atualizacao.getId());
		if (clienteOpt.isEmpty()) {
			throw new ClienteNaoEncontradoException(atualizacao.getId());
		}
		Cliente cliente = clienteOpt.get();
		ClienteAtualizador atualizador = new ClienteAtualizador();
		atualizador.atualizar(cliente, atualizacao);
		repositorio.save(cliente);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/excluir")
	public ResponseEntity<Void> excluirCliente(@RequestBody Cliente exclusao) {
		if (exclusao.getId() == null) {
			throw new IdNaoInformadoException();
		}
		Optional<Cliente> clienteOpt = repositorio.findById(exclusao.getId());
		if (clienteOpt.isEmpty()) {
			throw new ClienteNaoEncontradoException(exclusao.getId());
		}
		repositorio.delete(clienteOpt.get());
		return ResponseEntity.ok().build();
	}
}
