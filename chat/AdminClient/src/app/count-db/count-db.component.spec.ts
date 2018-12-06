import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CountDBComponent } from './count-db.component';

describe('CountDBComponent', () => {
  let component: CountDBComponent;
  let fixture: ComponentFixture<CountDBComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CountDBComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CountDBComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
