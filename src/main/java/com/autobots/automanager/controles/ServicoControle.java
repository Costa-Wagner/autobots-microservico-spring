package com.autobots.automanager.controles;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Servico;
import com.autobots.automanager.excecoes.ConflitoNegocioException;
import com.autobots.automanager.excecoes.RecursoNaoEncontradoException;
import com.autobots.automanager.modelo.AdicionadorLinkRecursos;
import com.autobots.automanager.modelo.BuscadorUsuario;
import com.autobots.automanager.modelo.ServicoAtualizador;
import com.autobots.automanager.repositorios.EmpresaRepositorio;

@RestController
@RequestMapping("/empresa/{empresaId}/servico")
public class ServicoControle {
	@Autowired
	private EmpresaRepositorio repositorio;
	@Autowired
	private BuscadorUsuario buscador;
	@Autowired
	private AdicionadorLinkRecursos adicionador;
	private ServicoAtualizador atualizador = new ServicoAtualizador();

	private Servico buscar(Empresa empresa, long id) {
		return empresa.getServicos().stream()
				.filter(s -> s.getId().equals(id))
				.findFirst()
				.orElseThrow(() -> new RecursoNaoEncontradoException("Serviço não encontrado", id));
	}

	@GetMapping
	public List<Servico> listar(@PathVariable long empresaId) {
		List<Servico> lista = buscador.empresa(empresaId).getServicos();
		lista.forEach(s -> adicionador.adicionarLink(empresaId, s));
		return lista;
	}

	@GetMapping("/{servicoId}")
	public Servico obter(@PathVariable long empresaId, @PathVariable long servicoId) {
		Servico s = buscar(buscador.empresa(empresaId), servicoId);
		adicionador.adicionarLink(empresaId, s);
		return s;
	}

	@PostMapping
	public ResponseEntity<Servico> cadastrar(@PathVariable long empresaId, @Valid @RequestBody Servico servico) {
		Empresa empresa = buscador.empresa(empresaId);
		servico.setId(null);
		empresa.getServicos().add(servico);
		Empresa salva = repositorio.save(empresa);
		List<Servico> lista = salva.getServicos();
		Servico criado = lista.get(lista.size() - 1);
		adicionador.adicionarLink(empresaId, criado);
		return ResponseEntity.status(HttpStatus.CREATED).body(criado);
	}

	@PutMapping("/{servicoId}")
	public ResponseEntity<Void> atualizar(@PathVariable long empresaId, @PathVariable long servicoId,
			@RequestBody Servico atualizacao) {
		Empresa empresa = buscador.empresa(empresaId);
		atualizador.atualizar(buscar(empresa, servicoId), atualizacao);
		repositorio.save(empresa);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{servicoId}")
	public ResponseEntity<Void> excluir(@PathVariable long empresaId, @PathVariable long servicoId) {
		Empresa empresa = buscador.empresa(empresaId);
		Servico s = buscar(empresa, servicoId);
		boolean emVenda = empresa.getVendas().stream()
				.anyMatch(v -> v.getServicos().stream().anyMatch(x -> x.getId().equals(servicoId)));
		if (emVenda) {
			throw new ConflitoNegocioException("Serviço consta em vendas e não pode ser excluído");
		}
		empresa.getServicos().remove(s);
		repositorio.save(empresa);
		return ResponseEntity.ok().build();
	}
}
