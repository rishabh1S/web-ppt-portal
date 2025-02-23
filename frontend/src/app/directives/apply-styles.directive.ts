import {
  Directive,
  ElementRef,
  Input,
  Renderer2,
  OnChanges,
} from '@angular/core';

@Directive({
  selector: '[applyStyles]',
})
export class ApplyStylesDirective implements OnChanges {
  @Input() applyStyles: { [key: string]: any } = {};

  constructor(private el: ElementRef, private renderer: Renderer2) {}

  ngOnChanges() {
    if (this.applyStyles) {
      // Optionally clear existing styles, if necessary
      this.el.nativeElement.style.cssText = '';
      Object.entries(this.applyStyles).forEach(([key, value]) => {
        if (value != null) {
          const cssKey = key === 'fontColor' ? 'color' : key;
          this.renderer.setStyle(this.el.nativeElement, cssKey, value);
        }
      });
    }
  }
}
