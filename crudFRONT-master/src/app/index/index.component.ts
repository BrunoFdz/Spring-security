import { Component, OnInit } from '@angular/core';
import { TokenService } from '../service/token.service';

@Component({
  selector: 'app-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.css']
})
export class IndexComponent implements OnInit {

  nombreUsuario: string;

  constructor(private tokenService: TokenService) { }

  ngOnInit() {

  }

  isLogged(): boolean{
    if(this.tokenService.getToken()){
      this.nombreUsuario = this.tokenService.getUserName();
      return true;
    } else {
      this.nombreUsuario = '';
      return false;
    }
  }


}
