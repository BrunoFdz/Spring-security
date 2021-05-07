import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { TokenService } from '../service/token.service';

@Injectable({
  providedIn: 'root'
})
export class LoginGuard implements CanActivate {

  constructor(private tokenService: TokenService, private router: Router) { }

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): boolean {

    //Guard para cuando se accede al login

    //Comprobamos si ya esta logueado y de ser asi lo enviamos al home
    if(this.tokenService.isLogged()){
      this.router.navigate(['/'])
      return false;
    }

    return true;
  }

}
