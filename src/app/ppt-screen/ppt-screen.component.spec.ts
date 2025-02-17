import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PptScreenComponent } from './ppt-screen.component';

describe('PptScreenComponent', () => {
  let component: PptScreenComponent;
  let fixture: ComponentFixture<PptScreenComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PptScreenComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PptScreenComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
