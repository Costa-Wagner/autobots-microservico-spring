package com.autobots.automanager.controles;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.excecoes.EmpresaNaoEncontradaException;
import com.autobots.automanager.excecoes.IdNaoInformadoException;
import com.autobots.automanager.modelo.AdicionadorLinkEmpresa;
import com.autobots.automanager.modelo.EmpresaAtualizador;
import com.autobots.automanager.repositorios.EmpresaRepositorio;

@RestController
@RequestMapping("/empresa")
public class EmpresaControle {
	@Autowired
	private EmpresaRepositorio repositorio;
	@Autowired
	private AdicionadorLinkEmpresa adicionadorLink;
	private EmpresaAtualizador atualizador = new EmpresaAtualizador();

	private Empresa buscarEmpresa(long id) {
		return repositorio.findById(id).orElseThrow(() -> new EmpresaNaoEncontradaException(id));
	}

	@GetMapping("/lista")
	public List<Empresa> obterEmpresas() {
		List<Empresa> empresas = repositorio.findAll();
		adicionadorLink.adicionarLink(empresas);
		return empresas;
	}

	@GetMapping("/{id}")
	public ResponseEntity<Empresa> obterEmpresa(@PathVariable long id) {
		Empresa empresa = buscarEmpresa(id);
		adicionadorLink.adicionarLink(empresa);
		return ResponseEntity.ok(empresa);
	}

	@PostMapping("/cadastro")
	public ResponseEntity<Empresa> cadastrarEmpresa(@Valid @RequestBody Empresa empresa) {
		empresa.setId(null);
		empresa.setCadastro(new Date());
		// usuários, mercadorias, serviços e vendas têm CRUD próprio: ignorados aqui
		empresa.setUsuarios(new ArrayList<>());
		empresa.setMercadorias(new ArrayList<>());
		empresa.setServicos(new ArrayList<>());
		empresa.setVendas(new ArrayList<>());
		Empresa salva = repositorio.save(empresa);
		adicionadorLink.adicionarLink(salva);
		return ResponseEntity.status(HttpStatus.CREATED).body(salva);
	}

	@PutMapping("/atualizar")
	public ResponseEntity<Void> atualizarEmpresa(@RequestBody Empresa atualizacao) {
		if (atualizacao.getId() == null) {
			throw new IdNaoInformadoException();
		}
		Empresa empresa = buscarEmpresa(atualizacao.getId());
		atualizador.atualizar(empresa, atualizacao);
		repositorio.save(empresa);
		return ResponseEntity.ok().build();
	}

	@Transactional
	@DeleteMapping("/excluir")
	public ResponseEntity<Void> excluirEmpresa(@RequestBody Empresa exclusao) {
		if (exclusao.getId() == null) {
			throw new IdNaoInformadoException();
		}
		Empresa empresa = buscarEmpresa(exclusao.getId());
		// vendas referenciam usuários, veículos e itens: apagar antes evita violação de chave estrangeira
		empresa.getVendas().clear();
		repositorio.flush();
		repositorio.delete(empresa);
		return ResponseEntity.ok().build();
	}
}
