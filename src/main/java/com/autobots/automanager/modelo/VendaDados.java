package com.autobots.automanager.modelo;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class VendaDados {
	@NotBlank(message = "Identificação é obrigatória")
	private String identificacao;
	@NotNull(message = "Cliente é obrigatório")
	private Long clienteId;
	@NotNull(message = "Funcionário é obrigatório")
	private Long funcionarioId;
	private Long veiculoId;
	private List<Long> mercadoriaIds;
	private List<Long> servicoIds;
}
