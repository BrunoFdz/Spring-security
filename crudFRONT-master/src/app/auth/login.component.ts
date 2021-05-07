import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../service/auth.service';
import { TokenService } from '../service/token.service';
import { LoginUsuario } from '../models/login-usuario';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  loginUsuario: LoginUsuario;
  nombreUsuario: string;
  password: string;
  errMsj: string;

  constructor(
    private tokenService: TokenService,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit() {
  }

  //Utilizando el authService nos logueamos y obtenemos el JwtDTO con (el token, nombre de usuario y los roles)
  onLogin(): void {
    this.loginUsuario = new LoginUsuario(this.nombreUsuario, this.password);
    this.authService.login(this.loginUsuario).subscribe(
      //En data vendrían los campos del JwtDTO
      data => {
        //Guardamos el token en el session storage a través del token service
        this.tokenService.setToken(data.token);
        this.router.navigate(['/'])
      },
      err =>{
        console.log(err);
        this.errMsj = err.error.message;
        console.log(this.errMsj);
      }
    )
  }
}
