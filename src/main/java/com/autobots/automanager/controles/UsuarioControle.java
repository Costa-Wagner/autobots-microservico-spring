package com.autobots.automanager.controles;

import java.util.ArrayList;
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
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.excecoes.EmpresaNaoEncontradaException;
import com.autobots.automanager.excecoes.UsuarioComVendasException;
import com.autobots.automanager.excecoes.UsuarioNaoEncontradoException;
import com.autobots.automanager.modelo.AdicionadorLinkUsuario;
import com.autobots.automanager.modelo.UsuarioAtualizador;
import com.autobots.automanager.repositorios.EmpresaRepositorio;

@RestController
@RequestMapping("/empresa/{empresaId}/usuario")
public class UsuarioControle {
	@Autowired
	private EmpresaRepositorio repositorio;
	@Autowired
	private AdicionadorLinkUsuario adicionador;
	private UsuarioAtualizador atualizador = new UsuarioAtualizador();

	private Empresa buscarEmpresa(long empresaId) {
		return repositorio.findById(empresaId)
				.orElseThrow(() -> new EmpresaNaoEncontradaException(empresaId));
	}

	private Usuario buscarUsuario(Empresa empresa, long usuarioId) {
		return empresa.getUsuarios().stream()
				.filter(u -> u.getId().equals(usuarioId))
				.findFirst()
				.orElseThrow(() -> new UsuarioNaoEncontradoException(usuarioId));
	}

	// usuário é cliente, funcionário ou dono de veículo em alguma venda da empresa
	private boolean possuiVendas(Empresa empresa, Usuario usuario) {
		Long id = usuario.getId();
		return empresa.getVendas().stream().anyMatch(v ->
				(v.getCliente() != null && id.equals(v.getCliente().getId()))
				|| (v.getFuncionario() != null && id.equals(v.getFuncionario().getId()))
				|| (v.getVeiculo() != null && v.getVeiculo().getProprietario() != null
						&& id.equals(v.getVeiculo().getProprietario().getId())));
	}

	@GetMapping
	public List<Usuario> listar(@PathVariable long empresaId) {
		List<Usuario> lista = buscarEmpresa(empresaId).getUsuarios();
		lista.forEach(u -> adicionador.adicionarLink(empresaId, u));
		return lista;
	}

	@GetMapping("/{usuarioId}")
	public Usuario obter(@PathVariable long empresaId, @PathVariable long usuarioId) {
		Usuario usuario = buscarUsuario(buscarEmpresa(empresaId), usuarioId);
		adicionador.adicionarLink(empresaId, usuario);
		return usuario;
	}

	@PostMapping
	public ResponseEntity<Usuario> cadastrar(@PathVariable long empresaId, @Valid @RequestBody Usuario usuario) {
		Empresa empresa = buscarEmpresa(empresaId);
		usuario.setId(null);   // ignora ids enviados, o banco gera
		usuario.getEndereco().setId(null);
		usuario.getTelefones().forEach(t -> t.setId(null));
		usuario.getDocumentos().forEach(d -> d.setId(null));
		usuario.getEmails().forEach(e -> e.setId(null));
		usuario.setCredenciais(new ArrayList<>());   // CRUD próprio (etapa 4)
		usuario.setMercadorias(new ArrayList<>());
		usuario.setVeiculos(new ArrayList<>());      // CRUD próprio (etapa 6)
		empresa.getUsuarios().add(usuario);
		Empresa salva = repositorio.save(empresa);
		List<Usuario> lista = salva.getUsuarios();
		Usuario criado = lista.get(lista.size() - 1);
		adicionador.adicionarLink(empresaId, criado);
		return ResponseEntity.status(HttpStatus.CREATED).body(criado);
	}

	@PutMapping("/{usuarioId}")
	public ResponseEntity<Void> atualizar(@PathVariable long empresaId, @PathVariable long usuarioId,
			@RequestBody Usuario atualizacao) {
		Empresa empresa = buscarEmpresa(empresaId);
		atualizador.atualizar(buscarUsuario(empresa, usuarioId), atualizacao);
		repositorio.save(empresa);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{usuarioId}")
	public ResponseEntity<Void> excluir(@PathVariable long empresaId, @PathVariable long usuarioId) {
		Empresa empresa = buscarEmpresa(empresaId);
		Usuario usuario = buscarUsuario(empresa, usuarioId);
		if (possuiVendas(empresa, usuario)) {
			throw new UsuarioComVendasException();
		}
		empresa.getUsuarios().remove(usuario);   // orphanRemoval apaga usuário e filhos do banco
		repositorio.save(empresa);
		return ResponseEntity.ok().build();
	}
}
