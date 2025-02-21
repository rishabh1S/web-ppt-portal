import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-shape-renderer',
  imports: [CommonModule],
  templateUrl: './shape-renderer.component.html',
  styleUrl: './shape-renderer.component.css',
})
export class ShapeRendererComponent {
  @Input() type: string = ''; // Shape type (e.g., RECT, ELLIPSE)
  @Input() x: number = 0; // X position (%)
  @Input() y: number = 0; // Y position (%)
  @Input() width: number = 0; // Width (%)
  @Input() height: number = 0; // Height (%)
  @Input() fillColor: string = ''; // Fill color
  @Input() strokeColor: string = ''; // Stroke color
  @Input() strokeWidth: number = 0; // Stroke width
}
