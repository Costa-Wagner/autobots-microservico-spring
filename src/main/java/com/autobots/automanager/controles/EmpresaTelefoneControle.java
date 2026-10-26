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

import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.excecoes.EmpresaNaoEncontradaException;
import com.autobots.automanager.excecoes.TelefoneNaoEncontradoException;
import com.autobots.automanager.modelo.AdicionadorLinkEmpresaTelefone;
import com.autobots.automanager.modelo.TelefoneAtualizador;
import com.autobots.automanager.repositorios.EmpresaRepositorio;

@RestController
@RequestMapping("/empresa/{empresaId}/telefone")
public class EmpresaTelefoneControle {
	@Autowired
	private EmpresaRepositorio repositorio;
	@Autowired
	private AdicionadorLinkEmpresaTelefone adicionador;
	private TelefoneAtualizador atualizador = new TelefoneAtualizador();

	private Empresa buscarEmpresa(long empresaId) {
		return repositorio.findById(empresaId)
				.orElseThrow(() -> new EmpresaNaoEncontradaException(empresaId));
	}

	private Telefone buscarTelefone(Empresa empresa, long telefoneId) {
		return empresa.getTelefones().stream()
				.filter(t -> t.getId().equals(telefoneId))
				.findFirst()
				.orElseThrow(() -> new TelefoneNaoEncontradoException(telefoneId));
	}

	@GetMapping
	public List<Telefone> listar(@PathVariable long empresaId) {
		List<Telefone> lista = buscarEmpresa(empresaId).getTelefones();
		lista.forEach(t -> adicionador.adicionarLink(empresaId, t));
		return lista;
	}

	@GetMapping("/{telefoneId}")
	public Telefone obter(@PathVariable long empresaId, @PathVariable long telefoneId) {
		Telefone telefone = buscarTelefone(buscarEmpresa(empresaId), telefoneId);
		adicionador.adicionarLink(empresaId, telefone);
		return telefone;
	}

	@PostMapping
	public ResponseEntity<Telefone> cadastrar(@PathVariable long empresaId, @Valid @RequestBody Telefone telefone) {
		Empresa empresa = buscarEmpresa(empresaId);
		telefone.setId(null);   // ignora id enviado, o banco gera
		empresa.getTelefones().add(telefone);
		Empresa salva = repositorio.save(empresa);
		List<Telefone> lista = salva.getTelefones();
		Telefone criado = lista.get(lista.size() - 1);
		adicionador.adicionarLink(empresaId, criado);
		return ResponseEntity.status(HttpStatus.CREATED).body(criado);
	}

	@PutMapping("/{telefoneId}")
	public ResponseEntity<Void> atualizar(@PathVariable long empresaId, @PathVariable long telefoneId,
			@RequestBody Telefone atualizacao) {
		Empresa empresa = buscarEmpresa(empresaId);
		atualizador.atualizar(buscarTelefone(empresa, telefoneId), atualizacao);
		repositorio.save(empresa);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{telefoneId}")
	public ResponseEntity<Void> excluir(@PathVariable long empresaId, @PathVariable long telefoneId) {
		Empresa empresa = buscarEmpresa(empresaId);
		Telefone telefone = buscarTelefone(empresa, telefoneId);
		empresa.getTelefones().remove(telefone);   // orphanRemoval apaga do banco
		repositorio.save(empresa);
		return ResponseEntity.ok().build();
	}
}
