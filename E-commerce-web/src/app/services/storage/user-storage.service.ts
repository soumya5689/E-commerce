import { Injectable } from '@angular/core';

const TOKEN = 'ecom-token';
const USER = 'ecom-user';

@Injectable({
  providedIn: 'root'
})
export class UserStorageService {

  constructor() { }

  public saveToken(token: string): void {    
    localStorage.removeItem(TOKEN);
    localStorage.setItem(TOKEN, token);
  }
  public saveUser(user): void {
    localStorage.removeItem(USER);
    localStorage.setItem(USER, JSON.stringify(user));
  }
  static getToken(): any {
    if(typeof window!== 'undefined')
    return localStorage.getItem(TOKEN);
  }
  static getUser(): any {
    if(typeof window!== 'undefined')
      return JSON.parse(localStorage.getItem(USER));
  }
  static getUserId(): string {
    const user = this.getUser();
    if (user == null) {
      return '';
    }
    return user.userId;
  }
  static getUserRole(): string {
    const user = this.getUser();
    if (user == null) {
      return '';
    }
    return user.role;
  }
  static isAdminLoggedIn(): boolean {
    if (this.getToken === null) {
      return false;
    }
    const role: string = this.getUserRole();
    return role == 'ADMIN';
  }
  static isCustomerLoggedIn(): boolean {
    if (this.getToken === null) {
      return false;
    }
    const role: string = this.getUserRole();
    return role == 'CUSTOMER';
  }
  static signOut():void{
    localStorage.removeItem(TOKEN);
    localStorage.removeItem(USER);
  }
}

