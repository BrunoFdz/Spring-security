import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../service/auth.service';
import { TokenService } from '../service/token.service';
import { NuevoUsuario} from '../models/nuevo-usuario';

@Component({
  selector: 'app-registro',
  templateUrl: './registro.component.html',
  styleUrls: ['./registro.component.css']
})
export class RegistroComponent implements OnInit {

  nuevoUsuario: NuevoUsuario;
  nombre: string;
  nombreUsuario: string;
  email: string;
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
  onRegister(): void {
    this.nuevoUsuario = new NuevoUsuario(this.nombre, this.nombreUsuario, this.email, this.password);
    this.authService.nuevo(this.nuevoUsuario).subscribe(
      //En data vendrían los campos del JwtDTO en este caso un mensaje de confirmación
      data => {
        alert('Cuenta creada correctamente');

        this.router.navigate(['/login'])
      },
      err =>{
        console.log(err);
        this.errMsj = err.error.mensaje;
        alert(this.errMsj);
      }
    )
  }

}
