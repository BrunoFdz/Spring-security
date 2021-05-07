import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable, throwError  } from 'rxjs';
import { TokenService } from '../service/token.service';
import { catchError, concatMap} from 'rxjs/operators';
import { JwtDTO } from '../models/jwt-dto';
import { AuthService } from '../service/auth.service';

const AUTHORIZATION = 'Authorization';

@Injectable({
  providedIn: 'root'
})
export class ProdInterceptorService implements HttpInterceptor{

  constructor(private tokenService: TokenService
  , private authService: AuthService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler):
    Observable<HttpEvent<any>> {

      //Comprobamos que este logueado
      if(!this.tokenService.isLogged()){
        return next.handle(req);
      }

      //Obtenemos el token
      const token = this.tokenService.getToken();


      let intReq = this.addToken(req, token);

      return next.handle(intReq).pipe(catchError( err => {
        if(err.status === 401){
          const dto: JwtDTO = new JwtDTO(this.tokenService.getToken());

          return this.authService.recargarToken(dto).pipe(concatMap(data => {
            console.log("refreshing");
            this.tokenService.setToken(data.token);
            let intReq = this.addToken(req, data.token);
            return next.handle(intReq);
          }));
        }else{
          return throwError(err);
        }
      }));
    }

    private addToken(req: HttpRequest<any>, token: string): HttpRequest<any>{
      return req.clone({headers: req.headers.set(AUTHORIZATION, 'Bearer ' + token)});
    }

}
