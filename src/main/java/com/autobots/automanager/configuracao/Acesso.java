package com.autobots.automanager.configuracao;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.autobots.automanager.adaptadores.UserDetailsImpl;

@Component("acesso")
public class Acesso {
	// ADMIN acessa qualquer empresa; os demais perfis só a própria
	public boolean daEmpresa(long empresaId) {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		if (a == null || !(a.getPrincipal() instanceof UserDetailsImpl)) {
			return false;
		}
		UserDetailsImpl u = (UserDetailsImpl) a.getPrincipal();
		boolean admin = u.getAuthorities().stream().anyMatch(x -> x.getAuthority().equals("ROLE_ADMIN"));
		return admin || (u.getEmpresaId() != null && u.getEmpresaId().longValue() == empresaId);
	}
}