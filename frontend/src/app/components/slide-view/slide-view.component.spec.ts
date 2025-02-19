import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SlideViewComponent } from './slide-view.component';

describe('SlideViewComponent', () => {
  let component: SlideViewComponent;
  let fixture: ComponentFixture<SlideViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SlideViewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SlideViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
