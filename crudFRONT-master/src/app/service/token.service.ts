import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

const TOKEN_KEY = 'AuthToken';

@Injectable({
  providedIn: 'root'
})
export class TokenService {

  roles: Array<string> = [];

  constructor(
    private router: Router
  ) { }

//Método para establecer el token
  public setToken(token: string): void{
    //Eliminamos el token antiguo(en caso de haber)
    window.sessionStorage.removeItem(TOKEN_KEY);
    //Agregamos el nuevo token pasandole como nombre del token el de la constante TOKEN_KEY
    window.sessionStorage.setItem(TOKEN_KEY, token);
  }

//Método para obtener el token
  public getToken(): string{
    return sessionStorage.getItem(TOKEN_KEY);
  }

  public isLogged(): boolean{
    if(this.getToken()){
      return true;
    }
    return false;
  }

  //Método para obtener el nombre de usuario
    public getUserName(): string{

      if(!this.isLogged()){
        return null;
      }

      const token = this.getToken();

      //Obtenemos el payload del token  para poder obtener el usuario (el payload es el segundo elemento de la cadena separa por puntos que es el token)
      const payload = token.split('.')[1];

      //Decodificamos el payload
      const payloadDecoded = atob(payload);

      console.log(payloadDecoded)

      //Parseamos el payloadDecoded
      const values = JSON.parse(payloadDecoded);

      //Obtengo el usuario (sub  es el parametro en el que se encuentra el username)
      const username = values.sub;

      return username;
    }

    public isAdmin(): boolean{

      if(!this.isLogged()){
        return false;
      }

      const token = this.getToken();

      //Obtenemos el payload del token  para poder obtener el usuario (el payload es el segundo elemento de la cadena separa por puntos que es el token)
      const payload = token.split('.')[1];

      //Decodificamos el payload
      const payloadDecoded = atob(payload);

      console.log("Payload Decoded");
      console.log(payloadDecoded);

      //Parseamos el payloadDecoded
      const values = JSON.parse(payloadDecoded);

      //Obtengo el usuario (sub  es el parametro en el que se encuentra el username)
      const roles = values.roles;

      if( roles.indexOf('ROLE_ADMIN') < 0){
        return false;
      }

      return true;
    }

    //Eliminamos todo lo que tengamos en el sessionStorage
    public logOut(): void{
      window.sessionStorage.clear();
      this.router.navigate(['/login']);
    }


}
