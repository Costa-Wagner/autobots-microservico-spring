package com.autobots.automanager.controles;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Veiculo;
import com.autobots.automanager.excecoes.ConflitoNegocioException;
import com.autobots.automanager.excecoes.RecursoNaoEncontradoException;
import com.autobots.automanager.modelo.AdicionadorLinkRecursos;
import com.autobots.automanager.modelo.BuscadorUsuario;
import com.autobots.automanager.modelo.VeiculoAtualizador;
import com.autobots.automanager.repositorios.EmpresaRepositorio;

@RestController
@RequestMapping("/empresa/{empresaId}/usuario/{usuarioId}/veiculo")
public class UsuarioVeiculoControle {
	@Autowired
	private EmpresaRepositorio repositorio;
	@Autowired
	private BuscadorUsuario buscador;
	@Autowired
	private AdicionadorLinkRecursos adicionador;
	private VeiculoAtualizador atualizador = new VeiculoAtualizador();

	private Veiculo buscar(Usuario usuario, long id) {
		return usuario.getVeiculos().stream()
				.filter(v -> v.getId().equals(id))
				.findFirst()
				.orElseThrow(() -> new RecursoNaoEncontradoException("Veículo não encontrado", id));
	}

	@GetMapping
	public List<Veiculo> listar(@PathVariable long empresaId, @PathVariable long usuarioId) {
		List<Veiculo> lista = buscador.usuario(buscador.empresa(empresaId), usuarioId).getVeiculos();
		lista.forEach(v -> adicionador.adicionarLink(empresaId, usuarioId, v));
		return lista;
	}

	@GetMapping("/{veiculoId}")
	public Veiculo obter(@PathVariable long empresaId, @PathVariable long usuarioId, @PathVariable long veiculoId) {
		Veiculo v = buscar(buscador.usuario(buscador.empresa(empresaId), usuarioId), veiculoId);
		adicionador.adicionarLink(empresaId, usuarioId, v);
		return v;
	}

	@PostMapping
	public ResponseEntity<Veiculo> cadastrar(@PathVariable long empresaId, @PathVariable long usuarioId,
			@Valid @RequestBody Veiculo veiculo) {
		Empresa empresa = buscador.empresa(empresaId);
		Usuario usuario = buscador.usuario(empresa, usuarioId);
		veiculo.setId(null);
		veiculo.setProprietario(usuario);
		usuario.getVeiculos().add(veiculo);
		Empresa salva = repositorio.save(empresa);
		List<Veiculo> lista = buscador.usuario(salva, usuarioId).getVeiculos();
		Veiculo criado = lista.get(lista.size() - 1);
		adicionador.adicionarLink(empresaId, usuarioId, criado);
		return ResponseEntity.status(HttpStatus.CREATED).body(criado);
	}

	@PutMapping("/{veiculoId}")
	public ResponseEntity<Void> atualizar(@PathVariable long empresaId, @PathVariable long usuarioId,
			@PathVariable long veiculoId, @RequestBody Veiculo atualizacao) {
		Empresa empresa = buscador.empresa(empresaId);
		Usuario usuario = buscador.usuario(empresa, usuarioId);
		atualizador.atualizar(buscar(usuario, veiculoId), atualizacao);
		repositorio.save(empresa);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{veiculoId}")
	public ResponseEntity<Void> excluir(@PathVariable long empresaId, @PathVariable long usuarioId,
			@PathVariable long veiculoId) {
		Empresa empresa = buscador.empresa(empresaId);
		Usuario usuario = buscador.usuario(empresa, usuarioId);
		Veiculo v = buscar(usuario, veiculoId);
		boolean emVenda = empresa.getVendas().stream()
				.anyMatch(x -> x.getVeiculo() != null && x.getVeiculo().getId().equals(veiculoId));
		if (emVenda) {
			throw new ConflitoNegocioException("Veículo consta em vendas e não pode ser excluído");
		}
		usuario.getVeiculos().remove(v);   // orphanRemoval apaga do banco
		repositorio.save(empresa);
		return ResponseEntity.ok().build();
	}
}
