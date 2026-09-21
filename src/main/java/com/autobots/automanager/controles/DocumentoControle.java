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
import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.excecoes.ClienteNaoEncontradoException;
import com.autobots.automanager.excecoes.DocumentoNaoEncontradoException;
import com.autobots.automanager.excecoes.UltimoDocumentoException;
import com.autobots.automanager.modelo.DocumentoAtualizador;
import com.autobots.automanager.repositorios.ClienteRepositorio;

@RestController
@RequestMapping("/cliente/{clienteId}/documento")
public class DocumentoControle {
	@Autowired
	private ClienteRepositorio repositorio;
	private DocumentoAtualizador atualizador = new DocumentoAtualizador();

	private Cliente buscarCliente(long clienteId) {
		return repositorio.findById(clienteId)
				.orElseThrow(() -> new ClienteNaoEncontradoException(clienteId));
	}

	private Documento buscarDocumento(Cliente cliente, long documentoId) {
		return cliente.getDocumentos().stream()
				.filter(d -> d.getId().equals(documentoId))
				.findFirst()
				.orElseThrow(() -> new DocumentoNaoEncontradoException(documentoId));
	}

	@GetMapping
	public List<Documento> listar(@PathVariable long clienteId) {
		return buscarCliente(clienteId).getDocumentos();
	}

	@GetMapping("/{documentoId}")
	public Documento obter(@PathVariable long clienteId, @PathVariable long documentoId) {
		return buscarDocumento(buscarCliente(clienteId), documentoId);
	}

	@PostMapping
	public ResponseEntity<Documento> cadastrar(@PathVariable long clienteId, @Valid @RequestBody Documento documento) {
		Cliente cliente = buscarCliente(clienteId);
		documento.setId(null);   // ignora id enviado, o banco gera
		cliente.getDocumentos().add(documento);
		Cliente salvo = repositorio.save(cliente);
		List<Documento> lista = salvo.getDocumentos();
		return ResponseEntity.status(HttpStatus.CREATED).body(lista.get(lista.size() - 1));
	}

	@PutMapping("/{documentoId}")
	public ResponseEntity<Void> atualizar(@PathVariable long clienteId, @PathVariable long documentoId,
			@RequestBody Documento atualizacao) {
		Cliente cliente = buscarCliente(clienteId);
		atualizador.atualizar(buscarDocumento(cliente, documentoId), atualizacao);
		repositorio.save(cliente);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{documentoId}")
	public ResponseEntity<Void> excluir(@PathVariable long clienteId, @PathVariable long documentoId) {
		Cliente cliente = buscarCliente(clienteId);
		Documento documento = buscarDocumento(cliente, documentoId);
		if (cliente.getDocumentos().size() <= 1) {
			throw new UltimoDocumentoException();
		}
		cliente.getDocumentos().remove(documento);   // orphanRemoval apaga do banco
		repositorio.save(cliente);
		return ResponseEntity.ok().build();
	}
}
