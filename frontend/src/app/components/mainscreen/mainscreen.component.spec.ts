import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MainscreenComponent } from './mainscreen.component';

describe('MainscreenComponent', () => {
  let component: MainscreenComponent;
  let fixture: ComponentFixture<MainscreenComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MainscreenComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(MainscreenComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
