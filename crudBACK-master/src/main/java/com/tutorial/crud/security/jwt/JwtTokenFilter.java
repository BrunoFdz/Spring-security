package com.tutorial.crud.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tutorial.crud.security.service.UserDetailsServiceImpl;

public class JwtTokenFilter extends OncePerRequestFilter{
	
	private final static Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);
	
	@Autowired
	private JwtProvider jwtProvider;
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService; 

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
		try {
			//Obtenemos el token sin  la cabecera Bearer
			String token = this.getToken(req);
			//Comprobamos que el token no sea nulo y que sea válido
			if(token != null && jwtProvider.validateToken(token)) {
				
				//Obtenemos el nombre de usuario del token
				String nombreUsuario = jwtProvider.getNombreUsuarioFromToken(token);
				
				//Obtenemos al usuario  a partir de su nombre de usuario
				UserDetails userDetails = userDetailsService.loadUserByUsername(nombreUsuario);
				
				//Creamos una autenticacion para el usuario
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());
				
				//Añadimos al contexto de seguridad al usuario
				SecurityContextHolder.getContext().setAuthentication(auth);
				
			}
		}catch(Exception e) {
			logger.error("fail en el metodo doFilterInternal" + e.getMessage());
		}
		
		filterChain.doFilter(req, res);
		
	}
	
	//Metodo que elimina la palabra Bearer de la cabecera para obtener el token unicamente
	private String getToken(HttpServletRequest req) {
		String header = req.getHeader("Authorization");
		
		if(header != null && header.startsWith("Bearer")) {
			return header.replace("Bearer ", "");
		}
		
		return null;
	}

}
