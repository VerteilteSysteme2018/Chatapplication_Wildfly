import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TraceDBComponent } from './trace-db.component';

describe('TraceDBComponent', () => {
  let component: TraceDBComponent;
  let fixture: ComponentFixture<TraceDBComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TraceDBComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TraceDBComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
