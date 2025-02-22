import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-shape-renderer',
  imports: [CommonModule],
  templateUrl: './shape-renderer.component.html',
  styleUrl: './shape-renderer.component.css',
})
export class ShapeRendererComponent {
  @Input() content: string = ''; // SVG path data
  @Input() x: number = 0;
  @Input() y: number = 0;
  @Input() width: number = 0;
  @Input() height: number = 0;
  @Input() fillColor: string = '';
  @Input() strokeColor: string = '';
  @Input() strokeWidth: number = 0;
}
