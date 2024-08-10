import { Component } from '@angular/core';
import { AdminService } from '../../service/admin.service';

@Component({
  selector: 'app-analytics',
  templateUrl: './analytics.component.html',
  styleUrl: './analytics.component.scss'
})
export class AnalyticsComponent {

  data:any;

  constructor(private adminService: AdminService){}

  ngOnInit(){
    this.adminService.getAnalytics().subscribe(res=>{
      console.log(res);
      this.data=res;
    })
  }
}
