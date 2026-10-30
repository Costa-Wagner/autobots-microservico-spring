package com.autobots.automanager.controles;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Mercadoria;
import com.autobots.automanager.entidades.Servico;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.enumeracoes.PerfilUsuario;
import com.autobots.automanager.excecoes.ConflitoNegocioException;
import com.autobots.automanager.excecoes.RecursoNaoEncontradoException;
import com.autobots.automanager.modelo.AdicionadorLinkVenda;
import com.autobots.automanager.modelo.BuscadorUsuario;
import com.autobots.automanager.modelo.StringVerificadorNulo;
import com.autobots.automanager.modelo.VendaDados;
import com.autobots.automanager.repositorios.EmpresaRepositorio;

@RestController
@RequestMapping("/empresa/{empresaId}/venda")
public class VendaControle {
	@Autowired
	private EmpresaRepositorio repositorio;
	@Autowired
	private BuscadorUsuario buscador;
	@Autowired
	private AdicionadorLinkVenda adicionador;
	private StringVerificadorNulo verificador = new StringVerificadorNulo();

	private Venda buscar(Empresa empresa, long id) {
		return empresa.getVendas().stream()
				.filter(v -> v.getId().equals(id))
				.findFirst()
				.orElseThrow(() -> new RecursoNaoEncontradoException("Venda não encontrada", id));
	}

	private Usuario comPerfil(Empresa empresa, long id, PerfilUsuario perfil) {
		Usuario u = buscador.usuario(empresa, id);
		if (!u.getPerfis().contains(perfil)) {
			throw new ConflitoNegocioException("Usuário " + id + " não possui o perfil " + perfil);
		}
		return u;
	}

	// aplica os dados recebidos; campo omitido (null) = sem alteração
	private void preencher(Venda venda, Empresa empresa, VendaDados d) {
		if (!verificador.verificar(d.getIdentificacao())) {
			venda.setIdentificacao(d.getIdentificacao());
		}
		if (d.getFuncionarioId() != null) {
			venda.setFuncionario(comPerfil(empresa, d.getFuncionarioId(), PerfilUsuario.FUNCIONARIO));
		}
		if (d.getMercadoriaIds() != null) {
			List<Mercadoria> itens = d.getMercadoriaIds().stream().distinct()
					.map(id -> empresa.getMercadorias().stream()
							.filter(m -> m.getId().equals(id)).findFirst()
							.orElseThrow(() -> new RecursoNaoEncontradoException("Mercadoria não encontrada", id)))
					.collect(Collectors.toList());
			venda.getMercadorias().clear();
			venda.getMercadorias().addAll(itens);
		}
		if (d.getServicoIds() != null) {
			List<Servico> itens = d.getServicoIds().stream().distinct()
					.map(id -> empresa.getServicos().stream()
							.filter(s -> s.getId().equals(id)).findFirst()
							.orElseThrow(() -> new RecursoNaoEncontradoException("Serviço não encontrado", id)))
					.collect(Collectors.toList());
			venda.getServicos().clear();
			venda.getServicos().addAll(itens);
		}
		if (d.getVeiculoId() != null) {
			venda.setVeiculo(venda.getCliente().getVeiculos().stream()
					.filter(v -> v.getId().equals(d.getVeiculoId())).findFirst()
					.orElseThrow(() -> new RecursoNaoEncontradoException("Veículo não encontrado", d.getVeiculoId())));
		}
		if (venda.getMercadorias().isEmpty() && venda.getServicos().isEmpty()) {
			throw new ConflitoNegocioException("Venda precisa ter ao menos uma mercadoria ou serviço");
		}
	}

	@GetMapping
	public List<Venda> listar(@PathVariable long empresaId) {
		List<Venda> lista = buscador.empresa(empresaId).getVendas();
		lista.forEach(v -> adicionador.adicionarLink(empresaId, v));
		return lista;
	}

	@GetMapping("/{vendaId}")
	public Venda obter(@PathVariable long empresaId, @PathVariable long vendaId) {
		Venda venda = buscar(buscador.empresa(empresaId), vendaId);
		adicionador.adicionarLink(empresaId, venda);
		return venda;
	}

	@PostMapping
	public ResponseEntity<Venda> cadastrar(@PathVariable long empresaId, @Valid @RequestBody VendaDados dados) {
		Empresa empresa = buscador.empresa(empresaId);
		Venda venda = new Venda();
		venda.setCadastro(new Date());
		venda.setCliente(comPerfil(empresa, dados.getClienteId(), PerfilUsuario.CLIENTE));
		preencher(venda, empresa, dados);
		empresa.getVendas().add(venda);
		Empresa salva = repositorio.save(empresa);
		List<Venda> lista = salva.getVendas();
		Venda criada = lista.get(lista.size() - 1);
		adicionador.adicionarLink(empresaId, criada);
		return ResponseEntity.status(HttpStatus.CREATED).body(criada);
	}

	@PutMapping("/{vendaId}")
	public ResponseEntity<Void> atualizar(@PathVariable long empresaId, @PathVariable long vendaId,
			@RequestBody VendaDados dados) {
		Empresa empresa = buscador.empresa(empresaId);
		preencher(buscar(empresa, vendaId), empresa, dados);   // clienteId é ignorado
		repositorio.save(empresa);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{vendaId}")
	public ResponseEntity<Void> excluir(@PathVariable long empresaId, @PathVariable long vendaId) {
		Empresa empresa = buscador.empresa(empresaId);
		empresa.getVendas().remove(buscar(empresa, vendaId));   // orphanRemoval apaga do banco
		repositorio.save(empresa);
		return ResponseEntity.ok().build();
	}
}
