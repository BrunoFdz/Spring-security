import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot} from '@angular/router';
import { TokenService } from '../service/token.service';

@Injectable({
  providedIn: 'root'
})
export class ProdGuardService implements CanActivate{

  realRol: string;

  constructor(private tokenService: TokenService, private router: Router) { }

  canActivate(
    route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean{

      //Si no esta logueado , es decir no tiene ningún token lo redirigimos al login directamente
      if(!this.tokenService.isLogged()){
          this.router.navigate(['/login']);
          return false;
      }

      //Obtenemos el rol que esperamos que tenga, esto se lo pasamaos en app.routing.module como un  array
      const expectedRol = route.data.expectedRol;

      //Comprobamos si es admin, de ser así le asignamos el rol admin si no le asignamos el de user
      this.realRol = this.tokenService.isAdmin() ? 'admin' : 'user';

      if(expectedRol.indexOf(this.realRol) < 0){
          this.router.navigate(['/']);
          return false;
      }

      return true;
  }
}
