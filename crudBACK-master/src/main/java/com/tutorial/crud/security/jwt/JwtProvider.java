package com.tutorial.crud.security.jwt;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.tutorial.crud.security.dto.JwtDto;
import com.tutorial.crud.security.entity.UsuarioPrincipal;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtProvider {
	
	private final static Logger Log = LoggerFactory.getLogger(JwtProvider.class);
	
	//Lo tenemos en el fichero de application.properties
	@Value("${jwt.secret}")
	private String secret;
	
	@Value("${jwt.expiration}")
	private int expiration;
	
	public String generateToken(Authentication authentication) {
		UsuarioPrincipal usuarioPrincipal = (UsuarioPrincipal) authentication.getPrincipal();
		
		List<String> roles = usuarioPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
		
		return Jwts.builder()
				.setSubject(usuarioPrincipal.getUsername())
				.claim("roles", roles)
				.setIssuedAt(new Date())
				.setExpiration(new Date(new Date().getTime() + expiration))
				.signWith(SignatureAlgorithm.HS512, secret.getBytes())
				.compact();
	}
	
	public String getNombreUsuarioFromToken(String token) {
		return Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token).getBody().getSubject();
	}
	
	public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token);
			return true;			
		}catch ( MalformedJwtException e){
			Log.error("token mal formado");
		}catch ( UnsupportedJwtException e){
			Log.error("token no soportado");
		}catch ( ExpiredJwtException e){
			Log.error("token expirado");
		}catch ( IllegalArgumentException e){
			Log.error("token vacio");
		}catch ( SignatureException e){
			Log.error("fallo en la firma");
		}
		
		return false;
	}
	
	
	//M??todo para refrescar el token
	public String refreshToken(JwtDto jwtDto) throws ParseException {
		JWT jwt = JWTParser.parse(jwtDto.getToken());
		
		JWTClaimsSet claims = jwt.getJWTClaimsSet();
		String nombreUsuario = claims.getSubject();
		List<String> roles = (List<String>) claims.getClaim("roles");
		
		return Jwts.builder()
				.setSubject(nombreUsuario)
				.claim("roles", roles)
				.setIssuedAt(new Date())
				.setExpiration(new Date(new Date().getTime() + expiration))
				.signWith(SignatureAlgorithm.HS512, secret.getBytes())
				.compact();
	}
}
