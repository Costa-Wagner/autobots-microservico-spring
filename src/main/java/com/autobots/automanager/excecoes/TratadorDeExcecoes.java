package com.autobots.automanager.excecoes;	

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

@RestControllerAdvice   // intercepta exceções lançadas por qualquer @RestController do projeto
public class TratadorDeExcecoes {

	// captura especificamente falha de @Valid
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> tratarValidacao(MethodArgumentNotValidException excecao) {
		Map<String, String> erros = new LinkedHashMap<>();           // nome do campo -> mensagem de erro
		for (FieldError erro : excecao.getBindingResult().getFieldErrors()) {
			// getDefaultMessage() pega a messagem da anotação
			erros.put(erro.getField(), erro.getDefaultMessage());	
		}
		// devolve 400 com o mapa de erros em vez do erro cru do Spring
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erros);   
	}

	//erro de constraint do banco (ex: documento duplicado, campo unique repetido) → 409 (conflito)
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Map<String, String>> tratarConflito(DataIntegrityViolationException excecao) {
		Map<String, String> erro = new LinkedHashMap<>();
		erro.put("erro", "Dado duplicado ou inválido (ex: número de documento já cadastrado)");
		return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
	}

	//JSON malformado ou com tipo errado no corpo da requisição → 400
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Map<String, String>> tratarJsonInvalido(HttpMessageNotReadableException excecao) {
		Map<String, String> erro = new LinkedHashMap<>();
		erro.put("erro", "Corpo da requisição inválido ou malformado");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
	}

	
	@ExceptionHandler(ClienteNaoEncontradoException.class)
	public ResponseEntity<Map<String, String>> tratarClienteNaoEncontrado(ClienteNaoEncontradoException excecao) {
		Map<String, String> erro = new LinkedHashMap<>();
		erro.put("erro", excecao.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
	}

	//documento inexistente ou que não pertence ao cliente da rota → 404
	@ExceptionHandler(DocumentoNaoEncontradoException.class)
	public ResponseEntity<Map<String, String>> tratarDocumentoNaoEncontrado(DocumentoNaoEncontradoException excecao) {
		Map<String, String> erro = new LinkedHashMap<>();
		erro.put("erro", excecao.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
	}

	//id ausente no corpo do PUT/DELETE (não confundir com id inexistente, que é 404) → 400 (requisição inválida)
	@ExceptionHandler(IdNaoInformadoException.class)
	public ResponseEntity<Map<String, String>> tratarIdNaoInformado(IdNaoInformadoException excecao) {
		Map<String, String> erro = new LinkedHashMap<>();
		erro.put("erro", excecao.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
	}

	//telefone inexistente ou que não pertence ao cliente da rota → 404
	@ExceptionHandler(TelefoneNaoEncontradoException.class)
	public ResponseEntity<Map<String, String>> tratarTelefoneNaoEncontrado(TelefoneNaoEncontradoException excecao) {
		Map<String, String> erro = new LinkedHashMap<>();
		erro.put("erro", excecao.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
	}

	//tentativa de remover o único documento do cliente → 409 (conflito com a regra de negócio)
	@ExceptionHandler(UltimoDocumentoException.class)
	public ResponseEntity<Map<String, String>> tratarUltimoDocumento(UltimoDocumentoException excecao) {
		Map<String, String> erro = new LinkedHashMap<>();
		erro.put("erro", excecao.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
	}

	//Content-Type diferente de JSON no corpo da requisição → 415
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<Map<String, String>> tratarMediaType(HttpMediaTypeNotSupportedException excecao) {
		Map<String, String> erro = new LinkedHashMap<>();
		erro.put("erro", "Tipo de conteúdo não suportado: use application/json");
		return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(erro);
	}

	//método HTTP inexistente para a rota (ex: POST em /cliente/lista) → 405
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<Map<String, String>> tratarMetodoNaoPermitido(HttpRequestMethodNotSupportedException excecao) {
		Map<String, String> erro = new LinkedHashMap<>();
		erro.put("erro", "Método HTTP não permitido para esta rota");
		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(erro);
	}

	//empresa inexistente → 404
	@ExceptionHandler(EmpresaNaoEncontradaException.class)
	public ResponseEntity<Map<String, String>> tratarEmpresaNaoEncontrada(EmpresaNaoEncontradaException excecao) {
		Map<String, String> erro = new LinkedHashMap<>();
		erro.put("erro", excecao.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
	}

	//usuário inexistente ou que não pertence à empresa da rota → 404
	@ExceptionHandler(UsuarioNaoEncontradoException.class)
	public ResponseEntity<Map<String, String>> tratarUsuarioNaoEncontrado(UsuarioNaoEncontradoException excecao) {
		Map<String, String> erro = new LinkedHashMap<>();
		erro.put("erro", excecao.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
	}

	//usuário com vendas não pode ser excluído → 409
	@ExceptionHandler(UsuarioComVendasException.class)
	public ResponseEntity<Map<String, String>> tratarUsuarioComVendas(UsuarioComVendasException excecao) {
		Map<String, String> erro = new LinkedHashMap<>();
		erro.put("erro", excecao.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
	}

	//e-mail inexistente ou que não pertence ao usuário da rota → 404
	@ExceptionHandler(EmailNaoEncontradoException.class)
	public ResponseEntity<Map<String, String>> tratarEmailNaoEncontrado(EmailNaoEncontradoException excecao) {
		Map<String, String> erro = new LinkedHashMap<>();
		erro.put("erro", excecao.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
	}

	//credencial inexistente ou que não pertence ao usuário da rota → 404
	@ExceptionHandler(CredencialNaoEncontradaException.class)
	public ResponseEntity<Map<String, String>> tratarCredencialNaoEncontrada(CredencialNaoEncontradaException excecao) {
		Map<String, String> erro = new LinkedHashMap<>();
		erro.put("erro", excecao.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
	}

	//PUT tentando trocar o tipo da credencial → 400
	@ExceptionHandler(TipoCredencialDiferenteException.class)
	public ResponseEntity<Map<String, String>> tratarTipoCredencialDiferente(TipoCredencialDiferenteException excecao) {
		Map<String, String> erro = new LinkedHashMap<>();
		erro.put("erro", excecao.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
	}

	//qualquer erro não previsto pelos handlers acima → 500, com mensagem padrão pro cliente (e printStackTrace() pra você ver o que houve no console durante o desenvolvimento)
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, String>> tratarErroGenerico(Exception excecao) {
		excecao.printStackTrace(); // remover ou trocar por logger antes de produção
		Map<String, String> erro = new LinkedHashMap<>();
		erro.put("erro", "Erro inesperado no servidor");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
	}
}