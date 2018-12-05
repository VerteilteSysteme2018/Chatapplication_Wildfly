import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminClHomeComponent } from './admin-cl-home.component';

describe('AdminClHomeComponent', () => {
  let component: AdminClHomeComponent;
  let fixture: ComponentFixture<AdminClHomeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AdminClHomeComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminClHomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
