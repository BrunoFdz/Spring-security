import { Component, OnInit } from '@angular/core';
import { TokenService } from '../service/token.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class MenuComponent implements OnInit {

  constructor(private tokenService: TokenService
    ,private router: Router) { }

  ngOnInit() {
  }

  isLogged(): boolean{
    if(this.tokenService.isLogged()){
      return true;
    } else{
      return false;
    }
  }

  onLogOut(): void {
    this.tokenService.logOut();
  }

}
