package com.autobots.automanager.modelo;

import com.autobots.automanager.entidades.Veiculo;

public class VeiculoAtualizador {
	private StringVerificadorNulo verificador = new StringVerificadorNulo();

	public void atualizar(Veiculo v, Veiculo a) {
		if (a.getTipo() != null) v.setTipo(a.getTipo());
		if (!verificador.verificar(a.getModelo())) v.setModelo(a.getModelo());
		if (!verificador.verificar(a.getPlaca())) v.setPlaca(a.getPlaca());
	}
}
