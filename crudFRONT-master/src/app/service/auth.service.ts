import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { NuevoUsuario } from '../models/nuevo-usuario';
import { Observable } from 'rxjs';
import { LoginUsuario } from '../models/login-usuario';
import { JwtDTO } from '../models/jwt-dto';



@Injectable({
  providedIn: 'root'
})
export class AuthService {

  authURL = 'http://localhost:8080/auth';

  constructor(private httpClient: HttpClient) { }

  public nuevo(nuevoUsuario: NuevoUsuario): Observable<any> {
    return this.httpClient.post<any>(`${this.authURL}/nuevo`, nuevoUsuario);
  }

  //Hacemos el login y nos devuelve un objeto de tipo JwtDTO (que contiene el token, tipo que es bearer, nombre de usuario y los roles)
  public login(loginUsuario: LoginUsuario): Observable<JwtDTO> {
    return this.httpClient.post<JwtDTO>(`${this.authURL}/login`, loginUsuario);
  }

  //Método para refrescar le token
  public recargarToken(jwtDto: JwtDTO): Observable<JwtDTO>{
    return this.httpClient.post<JwtDTO>(`${this.authURL}/refresh`, jwtDto);
  }
}
