import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { SignupComponent } from './signup/signup.component';
import { TrackOrderComponent } from './track-order/track-order.component';

const routes: Routes = [{ path: "login", component: LoginComponent },
{ path: "signup", component: SignupComponent },
{ path: "order", component: TrackOrderComponent },
{ path: 'customer', loadChildren: () => import('./customer/customer.module').then(m => m.CustomerModule) }, 
{ path: 'admin', loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule) }];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
