package com.autobots.automanager.controles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.excecoes.EmpresaNaoEncontradaException;
import com.autobots.automanager.modelo.AdicionadorLinkEmpresaEndereco;
import com.autobots.automanager.modelo.EnderecoAtualizador;
import com.autobots.automanager.repositorios.EmpresaRepositorio;
import com.autobots.automanager.configuracao.Regras;

@RestController
@RequestMapping("/empresa/{empresaId}/endereco")
@PreAuthorize(Regras.SOMENTE_ADMIN)
public class EmpresaEnderecoControle {
	@Autowired
	private EmpresaRepositorio repositorio;
	@Autowired
	private AdicionadorLinkEmpresaEndereco adicionador;
	private EnderecoAtualizador atualizador = new EnderecoAtualizador();

	private Empresa buscarEmpresa(long empresaId) {
		return repositorio.findById(empresaId)
				.orElseThrow(() -> new EmpresaNaoEncontradaException(empresaId));
	}

	@PreAuthorize(Regras.LEITURA_TODOS)
	@GetMapping
	public Endereco obter(@PathVariable long empresaId) {
		Endereco endereco = buscarEmpresa(empresaId).getEndereco();
		adicionador.adicionarLink(empresaId, endereco);
		return endereco;
	}

	@PutMapping
	public ResponseEntity<Void> atualizar(@PathVariable long empresaId, @RequestBody Endereco atualizacao) {
		Empresa empresa = buscarEmpresa(empresaId);
		atualizador.atualizar(empresa.getEndereco(), atualizacao);
		repositorio.save(empresa);
		return ResponseEntity.ok().build();
	}
}
