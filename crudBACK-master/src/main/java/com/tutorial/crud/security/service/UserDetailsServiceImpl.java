package com.tutorial.crud.security.service;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tutorial.crud.security.entity.Usuario;
import com.tutorial.crud.security.entity.UsuarioPrincipal;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
	
	@Autowired
	private UsuarioService usuarioService;
	
	private final static Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException {
		//Al devolver un optional no podemos asignarlo a un usuario tal cual por eso usamos get y de esta manera si lo encuentra lo asigna y si no 
		// devuelve el error UsernameNotFoundException
		Usuario usuario = usuarioService.getByNombreUsuario(nombreUsuario).get();
		
		logger.info(" Metodo loadUserByUsername de  usuarioDetailsService. Nombre de usuario: " + usuario.getRoles() +" ");
		
		//logger.info(" Metodo loadUserByUsername de  usuarioDetailsService. Roles: " + usuario.getRoles() +" ");
		
		
		//Convertimos al usuario en un usuarioDetails con la clase usuarioPrincipal que tenemos creada y el método build
		
		return UsuarioPrincipal.build(usuario);
				
	}

}
