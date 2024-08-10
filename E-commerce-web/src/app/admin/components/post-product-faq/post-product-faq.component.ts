import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { AdminService } from '../../service/admin.service';

@Component({
  selector: 'app-post-product-faq',
  templateUrl: './post-product-faq.component.html',
  styleUrl: './post-product-faq.component.scss'
})
export class PostProductFaqComponent {

  productId:number = this.activatedRoute.snapshot.params["productId"];
  FAQForm! :FormGroup;

  constructor(private fb: FormBuilder,
    private router: Router,
    private snackbar: MatSnackBar,
    private adminService: AdminService,
    private activatedRoute: ActivatedRoute) { }

    ngOnInit(){
      this.FAQForm = this.fb.group({
        question:[null,[Validators.required]],
        answer:[null,[Validators.required]],  
      })
    }

    postFAQ(){
      this.adminService.postFAQ(this.productId,this.FAQForm.value).subscribe(res=>{
        if(res.id!=null){
          this.snackbar.open('FAQ posted successfully','Close',{
            duration:5000
          });
          this.router.navigateByUrl('admin/dashboard');
        }else{
          this.snackbar.open("something went wrong !!",'Close',{
            duration:5000,
            panelClass:'error-snackbar'
          })
        }
      })
    }

}
