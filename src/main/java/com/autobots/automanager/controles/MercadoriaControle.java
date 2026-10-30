package com.autobots.automanager.controles;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Mercadoria;
import com.autobots.automanager.excecoes.ConflitoNegocioException;
import com.autobots.automanager.excecoes.RecursoNaoEncontradoException;
import com.autobots.automanager.modelo.AdicionadorLinkRecursos;
import com.autobots.automanager.modelo.BuscadorUsuario;
import com.autobots.automanager.modelo.MercadoriaAtualizador;
import com.autobots.automanager.repositorios.EmpresaRepositorio;

@RestController
@RequestMapping("/empresa/{empresaId}/mercadoria")
public class MercadoriaControle {
	@Autowired
	private EmpresaRepositorio repositorio;
	@Autowired
	private BuscadorUsuario buscador;
	@Autowired
	private AdicionadorLinkRecursos adicionador;
	private MercadoriaAtualizador atualizador = new MercadoriaAtualizador();

	private Mercadoria buscar(Empresa empresa, long id) {
		return empresa.getMercadorias().stream()
				.filter(m -> m.getId().equals(id))
				.findFirst()
				.orElseThrow(() -> new RecursoNaoEncontradoException("Mercadoria não encontrada", id));
	}

	@GetMapping
	public List<Mercadoria> listar(@PathVariable long empresaId) {
		List<Mercadoria> lista = buscador.empresa(empresaId).getMercadorias();
		lista.forEach(m -> adicionador.adicionarLink(empresaId, m));
		return lista;
	}

	@GetMapping("/{mercadoriaId}")
	public Mercadoria obter(@PathVariable long empresaId, @PathVariable long mercadoriaId) {
		Mercadoria m = buscar(buscador.empresa(empresaId), mercadoriaId);
		adicionador.adicionarLink(empresaId, m);
		return m;
	}

	@PostMapping
	public ResponseEntity<Mercadoria> cadastrar(@PathVariable long empresaId, @Valid @RequestBody Mercadoria mercadoria) {
		Empresa empresa = buscador.empresa(empresaId);
		mercadoria.setId(null);
		mercadoria.setCadastro(new Date());
		empresa.getMercadorias().add(mercadoria);
		Empresa salva = repositorio.save(empresa);
		List<Mercadoria> lista = salva.getMercadorias();
		Mercadoria criada = lista.get(lista.size() - 1);
		adicionador.adicionarLink(empresaId, criada);
		return ResponseEntity.status(HttpStatus.CREATED).body(criada);
	}

	@PutMapping("/{mercadoriaId}")
	public ResponseEntity<Void> atualizar(@PathVariable long empresaId, @PathVariable long mercadoriaId,
			@RequestBody Mercadoria atualizacao) {
		Empresa empresa = buscador.empresa(empresaId);
		atualizador.atualizar(buscar(empresa, mercadoriaId), atualizacao);
		repositorio.save(empresa);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{mercadoriaId}")
	public ResponseEntity<Void> excluir(@PathVariable long empresaId, @PathVariable long mercadoriaId) {
		Empresa empresa = buscador.empresa(empresaId);
		Mercadoria m = buscar(empresa, mercadoriaId);
		boolean emVenda = empresa.getVendas().stream()
				.anyMatch(v -> v.getMercadorias().stream().anyMatch(x -> x.getId().equals(mercadoriaId)));
		if (emVenda) {
			throw new ConflitoNegocioException("Mercadoria consta em vendas e não pode ser excluída");
		}
		// desfaz o vínculo com fornecedores antes de apagar (chave estrangeira)
		empresa.getUsuarios().forEach(u -> u.getMercadorias().removeIf(x -> x.getId().equals(mercadoriaId)));
		empresa.getMercadorias().remove(m);
		repositorio.save(empresa);
		return ResponseEntity.ok().build();
	}
}
