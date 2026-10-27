package com.autobots.automanager.controles;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.excecoes.DocumentoNaoEncontradoException;
import com.autobots.automanager.excecoes.UltimoDocumentoException;
import com.autobots.automanager.modelo.AdicionadorLinkUsuarioFilho;
import com.autobots.automanager.modelo.BuscadorUsuario;
import com.autobots.automanager.modelo.DocumentoAtualizador;
import com.autobots.automanager.repositorios.EmpresaRepositorio;

@RestController
@RequestMapping("/empresa/{empresaId}/usuario/{usuarioId}/documento")
public class UsuarioDocumentoControle {
	@Autowired
	private EmpresaRepositorio repositorio;
	@Autowired
	private BuscadorUsuario buscador;
	@Autowired
	private AdicionadorLinkUsuarioFilho adicionador;
	private DocumentoAtualizador atualizador = new DocumentoAtualizador();

	private Documento buscarDocumento(Usuario usuario, long documentoId) {
		return usuario.getDocumentos().stream()
				.filter(d -> d.getId().equals(documentoId))
				.findFirst()
				.orElseThrow(() -> new DocumentoNaoEncontradoException(documentoId));
	}

	@GetMapping
	public List<Documento> listar(@PathVariable long empresaId, @PathVariable long usuarioId) {
		List<Documento> lista = buscador.usuario(buscador.empresa(empresaId), usuarioId).getDocumentos();
		lista.forEach(d -> adicionador.adicionarLink(empresaId, usuarioId, d));
		return lista;
	}

	@GetMapping("/{documentoId}")
	public Documento obter(@PathVariable long empresaId, @PathVariable long usuarioId, @PathVariable long documentoId) {
		Usuario usuario = buscador.usuario(buscador.empresa(empresaId), usuarioId);
		Documento documento = buscarDocumento(usuario, documentoId);
		adicionador.adicionarLink(empresaId, usuarioId, documento);
		return documento;
	}

	@PostMapping
	public ResponseEntity<Documento> cadastrar(@PathVariable long empresaId, @PathVariable long usuarioId,
			@Valid @RequestBody Documento documento) {
		Empresa empresa = buscador.empresa(empresaId);
		Usuario usuario = buscador.usuario(empresa, usuarioId);
		documento.setId(null);
		usuario.getDocumentos().add(documento);
		Empresa salva = repositorio.save(empresa);
		List<Documento> lista = buscador.usuario(salva, usuarioId).getDocumentos();
		Documento criado = lista.get(lista.size() - 1);
		adicionador.adicionarLink(empresaId, usuarioId, criado);
		return ResponseEntity.status(HttpStatus.CREATED).body(criado);
	}

	@PutMapping("/{documentoId}")
	public ResponseEntity<Void> atualizar(@PathVariable long empresaId, @PathVariable long usuarioId,
			@PathVariable long documentoId, @RequestBody Documento atualizacao) {
		Empresa empresa = buscador.empresa(empresaId);
		Usuario usuario = buscador.usuario(empresa, usuarioId);
		atualizador.atualizar(buscarDocumento(usuario, documentoId), atualizacao);
		repositorio.save(empresa);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{documentoId}")
	public ResponseEntity<Void> excluir(@PathVariable long empresaId, @PathVariable long usuarioId,
			@PathVariable long documentoId) {
		Empresa empresa = buscador.empresa(empresaId);
		Usuario usuario = buscador.usuario(empresa, usuarioId);
		Documento documento = buscarDocumento(usuario, documentoId);
		if (usuario.getDocumentos().size() <= 1) {
			throw new UltimoDocumentoException();
		}
		usuario.getDocumentos().remove(documento);   // orphanRemoval apaga do banco
		repositorio.save(empresa);
		return ResponseEntity.ok().build();
	}
}
