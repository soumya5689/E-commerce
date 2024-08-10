import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrderByStatusComponent } from './order-by-status.component';

describe('OrderByStatusComponent', () => {
  let component: OrderByStatusComponent;
  let fixture: ComponentFixture<OrderByStatusComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OrderByStatusComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(OrderByStatusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
