package com.tutorial.crud.security.controlador;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tutorial.crud.dto.Mensaje;
import com.tutorial.crud.security.dto.JwtDto;
import com.tutorial.crud.security.dto.LoginUsuario;
import com.tutorial.crud.security.dto.NuevoUsuario;
import com.tutorial.crud.security.entity.Rol;
import com.tutorial.crud.security.entity.Usuario;
import com.tutorial.crud.security.enums.RolNombre;
import com.tutorial.crud.security.jwt.JwtProvider;
import com.tutorial.crud.security.service.RolService;
import com.tutorial.crud.security.service.UsuarioService;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UsuarioService usuarioService;

	@Autowired
	RolService rolService;

	@Autowired
	JwtProvider jwtProvider;

	// Metodo para crear nuevo usuario
	@PostMapping("/nuevo")
	public ResponseEntity<?> nuevo(@Valid @RequestBody NuevoUsuario nuevoUsuario, BindingResult bindingResult) {

		// Comprobamos que no haya errores en la validación
		if (bindingResult.hasErrors()) {
			return new ResponseEntity(new Mensaje("campos mal puestos o email inválido"), HttpStatus.BAD_REQUEST);
		}

		// Comprobamos que el nombre de usuario no exista
		if (usuarioService.existsByNombreUsuario(nuevoUsuario.getNombreUsuario())) {
			return new ResponseEntity(new Mensaje("El nombre de usuario ya existe"), HttpStatus.BAD_REQUEST);
		}

		// Comprobamos que el email no exista
		if (usuarioService.existsByEmail(nuevoUsuario.getEmail())) {
			return new ResponseEntity(new Mensaje("El email ya existe"), HttpStatus.BAD_REQUEST);
		}
		// Creamos un nuevo usuario y ciframos la contraseña
		Usuario usuario = new Usuario(nuevoUsuario.getNombre(), nuevoUsuario.getNombreUsuario(),
				nuevoUsuario.getEmail(), passwordEncoder.encode(nuevoUsuario.getPassword()));

		// Creamos un conjunto de roles
		Set<Rol> roles = new HashSet<>();

		// Añadimos el rol user a la lista ya que este rol lo contienen todos los
		// usuarios
		roles.add(rolService.getByRolNombre(RolNombre.ROLE_USER).get());

		// Comprobamos si el nuevo usuario contiene el rol admin y de ser asi se lo
		// asignamos
		if (nuevoUsuario.getRoles().contains("admin")) {
			roles.add(rolService.getByRolNombre(RolNombre.ROLE_ADMIN).get());
		}

		// Añadimos los roles al usuario
		usuario.setRoles(roles);

		// Guardamos el usuario en la base de datos
		usuarioService.save(usuario);

		// Devolvemos confirmando que se ha guardado el usuario
		return new ResponseEntity(new Mensaje("Usuario guardado"), HttpStatus.CREATED);

	}

	@PostMapping("/login")
	public ResponseEntity<JwtDto> login(@Valid @RequestBody LoginUsuario loginUsuario, BindingResult bindingResult) {
		// Comprobamos que no haya errores en la validación
		if (bindingResult.hasErrors()) {
			return new ResponseEntity(new Mensaje("Nombre de usuario o contraseña incorrectos"), HttpStatus.BAD_REQUEST);
		}
		
		//Creamos una autenticacion con los datos del usuario
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUsuario.getNombreUsuario(), loginUsuario.getPassword()));
		
		//Autenticamos al usuario
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		//Generamos el token para el usuario con sus datos
		String jwt = jwtProvider.generateToken(authentication);		
		
		//Creamos un jwtdto que será lo que le devolveremos al usuario que contiene su token el nombre de usuario y sus roles asignados
		JwtDto jwtDto = new JwtDto(jwt);
		
		//Devolvemos el jwtdto
		return new ResponseEntity(jwtDto, HttpStatus.OK);
		
	}
	
	@PostMapping("/refresh")
	public ResponseEntity<JwtDto> refreshToken(@RequestBody JwtDto jwtDto) throws ParseException{
		String token = jwtProvider.refreshToken(jwtDto);
		JwtDto jwt = new JwtDto(token);
		
		return new ResponseEntity(jwt, HttpStatus.OK);
	}

}
