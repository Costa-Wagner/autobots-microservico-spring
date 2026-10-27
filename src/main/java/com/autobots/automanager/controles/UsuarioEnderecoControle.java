package com.autobots.automanager.controles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.modelo.AdicionadorLinkUsuarioFilho;
import com.autobots.automanager.modelo.BuscadorUsuario;
import com.autobots.automanager.modelo.EnderecoAtualizador;
import com.autobots.automanager.repositorios.EmpresaRepositorio;

@RestController
@RequestMapping("/empresa/{empresaId}/usuario/{usuarioId}/endereco")
public class UsuarioEnderecoControle {
	@Autowired
	private EmpresaRepositorio repositorio;
	@Autowired
	private BuscadorUsuario buscador;
	@Autowired
	private AdicionadorLinkUsuarioFilho adicionador;
	private EnderecoAtualizador atualizador = new EnderecoAtualizador();

	@GetMapping
	public Endereco obter(@PathVariable long empresaId, @PathVariable long usuarioId) {
		Endereco endereco = buscador.usuario(buscador.empresa(empresaId), usuarioId).getEndereco();
		adicionador.adicionarLink(empresaId, usuarioId, endereco);
		return endereco;
	}

	@PutMapping
	public ResponseEntity<Void> atualizar(@PathVariable long empresaId, @PathVariable long usuarioId,
			@RequestBody Endereco atualizacao) {
		Empresa empresa = buscador.empresa(empresaId);
		Usuario usuario = buscador.usuario(empresa, usuarioId);
		atualizador.atualizar(usuario.getEndereco(), atualizacao);
		repositorio.save(empresa);
		return ResponseEntity.ok().build();
	}
}
