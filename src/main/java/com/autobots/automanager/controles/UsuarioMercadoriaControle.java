package com.autobots.automanager.controles;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Mercadoria;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.enumeracoes.PerfilUsuario;
import com.autobots.automanager.excecoes.ConflitoNegocioException;
import com.autobots.automanager.excecoes.RecursoNaoEncontradoException;
import com.autobots.automanager.modelo.AdicionadorLinkRecursos;
import com.autobots.automanager.modelo.BuscadorUsuario;
import com.autobots.automanager.repositorios.EmpresaRepositorio;

// vínculo fornecedor <-> mercadoria (a mercadoria em si é criada em /empresa/{id}/mercadoria)
@RestController
@RequestMapping("/empresa/{empresaId}/usuario/{usuarioId}/mercadoria")
public class UsuarioMercadoriaControle {
	@Autowired
	private EmpresaRepositorio repositorio;
	@Autowired
	private BuscadorUsuario buscador;
	@Autowired
	private AdicionadorLinkRecursos adicionador;

	@GetMapping
	public List<Mercadoria> listar(@PathVariable long empresaId, @PathVariable long usuarioId) {
		List<Mercadoria> lista = buscador.usuario(buscador.empresa(empresaId), usuarioId).getMercadorias();
		lista.forEach(m -> adicionador.adicionarLink(empresaId, m));
		return lista;
	}

	@PutMapping("/{mercadoriaId}")
	public ResponseEntity<Void> associar(@PathVariable long empresaId, @PathVariable long usuarioId,
			@PathVariable long mercadoriaId) {
		Empresa empresa = buscador.empresa(empresaId);
		Usuario usuario = buscador.usuario(empresa, usuarioId);
		if (!usuario.getPerfis().contains(PerfilUsuario.FORNECEDOR)) {
			throw new ConflitoNegocioException("Somente usuário com perfil FORNECEDOR pode ter mercadorias");
		}
		Mercadoria m = empresa.getMercadorias().stream()
				.filter(x -> x.getId().equals(mercadoriaId))
				.findFirst()
				.orElseThrow(() -> new RecursoNaoEncontradoException("Mercadoria não encontrada", mercadoriaId));
		boolean outroFornecedor = empresa.getUsuarios().stream()
				.anyMatch(u -> !u.getId().equals(usuarioId)
						&& u.getMercadorias().stream().anyMatch(x -> x.getId().equals(mercadoriaId)));
		if (outroFornecedor) {
			throw new ConflitoNegocioException("Mercadoria já vinculada a outro fornecedor");
		}
		if (usuario.getMercadorias().stream().noneMatch(x -> x.getId().equals(mercadoriaId))) {
			usuario.getMercadorias().add(m);
		}
		repositorio.save(empresa);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{mercadoriaId}")
	public ResponseEntity<Void> desassociar(@PathVariable long empresaId, @PathVariable long usuarioId,
			@PathVariable long mercadoriaId) {
		Empresa empresa = buscador.empresa(empresaId);
		Usuario usuario = buscador.usuario(empresa, usuarioId);
		boolean removeu = usuario.getMercadorias().removeIf(x -> x.getId().equals(mercadoriaId));
		if (!removeu) {
			throw new RecursoNaoEncontradoException("Mercadoria não vinculada ao usuário", mercadoriaId);
		}
		repositorio.save(empresa);   // só desfaz o vínculo; a mercadoria continua na empresa
		return ResponseEntity.ok().build();
	}
}
